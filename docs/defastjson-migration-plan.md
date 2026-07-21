# 去 fastjson 化迁移计划（内部迁移至 gson，公开 API 保持 JSON 库无关）

- 状态：已实施（P0–P7 完成，迁移文档见 `docs/migrations/0.7.0/`；0.6.x 收尾版待另行发布）
- 日期：2026-07-21
- 目标版本：`0.6.x` 收尾版（deprecation）→ `0.7.0`（breaking，移除 fastjson）

---

## 1. 背景与目标

- fastjson 1.x 已于 2024-10-23 归档（read-only），事实 EOL；1.x 最后版本 1.2.83 为 2022 年安全修复版（CVE-2022-25845，autotype 绕过）。本 SDK 当前依赖 `com.alibaba:fastjson:1.2.83_noneautotype`，且 fastjson 类型大量暴露在公开 API 签名中。
- 目标：
  1. **公开 API 与 JSON 库无关**：public/protected 签名中不出现任何具体 JSON 库类型（fastjson、gson 都不出现）；
  2. **内部实现迁移到 gson**（2.14.x，Java 8+，发布活跃，无 autotype 类结构性安全风险）；
  3. 未来再换 JSON 库时对用户零 breaking。

## 2. 现状：fastjson 暴露面分类

统计基线：`import com.alibaba.fastjson` 共 259 个文件（main 227 + test 32）。主 jar 未 relocate（shade 仅产出附加 classifier `jar-with-dependencies`），fastjson 类型对用户真实暴露。

| 类别 | 形式 | 规模 | 性质 |
|---|---|---|---|
| A | public 方法返回 `JSONObject`/`JSONArray`（`ToJsonObject`/`ToRequestJson`/`marshal` 等，如 `LogStore.ToRequestJson()`、`Index.ToJsonObject()`） | ~55 方法 | 破坏性 |
| B | public 方法/构造器参数接受 fastjson 类型（`FromJsonObject`/`deserialize`、Response 构造器如 `GetContextLogsResponse(Map, JSONObject)`） | ~100+ | 破坏性 |
| C | 继承/实现 fastjson 类型：13 个 enum `implements JSONSerializable`；`ShipperConfig` 接口（`GetJsonObj`/`FromJsonObj`）；`JobConfiguration.deserialize(JSONObject)` 抽象方法；`Unmarshaller<T>`（internal 包） | 16 | 破坏性（扩展点） |
| D | getter/setter 暴露 `JSONObject` 字段：`LogException.accessDeniedDetail`、`EtlMeta.metaValue`、`Advanced.others` | 3 | 破坏性，真·动态 JSON 场景 |
| E | 注解与自定义序列化器：`@JSONField` ~281 处（~30 文件）、`@JSONType` 3 处、`ObjectSerializer` ~10 个类（含 `ToGeneralSerializer`、`DateToUnixTimestampSerializer`） | ~30 文件 | 内部可替换 |
| F | 纯内部使用（request/ 包大部分、auth、Client 方法体内） | ~100-120 文件 | 无破坏 |
| G | `Feature.DisableSpecialKeyDetect` 8 处（Client.java ×4、LogException、Index、QueryResult） | 8 | 技术点，gson 天然无 `$ref`/特殊 key 问题 |

其他事实：`LogService` 接口 0 暴露；`Client` public 暴露仅 1 个（`ExtractLogtailProfile`），protected 暴露 ~22 个 `Extract*` 方法；`SerializerFeature`/`JSONPath`/`TypeReference` 均未使用。

## 3. 决策记录

