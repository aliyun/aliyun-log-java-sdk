# 0.6.x → 0.7.0 迁移指南：移除 fastjson

> English version: [migration-guide_EN.md](migration-guide_EN.md)
> 完整 API 变更清单（精确到类/方法）: [breaking-changes.md](breaking-changes.md)

## 1. 背景

自 **0.7.0** 起，SDK 彻底移除对 `com.alibaba:fastjson` 的依赖：

- fastjson 1.x 已于 2024-10 归档（事实 EOL），历史上存在 autotype 反序列化漏洞（如
  CVE-2022-25845）。本 SDK 此前依赖 `1.2.83_noneautotype` 安全修复版，但上游已无后续维护。
- 0.6.x 的公开 API 大量暴露 fastjson 类型（`ToJsonObject()` 返回
  `com.alibaba.fastjson.JSONObject` 等），导致 SDK 无法在不 break 用户的前提下更换 JSON 库。

0.7.0 的设计目标：

1. **公开 API 与 JSON 库无关**——签名中只出现 JDK 类型（`String`、`Map<String, Object>`
   等）或 SDK 自有类型，不出现任何第三方 JSON 库类型（fastjson、gson 都不出现）。
2. 内部实现迁移至 gson（`com.google.code.gson:gson:2.13.2`）。gson 仅作为传递依赖存在，
   且在 `jar-with-dependencies` classifier 中已 relocate 到
   `com.aliyun.log.thirdparty.com.google.gson`，不会与你的 classpath 冲突。
3. 未来 SDK 再更换 JSON 库时，对用户零 breaking。

## 2. 我是否受影响？（快速自检）

在你的项目里执行：

```bash
# 检查 1：是否直接使用 fastjson 类型与本 SDK 交互
grep -rn "com.alibaba.fastjson" --include="*.java" src/ | grep -v "^Binary"

# 检查 2：是否依赖 SDK 传递的 fastjson（自己没声明但代码里用了）
mvn dependency:tree -Dincludes=com.alibaba:fastjson

# 检查 3：是否继承了 SDK 扩展点
grep -rn "implements ShipperConfig\|extends JobConfiguration\|extends Client\b\|implements Unmarshaller" --include="*.java" src/
```

| 检查结果 | 结论 |
|---|---|
| 三项全空 | **无需修改代码**，直接升级。若测试中有 JSON 字符串逐字对比，见 §5 行为差异。 |
| 检查 1/2 命中，但 fastjson 只用于你自己的业务 JSON | 在你的 pom 里**显式声明 fastjson 依赖**（不再从 SDK 传递获得），SDK 相关代码不用改。 |
| 检查 1 命中且 fastjson 类型与 SDK API 交互（如 `JSONObject obj = index.ToJsonObject()`） | 按 §4 逐类迁移。 |
| 检查 3 命中 | 按 §4.6 扩展点迁移。 |

## 3. 影响面总览

完整清单（精确到每个类和方法签名）见 [breaking-changes.md](breaking-changes.md)。分类摘要：

| 类别 | 规模 | 破坏性 |
|---|---|---|
| A. `ToJsonObject()`/`FromJsonObject(...)`/`deserialize(...)`/`marshal(...)` 等方法的 `JSONObject`/`JSONArray` 类型从 fastjson 换成 SDK 内部 shim 类型 | common/response/request 约 130 个类，见清单 §2 | 编译错误（仅当你的代码显式引用 fastjson 类型接收/传参） |
| B. 动态 JSON 字段改为 `Map<String, Object>`：`LogException.accessDeniedDetail`、`EtlMeta.metaValue`、`Advanced.others` | 3 个字段，见清单 §3 | 编译错误 |
| C. 13 个 enum 不再 `implements JSONSerializable`；`ToGeneralSerializer` 删除；`@JSONField`/`@JSONType` 注解移除 | 见清单 §4、§5 | 仅当你依赖这些 fastjson 集成点 |
| D. 扩展点签名变化：`ShipperConfig`、`JobConfiguration`、`Unmarshaller`、`Client.Extract*`（`ExtractLogtailProfile` 由 public 降为 protected） | 见清单 §6 | 自定义实现/子类需要改 import |
| E. 异常类型：解析失败不再抛 `com.alibaba.fastjson.JSONException` | 见清单 §8 | 仅当你 catch 了 fastjson 异常 |
| F. 行为差异：JSON key 顺序、`GetLogContents()` 顺序等 | 见清单 §9 | 运行期，仅影响依赖顺序/文案的代码 |

## 4. 迁移指南（按场景）

### 4.1 场景：接收 SDK 返回的 JSONObject

```java
// 0.6.x
com.alibaba.fastjson.JSONObject obj = index.ToJsonObject();
String ttl = obj.getString("ttl");
```

**推荐改法**（不依赖任何 JSON 库类型，长期稳定）：走 String 入口。

