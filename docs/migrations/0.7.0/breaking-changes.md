# 0.7.0 Breaking Changes — Full API Inventory / 完整 API 变更清单

> Companion to [migration-guide.md](migration-guide.md)（中文） / [migration-guide_EN.md](migration-guide_EN.md)（English）.
>
> This file lists every public/protected API affected by the fastjson removal in 0.7.0,
> down to class and method level. Identifiers are language-neutral.
> 本文件精确列出 0.7.0 移除 fastjson 影响到的全部 public/protected API（类与方法级）。

Unless stated otherwise, `JSONObject` / `JSONArray` below mean:

| Version | Type |
|---|---|
| 0.6.x | `com.alibaba.fastjson.JSONObject` / `com.alibaba.fastjson.JSONArray` |
| 0.7.0 | `com.aliyun.openservices.log.internal.json.JSONObject` / `com.aliyun.openservices.log.internal.json.JSONArray` |

The 0.7.0 shim types: `JSONObject extends LinkedHashMap<String, Object>`, `JSONArray extends ArrayList<Object>`.

---

## 1. Dependency changes / 依赖变化

| | 0.6.x | 0.7.0 |
|---|---|---|
| fastjson | `com.alibaba:fastjson:1.2.83_noneautotype` (transitive) | **removed** |
| gson | — | `com.google.code.gson:gson:2.13.2` (transitive; never appears in API signatures) |

Shade relocations in the `jar-with-dependencies` classifier:

| 0.6.x | 0.7.0 |
|---|---|
| `com.alibaba.fastjson` → `com.aliyun.log.thirdparty.com.alibaba.fastjson` | removed |
| — | `com.google.gson` → `com.aliyun.log.thirdparty.com.google.gson` |
| protobuf / httpclient / commons relocations | unchanged |

---

## 2. Signature type replacement: fastjson → internal shim / 签名类型替换

Every method below kept its **name and shape**; only the `JSONObject`/`JSONArray` types in the
signature changed from fastjson to `com.aliyun.openservices.log.internal.json.*`.
User code that passes `com.alibaba.fastjson.JSONObject` into these methods, or assigns their
return value to a fastjson type, **no longer compiles**.

### 2.1 `com.aliyun.openservices.log.common`

