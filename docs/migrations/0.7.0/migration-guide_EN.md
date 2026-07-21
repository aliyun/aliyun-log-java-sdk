# Migration Guide 0.6.x → 0.7.0: fastjson Removal

> 中文版: [migration-guide.md](migration-guide.md)
> Full API inventory (class/method level): [breaking-changes.md](breaking-changes.md)

## 1. Background

Starting with **0.7.0**, the SDK completely removes its dependency on `com.alibaba:fastjson`:

- fastjson 1.x was archived in October 2024 (de-facto EOL) and has a history of autotype
  deserialization vulnerabilities (e.g. CVE-2022-25845). The SDK previously depended on the
  `1.2.83_noneautotype` security build, which no longer receives upstream maintenance.
- In 0.6.x, fastjson types were widely exposed in the public API (e.g. `ToJsonObject()`
  returning `com.alibaba.fastjson.JSONObject`), making it impossible to swap the JSON library
  without breaking users.

Design goals of 0.7.0:

1. **JSON-library-agnostic public API** — signatures only contain JDK types (`String`,
   `Map<String, Object>`, …) or SDK-owned types. No third-party JSON library types appear
   (neither fastjson nor gson).
2. Internals migrated to gson (`com.google.code.gson:gson:2.13.2`). gson is a transitive
   dependency only, and inside the `jar-with-dependencies` classifier it is relocated to
   `com.aliyun.log.thirdparty.com.google.gson`, so it cannot conflict with your classpath.
3. Any future JSON library change inside the SDK will be zero-breaking for users.

## 2. Am I affected? (quick self-check)

Run these in your project:

```bash
# Check 1: does your code use fastjson types when interacting with this SDK?
grep -rn "com.alibaba.fastjson" --include="*.java" src/ | grep -v "^Binary"

# Check 2: does your project rely on fastjson transitively from the SDK
# (used in code but never declared)?
mvn dependency:tree -Dincludes=com.alibaba:fastjson

# Check 3: do you extend SDK extension points?
grep -rn "implements ShipperConfig\|extends JobConfiguration\|extends Client\b\|implements Unmarshaller" --include="*.java" src/
```

| Result | Conclusion |
|---|---|
| All three empty | **No code change needed** — just upgrade. If tests compare JSON strings byte-for-byte, see §5. |
| Check 1/2 hit, but fastjson is only used for your own business JSON | **Declare fastjson explicitly** in your pom (it is no longer transitively provided). No SDK-related change needed. |
| Check 1 hits where fastjson types touch SDK APIs (e.g. `JSONObject obj = index.ToJsonObject()`) | Migrate per §4. |
| Check 3 hits | Migrate extension points per §4.6. |

## 3. Impact overview

The complete inventory — every affected class and method signature — is in
[breaking-changes.md](breaking-changes.md). Summary by category:

| Category | Scale | Breakage |
|---|---|---|
| A. `JSONObject`/`JSONArray` in signatures of `ToJsonObject()` / `FromJsonObject(...)` / `deserialize(...)` / `marshal(...)` etc. changed from fastjson to internal SDK shim types | ~130 classes across common/response/request, see inventory §2 | Compile error, only if your code explicitly references fastjson types to receive/pass values |
| B. Dynamic JSON fields changed to `Map<String, Object>`: `LogException.accessDeniedDetail`, `EtlMeta.metaValue`, `Advanced.others` | 3 fields, see inventory §3 | Compile error |
| C. 13 enums no longer implement `JSONSerializable`; `ToGeneralSerializer` deleted; all `@JSONField`/`@JSONType` annotations removed | see inventory §4, §5 | Only if you relied on these fastjson integration points |
| D. Extension point signature changes: `ShipperConfig`, `JobConfiguration`, `Unmarshaller`, `Client.Extract*` (`ExtractLogtailProfile` demoted public → protected) | see inventory §6 | Custom implementations/subclasses need import changes |
| E. Exception types: parse failures no longer throw `com.alibaba.fastjson.JSONException` | see inventory §8 | Only if you catch fastjson exceptions |
| F. Behavior differences: JSON key order, `GetLogContents()` order, etc. | see inventory §9 | Runtime, only for code that depends on ordering/messages |