```java
// 0.7.0 推荐
String json = index.ToJsonString();          // 各 model 的 String 入口均保留
// 用你自己选择的 JSON 库解析 json
```

**最小改法**（仅改 import）：shim 类型的取值方法与 fastjson 同名同语义。

```java
// 0.7.0 最小修改
com.aliyun.openservices.log.internal.json.JSONObject obj = index.ToJsonObject();
String ttl = obj.getString("ttl");           // 语义与 fastjson 一致
```

注意：`internal` 包仅供 SDK 内部使用，不承诺跨版本兼容；shim 类型
`extends LinkedHashMap<String, Object>`，只读场景可直接按 `Map<String, Object>` 接收：

```java
Map<String, Object> obj = index.ToJsonObject();   // 也可以
```

### 4.2 场景：向 SDK 传入 JSONObject

```java
// 0.6.x
com.alibaba.fastjson.JSONObject dict = com.alibaba.fastjson.JSON.parseObject(text);
config.FromJsonObject(dict);
```

**推荐改法**：用 String 入口。

```java
// 0.7.0 推荐
config.FromJsonString(text);
```

**最小改法**：换成 shim 类型。

```java
// 0.7.0 最小修改
import com.aliyun.openservices.log.internal.json.JSONObject;
config.FromJsonObject(JSONObject.parseObject(text));
```

### 4.3 场景：三个动态字段（`Map<String, Object>`）

```java
// 0.6.x
com.alibaba.fastjson.JSONObject detail = logException.getAccessDeniedDetail();
com.alibaba.fastjson.JSONObject meta = etlMeta.getMetaValue();
advanced.setOthers(someFastjsonObject);

// 0.7.0
Map<String, Object> detail = logException.getAccessDeniedDetail();
Object policyType = detail == null ? null : detail.get("PolicyType");
String rawDetail = logException.getAccessDeniedDetailRaw();   // 新增：原始 JSON 字符串

Map<String, Object> meta = etlMeta.getMetaValue();

Map<String, Object> others = new LinkedHashMap<String, Object>();
others.put("tail_size_kb", 100);
advanced.setOthers(others);
```

Map 中的值均为纯 JDK 类型（`String` / `Integer` / `Long` / `BigInteger` / `BigDecimal` /
`Boolean` / `Map` / `List`），不含任何 gson 运行时类型。嵌套对象按 `Map<String, Object>`
向下取即可。

### 4.4 场景：依赖 SDK 传递的 fastjson 做自己的业务

SDK 不再传递 fastjson。在你的 pom 中显式声明：

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.83_noneautotype</version><!-- 或迁移到 fastjson2/其它库 -->
</dependency>
```

### 4.5 场景：catch 了 fastjson 异常

```java
// 0.6.x
try {
    index.FromJsonString(text);
} catch (com.alibaba.fastjson.JSONException e) { ... }

// 0.7.0 —— SDK 解析错误统一从 LogException 或
// com.aliyun.openservices.log.internal.json.JSONException（RuntimeException 子类）抛出。
// 不要依赖异常消息文案（现为 gson 文案）。
try {
    index.FromJsonString(text);
} catch (LogException e) { ... }
```

### 4.6 场景：扩展点（自定义实现 / 继承 SDK 类）

| 你做了什么 | 需要的修改 |
|---|---|
| 自定义 `ShipperConfig` 实现（`GetJsonObj()` / `FromJsonObj(...)`） | 方法名不变，把 import 从 `com.alibaba.fastjson.JSONObject` 改为 `com.aliyun.openservices.log.internal.json.JSONObject`，构造逻辑基本不变（shim 是 `Map` 子类，`put` 可用） |
| 自定义 `JobConfiguration` 子类（`deserialize(JSONObject)`） | 同上，改 import；取值方法（`getString` 等）语义一致 |
| 实现 `Unmarshaller<T>` | `unmarshal(JSONArray, int)` 参数改为 shim `JSONArray` |
| 继承 `Client` 并重写/调用 `Extract*` | 参数类型改为 shim 类型；`ExtractLogtailProfile` 已由 public 降为 protected，外部调用请改走 `GetLogtailProfile(...)` 公开 API |
| 把 13 个 enum 当作 fastjson `JSONSerializable` 使用 | 该接口已移除。序列化输出不变（仍是 `toString()` 值）；自行序列化时直接用 `enumValue.toString()` |
| 依赖 model 类上的 `@JSONField`/`@JSONType` 注解（例如自己用 fastjson 序列化 SDK model） | 注解已移除，不能再用 fastjson 直接序列化 SDK model。请改用 model 的 `ToJsonString()` / SDK 请求方法 |

### 4.7 场景：测试/代码中逐字对比 JSON 字符串

key 顺序从 fastjson 的「getter 字母序」变为 gson 的「字段声明序」，语义等价、字节不等价。
把逐字对比改为语义对比（任意 JSON 库均可）：

```java
// 反例（0.7.0 下会挂）
assertEquals(expectedJson, config.ToJsonString());