| # | 决策项 | 结论 |
|---|---|---|
| 1 | 兼容策略 | **一次性 breaking release**，彻底移除 fastjson 依赖，不做双轨过渡 |
| 2 | ~155 个 model 序列化方法的去向 | **从 public API 移除，序列化逻辑收敛到内部**；已有 String 入口（如 `Index.FromJsonString(String)`）保留；确有用户场景的类保留 `toJsonString/fromJsonString` |
| 3 | 三个动态 JSON 字段的类型 | **`Map<String,Object>`**（纯 JDK 类型，配内部 normalizer 保证无 gson 运行时类；`accessDeniedDetail` 可同时提供 raw String getter） |
| 4 | 服务端新字段透传 | **Response 基类加 `getRawResponseBody()`** 只读透传；**不做**回写保真（unknownFields catch-all）；仅明确动态的字段支持动态访问 |
| 5 | 内部迁移策略 | **等价机械翻译**（fastjson JSONObject → gson JsonObject，逻辑不改）；gson 自动绑定 POJO 重构记入后续 TODO，fastjson 移除后分批做 |
| 6 | 验证策略 | 中间 commit 新旧双路径**对拍测试** + functiontest 核心场景回归（functiontest 可跑通，需提供环境配置） |
| 7 | 扩展点处理 | **全部收编为内部实现**：`ShipperConfig`/`JobConfiguration` 移除 JSON 方法；`Client.Extract*` 降为 private；`Unmarshaller` 改 gson 签名；迁移指南单独立章节警示 |
| 8 | gson 引入形式 | **普通 compile 依赖**（gson 2.14.x）+ 在 `jar-with-dependencies` classifier 中新增 `com.google.gson` relocation；主 jar 不 shade |
| 9 | 版本与节奏 | **先发 `0.6.x` 收尾版**（全量 `@Deprecated` + CHANGELOG 预告）→ **`0.7.0`** 完成迁移；不进 1.0；必须配 `MIGRATION.md` 迁移指南 |

## 4. 业界调研：JSON 库无关设计先例（已联网核实，2026-07）

| 案例 | 隔离策略 | 静态 model | 动态 JSON 类型 | 底层库对用户可见? |
|---|---|---|---|---|
| AWS SDK v2 | shade+relocate jackson-core（databind 彻底移除） | codegen 纯 POJO + 内部 marshaller | 自研 `Document` 不可变树 | 否 |
| Azure SDK | 自研 `azure-json` 抽象（`JsonSerializable`/`JsonReader`/`JsonWriter` + SPI），默认实现 vendor 了 jackson-core 源码 | model 手写 `toJson(JsonWriter)`/`fromJson(JsonReader)` | `readUntyped()` → 原生 Map/List | 否 |
| Elasticsearch Java Client | jakarta.json 规范 + `JsonpMapper` 抽象，可插拔 Jackson/JSON-B | codegen `JsonpSerializable` | 自研 `JsonData`（延迟绑定 raw JSON） | 仅规范 API |
| MongoDB Driver | 完全自研 bson，零第三方 JSON 依赖 | `Codec`/`CodecRegistry` | 自研 `Document`(宽松 Map) + `BsonDocument`(严格树) | 否 |
| Retrofit | `Converter.Factory` SPI，核心零 JSON 依赖 | 下放给用户选的 converter | 无 | 用户自选 |
| **阿里云 tea / SDK V2.0** | **普通依赖 gson 2.11（不 shade、不抽象）** | `TeaModel` POJO + `@NameInMap` 反射 | `Map<String,?>` / `Object` | gson 为传递依赖可见，但 API 签名不暴露 |
| Google HTTP Client | 自研 `JsonFactory` 流式抽象（Gson/Jackson 工厂） | `@Key` 注解反射 | `GenericJson`(implements Map) | 否 |

**结论与启示**：
1. "public API 零 JSON 库类型"是全行业共识，七个案例无一例外。
2. 内部用 gson 有同门官方先例：阿里云 tea/V2.0 SDK 内部就是 gson。gson 处"维护模式"但发布活跃（2.14.0，2026-04），API 稳定反而是 SDK 底层依赖的优点。
3. 隔离强度三档：shade（AWS，最强）> 自研抽象+SPI（Azure/ES）> 普通依赖但签名不暴露（tea，本次采用）。本次选最轻档，与同门一致；若日后用户侧 gson 冲突频发，可升级到 shade 档，因 API 已无暴露，升级对用户零感知。
4. 动态 JSON 两条路：自研小树类型（AWS `Document`）或 `Map<String,Object>`（Azure/tea）。本 SDK 动态场景仅 3 个字段 + 只读透传，选 `Map` + raw String，不自研树类型。
5. Azure 放弃直接依赖 Jackson 的首要原因是"用户 classpath 版本冲突导致运行时错误"——这是论证"API 不暴露 + 依赖可替换"价值的最佳业界引文。

关键来源：AWS 官方博客 *The AWS SDK for Java 2.17 removes its external dependency on Jackson*；Azure 官方博客 *Replacing jackson-databind with azure-json and azure-xml*；`co.elastic.clients.json.JsonpMapper`/`JsonData` Javadoc；`aliyun/tea-java` pom.xml；`google/gson` README/Releases；`alibaba/fastjson` 归档仓库。

## 5. 目标 API 设计要点（0.7.0）

