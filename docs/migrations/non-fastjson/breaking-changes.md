# non-fastjson 版本对外接口变更

> 中文迁移步骤：[migration-guide.md](migration-guide.md)
>
> English migration guide: [migration-guide_EN.md](migration-guide_EN.md)

本文只列出应用升级时可能感知到的变化。SDK 内部的 JSON 解析方式、辅助类和内部方法不属于对外兼容承诺，因此不在本文展开。

## 1. Maven 版本和依赖

| 项目 | 保留 fastjson 的版本线 | 移除 fastjson 的版本线 |
|---|---|---|
| 版本格式 | `0.6.x` | `0.6.x-non-fastjson` |
| 当前推荐版本 | `0.6.161` | `0.6.161-non-fastjson.2` |
| fastjson 传递依赖 | 保留 | 移除 |
| fat JAR classifier | `jar-with-dependencies` | `jar-with-dependencies` |

两条版本线会在迁移期并行维护。`-non-fastjson` 是正式 Maven 版本号的一部分，不是 classifier，也不会被识别为 SNAPSHOT。应用只能选择其中一个版本，不能把两个版本同时放入依赖图。

## 2. JSON 相关接口

### 2.1 不再使用 fastjson `JSONObject` / `JSONArray` 作为 SDK 交互接口

原来接收或返回 `com.alibaba.fastjson.JSONObject` / `JSONArray` 的 SDK 方法已经变化。应用应改用以下稳定入口：

- 优先使用 model 的 getter/setter。
- 需要完整 JSON 时，使用已有的 `toJsonString()` / `fromJsonString(String)`。
- 服务端返回中尚未建模的字段，可在支持的响应上使用 `Response.getRawResponseBody()`。

常见受影响类型如下；同一类型中的重复 JSON 辅助方法不再逐个列出：

| 功能 | 受影响的常见类型 |
|---|---|
| Project、LogStore 与索引 | `Project`、`LogStore`、`MetricStore`、`Index`、`IndexKey`、`IndexKeys`、`IndexLine`、`LinkStore`、`SubStore` |
| Logtail 配置 | `Config`、`ConfigInputDetail`、`ConfigOutputDetail`、`CommonConfigInputDetail`、`DelimiterConfigInputDetail`、`JsonConfigInputDetail`、`PluginLogConfigInputDetail`、`StreamLogConfigInputDetail`、`MachineGroup`、`MachineList` |
| Dashboard 与查询 | `Dashboard`、`Chart`、`Query`、`QueryResult`、`SavedSearch` |
| Job、ETL 与告警 | `Job`、`JobConfiguration`、`JobInstance`、`JobSchedule`、`Alert`、`AlertConfiguration`、`AuditJob`、`ETLV2`、`EtlJob`、`Ingestion`、`Export`、`Report`、`ScheduledJob`、`ScheduledSQL` |
| Scheduled SQL 参数 | `ScheduledSQLBaseParameters`、`ScheduledSQLParameters`、`Log2MetricParameters`、`Metric2MetricParameters`、`MetricDownSamplingConfig` |
| 数据源与数据目的 | `DataSource`、`DataSink`、`AliyunBSSSource`、`AliyunCloudMonitorSource`、`AliyunMaxComputeSource`、`AliyunOSSSource`、`JDBCSource`、`KafKaSource`、`AliyunADBSink`、`AliyunLOGSink`、`AliyunODPSSink`、`AliyunOSSSink`、`AliyunTSDBSink` |
| Shipper | `ShipperConfig`、`OdpsShipperConfig`、`OssShipperConfig`、`ShipperMigration`、`ShipperTask` |
| Resource 与 Topostore | `Resource`、`ResourceRecord`、`ResourceActionPolicy`、`ResourceAlertPolicy`、`ResourceContentTemplate`、`ResourceGlobalConfig`、`ResourceWebhookIntegration`、`Topostore`、`TopostoreNode`、`TopostoreRelation` |
| 响应对象 | `GetLogsResponse`、`GetLogsResponseV2`、`GetResourcePolicyResponse`、`GetContextLogsResponse`、`GetHistogramsResponse`、`GetProjectResponse`、`GetMetricsConfigResponse`、各类 Job/Get/List Response |

Alert 嵌套配置同样受影响，包括 `SeverityConfiguration`、`JoinConfiguration`、`TemplateConfiguration`、`ConditionConfiguration`、`Tag`、`GroupConfiguration`、`PolicyConfiguration`、`SinkEventStoreConfiguration`、`SinkCmsConfiguration` 和 `SinkAlerthubConfiguration`。此外还包括 `CnameConfiguration.Certificate`、`ShardingPolicy.ShardHash` 与 `ShardingPolicy.ShardGroup`。