// 改为语义对比（示例：解析后对比 Map）
assertEquals(JSONObject.parseObject(expectedJson), JSONObject.parseObject(config.ToJsonString()));
```

### 4.8 场景：按下标访问 GetLogs 结果字段

`LogItem.GetLogContents()` 的顺序从 fastjson HashMap 的哈希序（巧合稳定）变为服务端返回的
原始顺序。依赖固定下标的代码需改为按 key 查找：

```java
// 反例（0.6.x 侥幸可用）
String topic = item.GetLogContents().get(0).GetValue();

// 0.7.0
String topic = null;
for (LogContent c : item.GetLogContents()) {
    if ("__topic__".equals(c.GetKey())) { topic = c.GetValue(); break; }
}
```

## 5. 行为差异说明

迁移经过逐 model 的新旧序列化对拍验证，以下为已知且有意保留的差异
（完整表格见 [breaking-changes.md §9](breaking-changes.md#9-behavior-differences--行为差异)）：

| 项 | 说明 |
|---|---|
| **JSON key 顺序** | fastjson 按 getter 字母序输出，gson 按字段声明序输出。JSON 语义等价，但**逐字符对比 JSON 字符串的代码/测试需要改为语义对比**（见 §4.7）。 |
| **GetLogs 结果字段顺序** | `LogItem.GetLogContents()` 的顺序从 fastjson 的 HashMap 哈希序变为服务端返回的原始顺序。**依赖固定下标取字段的代码需改为按 key 查找**（见 §4.8）。 |
| 数字类型 | 与 fastjson 对齐：整数按大小解析为 `Integer` / `Long` / `BigInteger`，小数为 `BigDecimal`，序列化保持字面量不变（`30` 不会变 `30.0`）。 |
| 字符转义 | 已关闭 gson 的 HTML 转义，`< > & =` 与中文均原样输出，与 fastjson 一致。 |
| 解析严格性 | 与 fastjson 对齐拒绝非法 JSON；字符串内的未转义控制字符仍然接受（fastjson 遗留行为）。 |
| 错误消息 | 解析失败的异常消息文案来自 gson，与 0.6.x 不同；请勿依赖异常消息内容做逻辑判断。 |
| `Date` | 仍序列化为 Unix 时间戳（秒）。 |
| null 字段 | 仍不输出（与 fastjson 默认一致）。 |

## 6. 新能力：`getRawResponseBody()`

`Response` 基类新增只读透传，用于读取 SDK model 尚未覆盖的服务端新增字段：

```java
String raw = resp.getRawResponseBody(); // 服务端原始 JSON；未透传的接口返回 null
```

当前透传的接口：`GetCursor`、`ProjectConsumerGroupGetCheckPoint`、
`ProjectConsumerGroupHeartBeat`；后续版本按需扩展。该能力为 best-effort。

## 7. 保留的 String 入口

各 model 既有的 `FromJsonString(String)` / `ToJsonString()` / `toJsonString()` 均保留且行为
不变（Index、LogStore、Config、Machine Group、Dashboard/Chart、ETL、Shipper、Job/Alert 等）。
落盘、GitOps、跨系统传递配置请统一使用这些 String 入口——这是唯一承诺长期稳定的 JSON
交互方式。

## 8. API 稳定性标注：`@InternalApi`

0.7.0 引入注解 `com.aliyun.openservices.log.annotation.InternalApi`，把签名中仍出现
`internal.json.JSONObject/JSONArray` 的 public/protected 成员显式标为 SDK 内部件：

- **语义**：仅 SDK 内部使用，**不承诺任何跨版本兼容**，业务代码不应调用。
- **典型位置**：model 的 `ToJsonObject()/FromJsonObject(...)`、`Client` 的 `Extract*`、
  response 的 `deserialize`/构造器、`JobConfiguration`/`Notification`/`DataSource`/`DataSink`
  的 `deserialize(JSONObject)` 模板方法及子类 override、`ShipperConfig` 接口、
  `util.JsonUtils`、`internal.json.*`。

使用建议：

- 业务代码看到 `@InternalApi` 成员，请改用 String 入口（§7）或 getter/setter。
- 若你继承 SDK 类并 override 了 `@InternalApi` 方法，升级时请关注 release notes。
- 仓库内有守卫测试（`ApiExposureGuardTest`）强制：任何暴露 internal JSON 类型的
  public/protected 成员必须带该注解，防止未来新增裸暴露。

## 9. 0.6.x 维护说明

- 0.6.x 最后一个收尾版本仅包含 deprecation 标注与本次变更预告，不再新增功能。
- 后续安全类修复视情况评估；建议尽快升级至 0.7.0。