## 4. Migration guide (by scenario)

### 4.1 Scenario: receiving a JSONObject from the SDK

```java
// 0.6.x
com.alibaba.fastjson.JSONObject obj = index.ToJsonObject();
String ttl = obj.getString("ttl");
```

**Recommended** (no JSON library types, stable long-term): use the String entry points.

```java
// 0.7.0 recommended
String json = index.ToJsonString();          // String entry points are all preserved
// parse `json` with whatever JSON library you prefer
```

**Minimal change** (imports only): the shim type mirrors fastjson accessor names and semantics.

```java
// 0.7.0 minimal
com.aliyun.openservices.log.internal.json.JSONObject obj = index.ToJsonObject();
String ttl = obj.getString("ttl");           // same semantics as fastjson
```

Note: the `internal` package is for SDK-internal use and carries no cross-version compatibility
promise. The shim `extends LinkedHashMap<String, Object>`, so for read-only access you can
simply treat it as a `Map`:

```java
Map<String, Object> obj = index.ToJsonObject();   // also fine
```

### 4.2 Scenario: passing a JSONObject into the SDK

```java
// 0.6.x
com.alibaba.fastjson.JSONObject dict = com.alibaba.fastjson.JSON.parseObject(text);
config.FromJsonObject(dict);
```

**Recommended**: use the String entry point.

```java
// 0.7.0 recommended
config.FromJsonString(text);
```

**Minimal change**: switch to the shim type.

```java
// 0.7.0 minimal
import com.aliyun.openservices.log.internal.json.JSONObject;
config.FromJsonObject(JSONObject.parseObject(text));
```

### 4.3 Scenario: the three dynamic fields (`Map<String, Object>`)

```java
// 0.6.x
com.alibaba.fastjson.JSONObject detail = logException.getAccessDeniedDetail();
com.alibaba.fastjson.JSONObject meta = etlMeta.getMetaValue();
advanced.setOthers(someFastjsonObject);

// 0.7.0
Map<String, Object> detail = logException.getAccessDeniedDetail();
Object policyType = detail == null ? null : detail.get("PolicyType");
String rawDetail = logException.getAccessDeniedDetailRaw();   // new: raw JSON string

Map<String, Object> meta = etlMeta.getMetaValue();

Map<String, Object> others = new LinkedHashMap<String, Object>();
others.put("tail_size_kb", 100);
advanced.setOthers(others);
```

Values inside these maps are pure JDK types only (`String` / `Integer` / `Long` / `BigInteger`
/ `BigDecimal` / `Boolean` / `Map` / `List`) — no gson runtime types ever leak out. Nested
objects are plain `Map<String, Object>` all the way down.

### 4.4 Scenario: relying on the SDK's transitive fastjson for your own code

The SDK no longer provides fastjson transitively. Declare it explicitly in your pom:

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.83_noneautotype</version><!-- or migrate to fastjson2 / another library -->
</dependency>
```

### 4.5 Scenario: catching fastjson exceptions

```java
// 0.6.x
try {
    index.FromJsonString(text);
} catch (com.alibaba.fastjson.JSONException e) { ... }

