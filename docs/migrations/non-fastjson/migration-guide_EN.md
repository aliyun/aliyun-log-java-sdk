# aliyun-log Java SDK non-fastjson Migration Guide

> 中文版：[migration-guide.md](migration-guide.md)
>
> Public API changes: [breaking-changes.md](breaking-changes.md)

## 1. Choose one release line

Two release lines are maintained during the migration period:

| Maven version | fastjson dependency | Intended use |
|---|---|---|
| `0.6.x` | retained | applications that have not migrated yet |
| `0.6.x-non-fastjson` | removed | applications that have completed the checks in this guide |

For example, the current recommended release is `0.6.162-non-fastjson.1`. The suffix is part of a normal release version. It is not a classifier and does not make the artifact a SNAPSHOT. Maven considers `0.6.162-non-fastjson.1` newer than `0.6.162`, but applications should still select the intended version explicitly. Unqualified `0.6.x` releases will remain available during the transition.

An application must select exactly one line. Do not put `0.6.162` and `0.6.162-non-fastjson.1` in the same dependency graph: Maven will select only one of them, while downstream code may have been compiled against the other API.

## 2. Change the Maven dependency

For the regular JAR, change only the version:

```xml
<!-- Before: release line that retains fastjson -->
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.162</version>
</dependency>
```

```xml
<!-- After: release line without fastjson -->
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.162-non-fastjson.1</version>
</dependency>
```

If you use the fat JAR, keep the `jar-with-dependencies` classifier:

```xml
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.162-non-fastjson.1</version>
    <classifier>jar-with-dependencies</classifier>
</dependency>
```

The resulting file is `aliyun-log-0.6.162-non-fastjson.1-jar-with-dependencies.jar`.

## 3. Check your application before upgrading

Run these checks in the application repository:

```bash
# Does application code directly use fastjson?
rg "com\.alibaba\.fastjson" --glob '*.java'

# Does the application rely on fastjson supplied transitively by the SDK?
mvn dependency:tree -Dincludes=com.alibaba:fastjson

# Does application code use SDK internal APIs?
rg "com\.aliyun\.openservices\.log\.internal|InternalApi" --glob '*.java'

# Does the application implement or extend an affected extension point?
rg "implements ShipperConfig|extends JobConfiguration|extends Client\b|implements Unmarshaller" --glob '*.java'
```

- If all checks are empty, changing the Maven version and rerunning the build and tests is normally enough.
- If fastjson is used only for application-owned JSON, declare that dependency explicitly; the SDK no longer provides it transitively.
- If fastjson types are passed to or returned from SDK APIs, migrate them as shown below.
- If SDK `internal` packages or custom extension points are used, review the [public API changes](breaking-changes.md).

## 4. Common source changes

### 4.1 Stop exchanging fastjson objects with the SDK

Prefer model getters/setters. When complete JSON is needed, use the String API and parse it with the JSON library selected by your application.

```java
// Before
com.alibaba.fastjson.JSONObject json = index.toJsonObject();
String jsonText = json.toJSONString();

// After
String jsonText = index.toJsonString();
```

Use the String API for input as well:

```java
// Before
com.alibaba.fastjson.JSONObject json =
        com.alibaba.fastjson.JSON.parseObject(jsonText);
config.fromJsonObject(json);

// After
config.fromJsonString(jsonText);
```

Do not import `com.aliyun.openservices.log.internal.*` as a replacement for fastjson. Those types are not application-facing compatibility APIs.

### 4.2 Use `Map<String, Object>` for dynamic JSON fields

The following APIs no longer accept or return fastjson `JSONObject`:

- `LogException.getAccessDeniedDetail()` / `setAccessDeniedDetail(...)`
- `EtlMeta.getMetaValue()` / `setMetaValue(...)`
- `Advanced.getOthers()` / `setOthers(...)`

Representative migration:

```java
// Before
com.alibaba.fastjson.JSONObject others = new com.alibaba.fastjson.JSONObject();
others.put("tail_size_kb", 100);
advanced.setOthers(others);

// After
Map<String, Object> others = new LinkedHashMap<String, Object>();
others.put("tail_size_kb", 100);
advanced.setOthers(others);
```

Nested objects and arrays are represented as `Map<String, Object>` and `List<Object>`. Use `LogException.getAccessDeniedDetailRaw()` when the original access-denied JSON is required.

### 4.3 Keep fastjson only for application-owned JSON

Applications may continue to use fastjson independently, but must declare and maintain the dependency themselves:

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>choose-and-maintain-an-appropriate-version</version>
</dependency>
```

Do not rely on the SDK to supply fastjson, and do not serialize SDK models directly with fastjson. Use model getters/setters or `toJsonString()` / `fromJsonString(...)`.

### 4.4 Custom extensions

If the application implements `ShipperConfig`, extends `JobConfiguration`, or subclasses `Client`, JSON parameter types and some method names have changed. These extension points are not recommended for new application code. Recompile existing implementations, follow the compiler errors, and isolate the adaptation in a dedicated adapter. See [Custom implementations and inheritance](breaking-changes.md#4-自定义实现和继承).

## 5. Behavior changes to check

- JSON key order may change. Compare JSON semantically instead of comparing strings byte for byte.
- `LogItem.GetLogContents()` now preserves server response order. Find a field by key instead of using a fixed index.
- Input must be standard JSON: use double quotes and avoid trailing commas, unquoted keys, and empty input.
- SDK parse failures no longer throw fastjson `JSONException`. Handle `LogException` and other exceptions declared by the SDK API, and do not match exception message text.
- `null` fields remain omitted and are not sent to the service as explicit `null` values.

Example semantic comparison:

```java
// Before
assertEquals(expectedJson, config.toJsonString());

// After: assertJsonEquals represents the application's JSON-tree comparison helper
assertJsonEquals(expectedJson, config.toJsonString());
```

Example key lookup:

```java
String topic = null;
for (LogContent content : item.GetLogContents()) {
    if ("__topic__".equals(content.GetKey())) {
        topic = content.GetValue();
        break;
    }
}
```

## 6. Validate after upgrading

1. Run `mvn dependency:tree` and confirm that only one `com.aliyun.openservices:aliyun-log` version is present and that it has the `-non-fastjson` suffix.
2. Confirm that the application does not accidentally rely on fastjson from the SDK.
3. Recompile every module and address fastjson types, old JSON method names, and custom extension points.
4. Run unit and integration tests, especially configuration create/update, Job, Alert, Shipper, GetLogs, and error handling.
5. Compare generated request JSON semantically, including the omission of optional null fields.

For a temporary rollback, restore the paired unqualified version—for example, change `0.6.162-non-fastjson.1` back to `0.6.162`. Do not retain both versions.