| Class | Affected methods |
|---|---|
| `Advanced` | `toJsonObject()`, `static fromJsonObject(JSONObject)` |
| `Alert` | `deserialize(JSONObject)` |
| `AlertConfiguration` (+ inner `SeverityConfiguration`, `NotificationConfiguration`, `JoinConfiguration`, `StoreConfig`, `Sink`, `SinkStore`, `SinkAlerm`) | `deserialize(JSONObject)` on each |
| `AliyunADBSink`, `AliyunLOGSink`, `AliyunOSSSink`, `AliyunTSDBSink` | `deserialize(JSONObject)` |
| `AliyunBSSSource`, `AliyunCloudMonitorSource`, `AliyunMaxComputeSource`, `AliyunOSSSource` | `deserialize(JSONObject)` |
| `ApsaraLogConfigInputDetail` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `AuditJob` | `deserialize(JSONObject)` |
| `AuditJobConfiguration` | `deserialize(JSONObject)`, `toJsonObject()` |
| `CertificateConfiguration` | `marshal()` |
| `Chart` | `RawDisplayToJsonObject()`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `CnameConfiguration` (+ inner `CnameConfigurationRecord`) | `unmarshal(JSONObject)` |
| `CommonConfigInputDetail` | `SetShardHashKey(JSONArray)`, `SetFilterRegex(JSONArray)`, `SetFilterKey(JSONArray)`, `abstract ToJsonObject()`, `abstract FromJsonObject(JSONObject)`, `protected CommonConfigToJsonObject(JSONObject)`, `protected CommonConfigFromJsonObject(JSONObject)`, `static FromJsonObjectS(String, JSONObject)` |
| `Config` | `SetInputDetail(JSONObject)`, `SetOutputDetail(JSONObject)`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `ConfigInputDetail` | `ToJsonObject()`, `FromJsonObject(JSONObject)`, `SetKey(JSONArray)` |
| `ConfigOutputDetail` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `ConsumerGroup` | `ToRequestJson()` |
| `ConsumerGroupShardCheckPoint` | `Deserialize(JSONObject)` |
| `CsvExternalStore` | constructor `CsvExternalStore(JSONObject)` |
| `Dashboard` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `DashboardBasedJobConfiguration` | `deserialize(JSONObject)` |
| `DataFormat`, `DataSink`, `DataSource` | `deserialize(JSONObject)` |
| `DeleteLogStoreLogsTask` | `toJsonObject()`, `fromJsonObject(JSONObject)` |
| `DelimitedTextFormat` | `deserialize(JSONObject)` |
| `DelimiterConfigInputDetail` | `SetKey(JSONArray)`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `DingTalkNotification`, `EmailNotification`, `HttpNotification`, `SmsNotification`, `WebhookNotification`, `Notification` | `deserialize(JSONObject)` |
| `Domain` | `toJsonObject()`, `fromJsonObject(JSONObject)` |
| `EncryptConf`, `EncryptUserCmkConf` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `EncryptConfig` | `static FromJsonObject(JSONObject)`, `ToJsonObject()` |
| `ETLConfiguration`, `ETLV2` | `deserialize(JSONObject)` |
| `EtlJob` | `toJsonObject(boolean, boolean)`, `fromJsonObject(JSONObject)` |
| `EtlMeta` | `toJsonObject()`, `fromJsonObject(JSONObject)` — also see §3 |
| `Export`, `ExportConfiguration` | `deserialize(JSONObject)` |
| `ExportContentColumnStorageDetail`, `ExportContentCsvDetail`, `ExportContentDetail`, `ExportContentJsonDetail`, `ExportContentParquetDetail` | `deserialize(JSONObject)` |
| `ExportGeneralSink` | `deserialize(JSONObject)` |
| `ExternalStore` | constructor `ExternalStore(JSONObject)`, `fromJson(JSONObject)`, `toJson()` |
| `GeneralJobConfiguration` | `deserialize(JSONObject)`, `toJsonObject()` |
| `GroupAttribute` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `Index` | `ToRequestJson()`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `IndexJsonKey` | `FromJsonObject(JSONObject)`, `ToRequestJson()` |
| `IndexKey` | `ToRequestJson()`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `IndexKeys` | `ToRequestJson()`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `IndexLine` | `ToRequestJson()`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `Ingestion`, `IngestionConfiguration`, `IngestionGeneralSource` | `deserialize(JSONObject)` |
| `JDBCSource`, `KafKaSource` | `deserialize(JSONObject)` |
| `Job`, `JobInstance`, `JobSchedule`, `JobDownSamplingConfiguration` | `deserialize(JSONObject)` |
| `JobConfiguration` | `abstract deserialize(JSONObject)` — extension point, see §6 |
| `JSONFormat`, `LineFormat`, `MultilineFormat`, `StructuredDataFormat` | `deserialize(JSONObject)` |
| `JsonConfigInputDetail` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `LinkStore` | `ToRequestJson()` |
| `LocalFileConfigInputDetail` | `protected LocalFileConfigToJsonObject(JSONObject)`, `protected LocalFileConfigFromJsonObject(JSONObject)` |
| `Logging` | `marshal()`, `static unmarshal(JSONObject)` |
| `LoggingDetail` | `marshal()`, `static unmarshal(JSONObject)` |
| `LogStore` | `ToRequestJson()`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `LogtailProfile` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `Machine` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `MachineGroup` | `SetMachineList(JSONArray)`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `MachineList` | `SetMachineList(JSONArray)`, `FromJsonArray(JSONArray)` |
| `Metric2MetricParameters` | `deserialize(JSONObject)` |
| `MetricStore` | `ToRequestJson()`, `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `OdpsShipperConfig` | `GetJsonObj()`, `FromJsonObj(JSONObject)` |
| `OssShipperConfig` | `GetJsonObj()`, `FromJsonObj(JSONObject)` |
| `OssShipperCsvStorageDetail`, `OssShipperJsonStorageDetail`, `OssShipperParquetStorageDetail`, `OssShipperStorageColumn` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `OssShipperStorageDetail` | `abstract ToJsonObject()`, `abstract FromJsonObject(JSONObject)` |
| `PluginLogConfigInputDetail`, `StreamLogConfigInputDetail` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `Project` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `ProjectConsumerGroup` | `ToRequestJson()` |
| `ProjectQuota` | `static parseFromJSON(JSONObject)`, `fromJSON(JSONObject)` |
| `Query` | `deserialize(JSONObject)` |
| `QueryResult` | `static extractLogFromJSON(JSONObject, String)`, `static parseData(JSONArray, String)`, inner `PhraseQueryInfo.deserializeFrom(JSONObject)` |
| `RebuildIndex`, `RebuildIndexConfiguration` | `deserialize(JSONObject)` |
| `Report`, `ReportConfiguration` | `deserialize(JSONObject)` |
| `Resource`, `ResourceRecord` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `SavedSearch` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `ScheduledJob`, `ScheduledSQL`, `ScheduledSQLBaseParameters`, `ScheduledSQLConfiguration` | `deserialize(JSONObject)` |
| `SensitiveKey` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |
| `ShardingPolicy` (+ inner `HashShardingPolicy`, `GroupShardingPolicy`) | `ToJsonObject()`, `FromJsonObject(JSONObject)` on each |
| `ShipperMigration` | `static extractGetMigration(JSONObject, String)`, `static extractListMigration(JSONObject, String)`, `static extractMigrations(JSONObject, String)` |
| `ShipperTask` | `FromJsonObject(JSONObject)` |
| `SqlInstance` | `fromJson(JSONObject)` |
| `SubStore` | `toRequestJson()` |
| `TagResource` | `static FromJsonObject(JSONObject)` |
| `TimeSpan` | `deserialize(JSONObject)` |
| `Topostore`, `TopostoreNode`, `TopostoreRelation` | `ToJsonObject()`, `FromJsonObject(JSONObject)` |

### 2.2 `com.aliyun.openservices.log.request`

| Class | Affected methods |
|---|---|
| `SetProjectCnameRequest` | `marshal()` |

### 2.3 `com.aliyun.openservices.log.response`

| Class | Affected members |
|---|---|
| `ConsumerGroupCheckPointResponse` | constructor `(Map<String,String>, JSONArray)` |
| `GetCheckPointResponse` | constructor `(Map<String,String>, JSONArray)` |
| `GetContextLogsResponse` | constructor `(Map<String,String>, JSONObject)` |
| `GetHistogramsResponse` | `fromJSON(JSONArray)` |
| `GetLogStoreMeteringModeResponse`, `GetMetricStoreMeteringModeResponse`, `GetLogStoreMultimodalConfigurationResponse` | `deserializeFrom(JSONObject)` |
| `GetMetricsConfigResponse` | `fromJsonObject(JSONObject)` |
| `GetProjectResponse` | `FromJsonObject(JSONObject)` |
| `ListMetricsConfigResponse` | `fromJSON(JSONObject)` |
| `ListProjectCnameResponse` | `unmarshal(JSONArray)` |
| `ListProjectResponse` | `fromJSON(JSONObject)` |
| `ProjectConsumerGroupCheckPointResponse` | constructor `(Map<String,String>, JSONObject)` |
| `ProjectConsumerGroupHeartBeatResponse` | constructor `(Map<String,String>, JSONObject)` |
| `ResponseList` | `deserialize(JSONObject, String)` |
| `GetAlertResponse`, `GetETLV2Response`, `GetIngestionResponse`, `GetAuditJobResponse`, `GetJobResponse`, `GetJobInstanceResponse`, `GetScheduledSQLResponse`, `GetRebuildIndexResponse`, `GetExportResponse`, `GetReportResponse` | `deserialize(JSONObject, String)` |

### 2.4 `com.aliyun.openservices.log` — `Client`

All `Extract*` parsing helpers keep `protected` visibility with shim types, **except**
`ExtractLogtailProfile` which additionally changed `public` → `protected` (see §6).

`protected` methods with shim-typed parameters: `ExtractLogtailProfile`, `ExtractTagResources`,
`ExtractConfigFromResponse`, `ExtractConfigs`, `ExtractMachineGroupFromResponse`,
`ExtractMachineGroups`, `ExtractShards`, `ExtractChartFromResponse`,
`ExtractDashboardFromResponse`, `ExtractDashboards`, `ExtractSavedSearchFromResponse`,
`ExtractSavedSearches`, `ExtractDomains`, `extractTopostoreFromResponse`, `extractTopostores`,
`extractTopostoreNodeFromResponse`, `extractTopostoreNodesFromResponse`,
`extractTopostoreRelationFromResponse`, `extractTopostoreRelationsFromResponse`,
`extractResourceFromResponse`, `extractResources`, `extractResourceRecordFromResponse`,
`extractResourceRecords`.

### 2.5 `com.aliyun.openservices.log.internal` / `util`

| Class | Affected methods |
|---|---|
| `internal.Unmarshaller<T>` | `T unmarshal(JSONArray value, int index)` — extension point, see §6 |
| `util.JsonUtils` | `readList(JSONObject, String, Unmarshaller)`, `readList(JSONArray, Unmarshaller)`, `readOptionalStrings(JSONObject, String)`, `readStringList(JSONObject, String)`, `readOptionalString(JSONObject, String[, String])`, `readBool(JSONObject, String, boolean)`, `readOptionalInt(JSONObject, String)`, `readOptionalDate(JSONObject, String)`, `readDate(JSONObject, String)`, `readOptionalMap(JSONObject, String)` |

---

## 3. Dynamic JSON fields: `JSONObject` → `Map<String, Object>` / 动态字段

| Class | 0.6.x | 0.7.0 |
|---|---|---|
| `exception.LogException` | `JSONObject getAccessDeniedDetail()` / `setAccessDeniedDetail(JSONObject)` | `Map<String, Object> getAccessDeniedDetail()` / `setAccessDeniedDetail(Map<String, Object>)`; **new** `String getAccessDeniedDetailRaw()` |
| `common.EtlMeta` | `JSONObject getMetaValue()` / `setMetaValue(JSONObject)`; constructor `EtlMeta(String, String, String, JSONObject, boolean)` | `Map<String, Object>` in all three |
| `common.Advanced` | `JSONObject getOthers()` / `setOthers(JSONObject)` | `Map<String, Object>` in both |

Values inside the maps are pure JDK types only: `String`, `Integer`, `Long`, `BigInteger`,
`BigDecimal`, `Boolean`, `Map`, `List`. No gson runtime types ever leak out.

---

## 4. Deleted / added classes / 删除与新增的类

**Deleted:**

| Class | Replacement |
|---|---|
| `com.aliyun.openservices.log.common.ToGeneralSerializer` (fastjson `ObjectSerializer`) | internal gson adapter; serialized output of `GeneralJobConfiguration` / `IngestionGeneralSource` / `ExportGeneralSink` is unchanged |

**Added — `com.aliyun.openservices.log.internal.json` (INTERNAL, no compatibility promise):**

| Class | Shape |
|---|---|
| `JSONObject` | `extends LinkedHashMap<String, Object>`; `parseObject(String)`, `parseObject(String, Class<T>)`, `parse(String)`, `toJSONString(Object)`, `toJSONString()`, `getJSONObject/getJSONArray/getString/getInteger/getIntValue/getLong/getLongValue/getBoolean/getBooleanValue/getDouble/getDoubleValue/getFloat/getFloatValue/getBigDecimal`, `getInnerMap()` |
| `JSONArray` | `extends ArrayList<Object>`; `parseArray(String)`, `toJSONString()`, index-based getters mirroring `JSONObject` |
| `JSON` | `toJSONString(Object)`, `parse(String)`, `parseObject(String)`, `parseArray(String)` |
| `JSONException` | `extends RuntimeException` |
| `GsonHolder` | `static Gson gson()` (internal) |

Accessor semantics mirror fastjson: missing key → `getString` returns `null`, `getIntValue`
returns `0`; scalars convert loosely between String/Number/Boolean.

---

## 5. Removed fastjson annotations & interfaces / 注解与接口移除

### 5.1 Enums no longer implement `com.alibaba.fastjson.serializer.JSONSerializable`

The `write(...)` method is removed from all 13; serialized output (the `toString()` value) is
unchanged, now produced by an internal gson adapter:

1. `common.DataSinkType`
2. `common.DataSourceType`
3. `common.JobScheduleType`
4. `common.JobState`
5. `common.JobType`
6. `common.NotificationType`
7. `common.ResourceName`
8. `common.TimeSpanType`
9. `common.KafKaSource.KafkaPosition`
10. `common.KafKaSource.ValueType`
11. `common.AlertConfiguration.JoinType`
12. `common.AlertConfiguration.GroupType`
13. `common.AlertConfiguration.StoreType`

### 5.2 `@JSONField` / `@JSONType` removed from all model classes

Files that carried fastjson annotations in 0.6.x (46 files) now either:

- use `com.google.gson.annotations.SerializedName` where the JSON key differs from the field
  name (e.g. `MetricsConfig` family, `Resource*` policy/template classes, `Parameter`,
  `UntagResourcesRequest`, `ExportContentCsvDetail`), or
- carry no annotation at all (field name == JSON key), or
- have their special semantics implemented by internal gson adapters:
  - `@JSONField(unwrapped=true)` on `ScheduledSQLBaseParameters` → internal flattening adapter
  - `@JSONField(serializeUsing=ToGeneralSerializer.class)` → internal raw-JSON adapter

If you subclassed these models and relied on fastjson annotations being present for your own
fastjson serialization, that behavior is gone.

---

## 6. Extension point changes / 扩展点变化

| Extension point | Change |
|---|---|
| `common.ShipperConfig` interface | `JSONObject GetJsonObj()` / `void FromJsonObj(JSONObject)` now use shim types. Custom implementations must switch imports. |
| `common.JobConfiguration` | `abstract void deserialize(JSONObject)` now takes shim type. Custom subclasses must switch imports. |
| `internal.Unmarshaller<T>` | `T unmarshal(JSONArray, int)` now takes shim type. |
| `Client.ExtractLogtailProfile` | visibility `public` → `protected`. All other `Extract*` were already `protected` (types changed, see §2.4). Subclasses overriding any `Extract*` must update parameter types. |

---

## 7. `Response` base class additions (non-breaking) / Response 新增能力

```java
public String getRawResponseBody();          // raw server JSON, may be null
public void setRawResponseBody(String raw);
```

Currently populated by: `GetCursor`, `ProjectConsumerGroupGetCheckPoint`,
`ProjectConsumerGroupHeartBeat`. Other APIs return `null` (best-effort passthrough, may expand
in later versions).

---

## 8. Exception type changes / 异常类型

- Parse failures inside the SDK now surface `com.aliyun.openservices.log.internal.json.JSONException`
  (extends `RuntimeException`, same unchecked semantics) instead of
  `com.alibaba.fastjson.JSONException`. Do not catch fastjson exception types anymore.
- `LogException.getAccessDeniedDetail()` type change: see §3.

---

## 9. Behavior differences / 行为差异

| Item | 0.6.x (fastjson) | 0.7.0 (gson) |
|---|---|---|
| JSON key order in serialized output | alphabetical (getter order) | field declaration order — semantically equivalent; byte-for-byte comparisons will break |
| `GetLogs` → `LogItem.GetLogContents()` order | HashMap hash order (accidental) | server response order — positional access like `get(0)` for `__topic__` will break |
| Number parsing | Integer/Long/BigInteger/BigDecimal by magnitude | identical (aligned deliberately) |
| HTML characters `< > & =` | not escaped | not escaped (gson HTML escaping disabled) |
| Invalid JSON | rejected | rejected (strictness aligned); unescaped control chars inside quoted strings still accepted |
| Parse error messages | fastjson wording | gson wording — do not match on message text |
| `Date` serialization | Unix seconds | Unix seconds |
| `null` fields | omitted | omitted |
