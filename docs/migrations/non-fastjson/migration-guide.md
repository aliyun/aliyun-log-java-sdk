# aliyun-log Java SDK non-fastjson 版本迁移指南

> English version: [migration-guide_EN.md](migration-guide_EN.md)
>
> 对外接口变更清单：[breaking-changes.md](breaking-changes.md)

## 1. 版本选择

迁移期间会并行维护两条版本线：

| Maven 版本 | fastjson 依赖 | 适用场景 |
|---|---|---|
| `0.6.x` | 保留 | 尚未开始迁移的应用 |
| `0.6.x-non-fastjson` | 移除 | 已完成本文检查和改造的应用 |

例如，当前推荐使用 `0.6.161-non-fastjson.2`。`-non-fastjson` 是正式版本号的一部分，不是 classifier，也不是 SNAPSHOT。Maven 会认为 `0.6.161-non-fastjson.2` 高于 `0.6.161`，但应用仍应显式指定要使用的版本。迁移完成前，不带后缀的版本仍会继续维护一段时间。

同一个应用只能选择其中一条版本线。不要在同一依赖图中同时引入 `0.6.161` 和 `0.6.161-non-fastjson.2`，否则 Maven 最终只会仲裁出其中一个版本，而依赖它们的代码可能按另一套 API 编译。

## 2. 修改 Maven 依赖

普通 JAR 只需要修改版本号：

```xml
<!-- 修改前：保留 fastjson 的版本线 -->
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.161</version>
</dependency>
```

```xml
<!-- 修改后：已移除 fastjson 的版本线 -->
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.161-non-fastjson.2</version>
</dependency>
```

如果原来使用 fat JAR，classifier 保持 `jar-with-dependencies`：

```xml
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.161-non-fastjson.2</version>
    <classifier>jar-with-dependencies</classifier>
</dependency>
```

对应文件名是 `aliyun-log-0.6.161-non-fastjson.2-jar-with-dependencies.jar`。

## 3. 升级前检查

在应用仓库执行以下检查：

```bash
# 应用是否直接使用 fastjson
rg "com\.alibaba\.fastjson" --glob '*.java'

# 应用是否依赖 SDK 传递引入 fastjson
mvn dependency:tree -Dincludes=com.alibaba:fastjson

# 应用是否使用 SDK 的内部接口
rg "com\.aliyun\.openservices\.log\.internal|InternalApi" --glob '*.java'

# 应用是否实现或继承了受影响的扩展点
rg "implements ShipperConfig|extends JobConfiguration|extends Client\b|implements Unmarshaller" --glob '*.java'
```

判断方式：

- 如果以上检查均无命中，通常只需修改 Maven 版本并重新编译、运行测试。
- 如果 fastjson 只用于应用自己的 JSON 处理，请在应用中显式声明所需 JSON 依赖；SDK 不再传递提供 fastjson。
- 如果 fastjson 类型用于调用 SDK，按下一节修改。
- 如果使用了 SDK `internal` 包或自定义扩展点，请参照[对外接口变更清单](breaking-changes.md)逐项检查。

## 4. 常见代码迁移

### 4.1 不再通过 fastjson 对象与 SDK 交换数据

优先使用 SDK model 的 getter/setter；需要完整 JSON 时使用 String 接口，再由应用选择的 JSON 库处理。

```java
// 修改前
com.alibaba.fastjson.JSONObject json = index.toJsonObject();
String jsonText = json.toJSONString();
```

```java
// 修改后
String jsonText = index.toJsonString();
```

向 SDK model 导入 JSON 时同样使用 String 接口：

```java
// 修改前
com.alibaba.fastjson.JSONObject json =
        com.alibaba.fastjson.JSON.parseObject(jsonText);
config.fromJsonObject(json);

// 修改后
config.fromJsonString(jsonText);
```

不要把 `com.aliyun.openservices.log.internal.*` 作为 fastjson 的替代类型引入业务代码。这些类型不是面向应用的兼容接口。