### 5.1 公开 API 变化
- 移除所有 fastjson import 与类型；model 类成为纯 POJO（getter/setter/builder 不变）。
- A/B 类方法（`ToJsonObject`/`ToRequestJson`/`FromJsonObject`/`deserialize`/`marshal`）从 public API 删除，逻辑移入内部 serializer（`com.aliyun.openservices.log.internal.json` 包，package-private 或明确标注 internal）。
- 保留/新增的 String 入口：`Index.FromJsonString(String)` 等既有方法保留；对用户确有落盘/GitOps 场景的配置类（Dashboard、Alert、Index、LogStore 等）提供 `toJsonString()`/`fromJsonString(String)`。
- 动态字段：`EtlMeta.metaValue`、`Advanced.others`、`LogException.accessDeniedDetail` 改为 `Map<String,Object>`；`LogException` 另加 `getAccessDeniedDetailRaw()`（String）。
- 透传：`Response` 基类新增 `String getRawResponseBody()`（GetLogs 等大响应可评估是否豁免/可配置）。
- 扩展点收编：13 个 enum 去掉 `implements JSONSerializable`；`ShipperConfig` 移除 `GetJsonObj`/`FromJsonObj`；`JobConfiguration` 移除 `deserialize(JSONObject)` 抽象方法；`Client` 的 ~22 个 `protected Extract*` 降为 private；`Unmarshaller<T>` 改用 gson 类型（internal 包）。

### 5.2 内部 gson 基建（必做项）
统一内部 `Gson` 单例（如 `internal/json/GsonHolder`），强制配置：
- `disableHtmlEscaping()` —— gson 默认转义 `<` `>` `=` `&` 为 `\u003c` 等；SLS query/正则/SPL 中这些字符普遍，不关会导致服务端收到的配置变形。
- `setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)` —— 避免整数反序列化成 `Double`（`ttl: 30` 变 `30.0`）。
- serializeNulls 保持默认关闭（与 fastjson 现状一致，`SerializerFeature` 现无使用）。
- 注册 TypeAdapter：`Date`→Unix 时间戳（替代 `DateToUnixTimestampSerializer`）；13 个 enum 的自定义序列化（替代 `JSONSerializable.write`）；`ToGeneralSerializer` 对应的 3 个 `@JSONType` 类。
- **TypeAdapter 集中注册，不在 model 上加 gson 注解**（`@SerializedName` 例外，见 6 阶段 P4）——尽量把 gson 痕迹压缩在 internal 包内。
- `JsonTreeConverter`（normalizer，~30 行）：`JsonElement` → 纯 `LinkedHashMap`/`ArrayList`/`String`/`Long`/`Double`/`Boolean`，保证交给用户的 `Map` 无 gson 运行时类。
- `DisableSpecialKeyDetect` 的 8 处无需等价物：gson 不特殊处理 `$ref`/含 `.` 的 key，直接删除即可（对拍测试覆盖含特殊 key 的样本以确认）。

### 5.3 机械翻译对照表
| fastjson | gson |
|---|---|
| `JSONObject.parseObject(s, Feature.DisableSpecialKeyDetect)` | `JsonParser.parseString(s).getAsJsonObject()` |
| `obj.put(k, v)` | `obj.addProperty(k, v)` / `obj.add(k, elem)` |
| `obj.getString(k)` / `getIntValue` / `getBooleanValue` | `obj.get(k).getAsString()` 等（注意 null 处理差异，见 §8） |
| `obj.containsKey(k)` | `obj.has(k)` |
| `JSON.toJSONString(o, SERIALIZE_CONFIG)` | `GsonHolder.gson().toJson(o)` |
| `@JSONField(name="x")` | `@SerializedName("x")` |
| `@JSONField(serialize=false)`（方法上，4 处） | 无需处理（gson 只序列化字段，天然忽略方法） |
| `@JSONType(serializer=...)` | `registerTypeAdapter` |
| `implements JSONSerializable` + `ObjectSerializer` | `JsonSerializer<T>`/`TypeAdapter<T>` 集中注册 |

## 6. 分阶段实施（PR 拆分）

> P1–P5 期间 fastjson 与 gson 并存，每个 PR 独立可编译、可跑测试；P6 一刀切移除。