// 0.7.0 — SDK parse errors surface as LogException or
// com.aliyun.openservices.log.internal.json.JSONException (a RuntimeException).
// Do not depend on exception message wording (it now comes from gson).
try {
    index.FromJsonString(text);
} catch (LogException e) { ... }
```

### 4.6 Scenario: extension points (custom implementations / SDK subclasses)

| What you did | Required change |
|---|---|
| Custom `ShipperConfig` implementation (`GetJsonObj()` / `FromJsonObj(...)`) | Method names unchanged; switch imports from `com.alibaba.fastjson.JSONObject` to `com.aliyun.openservices.log.internal.json.JSONObject`. Construction logic mostly carries over (the shim is a `Map` subclass; `put` works). |
| Custom `JobConfiguration` subclass (`deserialize(JSONObject)`) | Same as above — switch imports; accessors (`getString`, …) keep fastjson semantics. |
| `Unmarshaller<T>` implementation | `unmarshal(JSONArray, int)` parameter is now the shim `JSONArray`. |
| Subclassing `Client` and overriding/calling `Extract*` | Parameter types are now shim types. `ExtractLogtailProfile` changed from public to protected — external callers should use the public `GetLogtailProfile(...)` API instead. |
| Using the 13 enums as fastjson `JSONSerializable` | The interface is gone. Serialized output is unchanged (still the `toString()` value); when serializing yourself, use `enumValue.toString()`. |
| Depending on `@JSONField`/`@JSONType` annotations on SDK models (e.g. serializing SDK models with your own fastjson) | The annotations are removed; serializing SDK models directly with fastjson no longer works. Use the models' `ToJsonString()` / the SDK request methods instead. |

### 4.7 Scenario: byte-for-byte JSON string comparisons in tests/code

Key order changed from fastjson's alphabetical getter order to gson's field-declaration order.
Semantically equivalent, byte-wise different. Replace exact-string comparison with semantic
comparison (any JSON library works):

```java
// Anti-pattern (breaks on 0.7.0)
assertEquals(expectedJson, config.ToJsonString());

// Semantic comparison (example: compare parsed maps)
assertEquals(JSONObject.parseObject(expectedJson), JSONObject.parseObject(config.ToJsonString()));
```

### 4.8 Scenario: positional access to GetLogs result fields

The order of `LogItem.GetLogContents()` changed from fastjson HashMap hash order (accidentally
stable) to the server's original response order. Code relying on fixed indices must look up by
key:

```java
// Anti-pattern (worked by accident on 0.6.x)
String topic = item.GetLogContents().get(0).GetValue();

// 0.7.0
String topic = null;
for (LogContent c : item.GetLogContents()) {
    if ("__topic__".equals(c.GetKey())) { topic = c.GetValue(); break; }
}
```

## 5. Behavior differences

The migration was validated with per-model old-vs-new serialization comparison tests. The
following differences are known and intentional (full table in
[breaking-changes.md §9](breaking-changes.md#9-behavior-differences--行为差异)):

| Item | Notes |
|---|---|
| **JSON key order** | fastjson: alphabetical getter order; gson: field declaration order. Semantically equivalent — **byte-for-byte JSON comparisons must switch to semantic comparison** (§4.7). |
| **GetLogs field order** | `LogItem.GetLogContents()` now preserves server response order instead of HashMap hash order. **Positional access must switch to key lookup** (§4.8). |
| Number types | Aligned with fastjson: integers parse to `Integer` / `Long` / `BigInteger` by magnitude, decimals to `BigDecimal`; literals round-trip unchanged (`30` never becomes `30.0`). |
| Character escaping | gson HTML escaping is disabled; `< > & =` and CJK characters are emitted verbatim, same as fastjson. |
| Parse strictness | Invalid JSON is rejected, aligned with fastjson; unescaped control characters inside quoted strings are still accepted (fastjson legacy behavior). |
| Error messages | Parse-failure messages now come from gson and differ from 0.6.x. Never branch on exception message text. |
| `Date` | Still serialized as Unix timestamp (seconds). |
| `null` fields | Still omitted from output (fastjson-compatible default). |

## 6. New capability: `getRawResponseBody()`

The `Response` base class gains a read-only passthrough for reading server fields not yet
covered by SDK models:

```java
String raw = resp.getRawResponseBody(); // raw server JSON; null if not passed through
```

Currently populated by: `GetCursor`, `ProjectConsumerGroupGetCheckPoint`,
`ProjectConsumerGroupHeartBeat`. Best-effort; may expand in later versions.

## 7. Preserved String entry points

All existing `FromJsonString(String)` / `ToJsonString()` / `toJsonString()` methods are
preserved with unchanged behavior (Index, LogStore, Config, Machine Group, Dashboard/Chart,
ETL, Shipper, Job/Alert, …). For persistence, GitOps, and cross-system config transfer, use
these String entry points — they are the only JSON interaction surface with a long-term
stability promise.

## 8. 0.6.x maintenance

- The final 0.6.x release only adds deprecation markers and an advance notice of this change;
  no new features.
- Security fixes for 0.6.x will be evaluated case by case; upgrading to 0.7.0 is recommended.