### 4.2 动态 JSON 字段改用 `Map<String, Object>`

以下对外字段不再返回或接收 fastjson `JSONObject`：

- `LogException.getAccessDeniedDetail()` / `setAccessDeniedDetail(...)`
- `EtlMeta.getMetaValue()` / `setMetaValue(...)`
- `Advanced.getOthers()` / `setOthers(...)`

代表性改法：

```java
// 修改前
com.alibaba.fastjson.JSONObject others = new com.alibaba.fastjson.JSONObject();
others.put("tail_size_kb", 100);
advanced.setOthers(others);

// 修改后
Map<String, Object> others = new LinkedHashMap<String, Object>();
others.put("tail_size_kb", 100);
advanced.setOthers(others);
```

嵌套对象和数组分别使用 `Map<String, Object>` 与 `List<Object>`。如需读取访问拒绝详情的原始内容，可使用 `LogException.getAccessDeniedDetailRaw()`。

### 4.3 应用仍需使用 fastjson

如果 fastjson 只服务于应用自身，可以继续使用，但需要由应用显式声明版本：

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>应用自行选择并维护的版本</version>
</dependency>
```

不要继续依赖 SDK 间接引入 fastjson，也不要用 fastjson 直接序列化 SDK model；请使用 SDK 的 getter/setter 或 `toJsonString()` / `fromJsonString(...)`。

### 4.4 自定义扩展

如果应用自行实现了 `ShipperConfig`、继承了 `JobConfiguration` 或 `Client`，相关 JSON 方法的参数类型和部分方法名已经变化。这些扩展点不建议在新代码中继续使用；已有实现请根据编译错误完成适配，并把相关代码隔离在单独的 adapter 中。具体名称见[对外接口变更清单](breaking-changes.md#4-自定义实现和继承)。

## 5. 需要关注的行为变化

- JSON 输出的 key 顺序可能变化。JSON 语义不变，但不要逐字比较 JSON 字符串。
- `LogItem.GetLogContents()` 现在保留服务端返回顺序。不要按固定下标取特定字段，应按 key 查找。
- 输入必须是标准 JSON：键和字符串使用双引号，不要使用尾随逗号、未加引号的 key 或空字符串。
- SDK 解析失败不再抛出 fastjson 的 `JSONException`。按 SDK 方法声明处理 `LogException` 等异常，不要匹配异常消息文本。
- `null` 字段仍会省略，不会以显式 `null` 发送到服务端。

逐字比较 JSON 的测试可改为语义比较：

```java
// 修改前
assertEquals(expectedJson, config.toJsonString());

// 修改后：assertJsonEquals 代表应用自己的 JSON 语义比较方法
assertJsonEquals(expectedJson, config.toJsonString());
```

按 key 读取日志字段：

```java
String topic = null;
for (LogContent content : item.GetLogContents()) {
    if ("__topic__".equals(content.GetKey())) {
        topic = content.GetValue();
        break;
    }
}
```

## 6. 升级后验证

建议至少完成以下验证：

1. 执行 `mvn dependency:tree`，确认只存在一个 `com.aliyun.openservices:aliyun-log` 版本，且版本带 `-non-fastjson`。
2. 再次检查依赖树，确认应用没有无意中依赖 SDK 提供 fastjson。
3. 重新编译全部模块，重点处理 fastjson 类型、旧 JSON 方法名和自定义扩展点的编译错误。
4. 运行单元测试和集成测试，重点覆盖配置创建/更新、Job、Alert、Shipper、GetLogs 和异常处理。
5. 对应用生成的请求 JSON 做语义比较，确认字段和值符合预期，同时确认可选空值没有被发送。

如果升级后需要临时回退，只需恢复成配对的不带后缀版本，例如从 `0.6.161-non-fastjson.2` 恢复为 `0.6.161`；不要同时保留两个版本。