是否受影响应以应用编译结果及以下搜索为准：

```bash
rg "com\.alibaba\.fastjson" --glob '*.java'
```

### 2.2 JSON 方法名称统一

如果应用直接调用过以下旧方法名，需要修改：

| 修改前 | 修改后 |
|---|---|
| `ToJsonObject()` | `toJsonObject()`；应用代码更推荐 `toJsonString()` |
| `ToJsonString()` | `toJsonString()` |
| `FromJsonObject(...)` | `fromJsonObject(...)`；应用代码更推荐 `fromJsonString(...)` |
| `FromJsonString(...)` | `fromJsonString(...)` |
| response/model `deserialize(...)` | `fromJsonObject(...)` |
| response factory `deserializeFrom(...)` | `fromResponse(...)` |
| `ShipperConfig.GetJsonObj()` | `toJsonObject()` |
| `ShipperConfig.FromJsonObj(...)` | `fromJsonObject(...)` |
| `ShipperConfig.GetShipperType()` | `getShipperType()` |

`GetLogsResponse`、`GetLogsResponseV2` 和 `GetResourcePolicyResponse` 的 response factory 均使用 `fromResponse(...)`。正常通过 `Client` 调用这些 API 的应用不需要直接调用 factory。

## 3. 动态字段类型变化

以下接口从 fastjson `JSONObject` 改为 JDK `Map<String, Object>`：

| 类型 | 受影响接口 |
|---|---|
| `LogException` | `getAccessDeniedDetail()`、`setAccessDeniedDetail(...)`；新增 `getAccessDeniedDetailRaw()` 获取原始 JSON |
| `EtlMeta` | `getMetaValue()`、`setMetaValue(...)` 以及相应构造参数 |
| `Advanced` | `getOthers()`、`setOthers(...)` |

Map 中的嵌套对象和数组分别表示为 `Map<String, Object>` 与 `List<Object>`。

## 4. 自定义实现和继承

大多数应用不会使用本节接口。如果存在以下自定义代码，需要在升级时重新编译并适配：

| 使用方式 | 变化 |
|---|---|
| 自定义 `ShipperConfig` | JSON 方法改为小驼峰命名，参数和返回值不再是 fastjson 类型 |
| 继承 `JobConfiguration` | `fromJsonObject(...)` 参数不再是 fastjson 类型 |
| 实现 `Unmarshaller<T>` | `unmarshal(...)` 的数组参数类型变化 |
| 继承 `Client` 并重写 `Extract*` | JSON 参数类型变化；`ExtractLogtailProfile` 从 `public` 调整为 `protected` |

这些接口主要服务于 SDK 自身扩展。建议已有应用将适配代码隔离在 adapter 中，新代码优先组合 SDK 的公开请求、响应和 model API。

## 5. fastjson 集成点移除

- SDK model 不再携带 fastjson 的 `@JSONField` / `@JSONType` 注解。不要再用 fastjson 直接序列化 SDK model。
- `DataSinkType`、`DataSourceType`、`JobScheduleType`、`JobState`、`JobType`、`NotificationType`、`ResourceName`、`TimeSpanType` 以及部分嵌套 enum 不再实现 fastjson `JSONSerializable`。enum 的 `toString()` 值不变。
- `ToGeneralSerializer` 已删除。应用应通过对应 model 的 getter/setter 或 String JSON 接口工作。
- SDK 不再传递提供 fastjson。应用自己的代码如果仍然需要 fastjson，必须显式声明依赖并自行维护版本。

## 6. 行为变化

| 项目 | non-fastjson 版本行为 | 应用需要做什么 |
|---|---|---|
| JSON key 顺序 | 顺序可能与原版本不同，JSON 语义不变 | 测试改为 JSON 语义比较，不要逐字比较字符串 |
| `LogItem.GetLogContents()` 顺序 | 保留服务端返回顺序 | 按 key 查找字段，不要依赖固定下标 |
| JSON 输入 | 要求标准 JSON | 使用双引号；不要使用尾随逗号、未加引号的 key 或空输入 |
| 解析异常 | 不再抛 fastjson `JSONException`，消息文本也可能变化 | 按 SDK 方法声明处理异常，不要按消息文本分支 |
| `null` 字段 | 继续省略 | 无需修改；显式 `null` 不会发送到服务端 |
| Date | 继续使用 Unix 秒时间戳 | 无需修改 |

## 7. 新增的响应能力

`Response` 新增 `getRawResponseBody()`，用于读取 SDK model 尚未覆盖的服务端原始响应字段。该值可能为 `null`，应用应先判空；它不会改变已有 getter 的行为。