- **P0 基建**：引入 gson 依赖；`GsonHolder` + 全部 TypeAdapter + `JsonTreeConverter`；pom shade 加 gson relocation。纯新增，零风险。
- **P1 对拍测试基建**：对拍工具（同一 model 分别走 fastjson/gson 路径，断言 JSON 语义相等——key 集合+值比对，容忍顺序差异）；为高风险 model（AlertConfiguration、Index、LogStore、ETLConfiguration、Job* 系列）先建满字段样本；反序列化侧收集/构造响应体样本（含特殊 key、中文、`<>&=` 字符、大整数、null/缺省字段）。
- **P2 纯内部文件迁移**（F 类，~100-120 文件）：按包分批（`request/` → `common/auth/` → Client 方法体），每批一个 PR，机械翻译。
- **P3 model 序列化迁移**（A/B/E 类，~200 文件）：`common/` 按功能域分批（logstore/index → machine group/config → alert/job/etl → topostore/resource → 其余）；每批：翻译 + 对拍测试通过。此阶段方法签名暂改为 gson 类型或双路径并存（内部过渡态，不发版）。
- **P4 注解类迁移**（E 类）：`@JSONField`→`@SerializedName`（~281 处，可脚本化）+ 对拍验证。
- **P5 公开 API 手术**（0.7.0 分支）：删除 A/B 类 public 方法或转内部；扩展点收编（C 类）；动态字段改 `Map<String,Object>`（D 类）；`getRawResponseBody()`；test/sample 32+4 个文件同步迁移。
- **P6 移除 fastjson**：删依赖、删 `JsonUtils` fastjson 残留、pom 去掉 fastjson relocation；全量单测 + functiontest 核心场景（logstore/index/alert/consumer group/put-get logs，**需环境配置**）。
- **P7 发布**：
  - `0.6.x` 收尾版（从 master 切，仅含 deprecation）：A/B/C/D 类全部 public 方法标 `@Deprecated` + javadoc 注明 0.7.0 替代方式；CHANGELOG 预告。
  - `0.7.0`：合入 P0–P6 + `MIGRATION.md`。

## 7. MIGRATION.md 大纲（随 0.7.0 发布）

1. 迁移背景（fastjson EOL、安全动机）与影响范围一句话总结。
2. 按类别的"移除方法 → 替代方式"对照表（A/B 类逐方法列出）。
3. 动态字段签名变化：`JSONObject` → `Map<String,Object>` 的用法示例（读/写各一段代码）。
4. **扩展点警示章节**：自定义 `ShipperConfig` 实现、继承 `Client` 重写 `Extract*`、依赖 enum `JSONSerializable` 的用户如何迁移。
5. 新能力：`getRawResponseBody()` 透传用法。
6. 行为差异说明：数字类型（Long/Double 策略）、字符转义、错误消息文案变化（parse 异常来自 gson）。
7. 保留的 String 入口清单（`fromJsonString`/`toJsonString`）。

## 8. 风险与注意事项

| 风险 | 缓解 |
|---|---|
| gson HTML 转义改变请求体（`<>&=` → `\u003c`） | `disableHtmlEscaping()` 强制项 + 对拍样本覆盖 |
| 整数变 Double（`30` → `30.0`） | `LONG_OR_DOUBLE` 强制项 + 对拍断言数字字面量 |
| fastjson `getString(missing)` 返回 null 不抛异常，gson `get(missing)` 返回 null 但 `.getAsString()` NPE | 机械翻译时统一用带缺省的辅助方法（`JsonUtils` gson 版提供 `readOptionalString` 等，沿用现有模式） |
| fastjson 手动代码中 `containsKey` 的微妙缺省值语义被改变 | 机械翻译"逻辑一行不改"纪律 + 对拍缺省字段样本 |
| 大整数/科学计数法精度差异 | 对拍样本覆盖 `Long.MAX_VALUE`、小数 |
| functiontest 覆盖不全 | 核心场景清单人工确认 + 对拍测试兜底 |
| 下游存在未知的 `ToJsonObject()`/扩展点使用者 | 0.6.x deprecation 版提供编译期警告缓冲；集团内代码扫描 + 主动通知（待办） |

## 9. 后续 TODO（0.7.0 之后，独立排期）

- [ ] 手动 JsonObject 构建代码分批重构为 gson 自动绑定 POJO（问题 5 决策的方案 B），逐域进行、对拍保护。
- [ ] 评估将 gson 升级为主 jar shade/relocate（AWS 模式），彻底消除传递依赖冲突——API 已无暴露，届时对用户零感知。
- [ ] `0.6.x` 分支维护策略（是否只做安全修复、维护时长）内部确认。
- [ ] 集团内下游对 deprecated API 的使用扫描与通知。
