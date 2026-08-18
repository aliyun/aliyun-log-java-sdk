# Log Service Java SDK

[中文版 README](https://github.com/aliyun/aliyun-log-java-sdk/blob/master/README.md)

The Java SDK is a wrapper for all Log Service APIs. You can use it to call any Log Service API. Part of the API documentation is available in the [Help Center](https://help.aliyun.com/document_detail/29007.html).

### Notes
1. To improve I/O efficiency on your system, do **not** use this SDK directly to write data into Log Service. The standard way to write data is described in [**Quick Start for Aliyun LOG Java Producer**](https://yq.aliyun.com/articles/682761).
2. To consume data from Log Service, do **not** use the raw pull API of this SDK directly. We provide a higher-level [**Consumer Library**](https://help.aliyun.com/document_detail/28998.html) that hides Log Service implementation details and offers features such as load balancing and ordered consumption.

### Sample 1: Build a client
```

String accessId = "your_access_id";
String accessKey = "your_access_key";
String host = "your_endpoint";
Client client = new Client(host, accessId, accessKey);

// Notes on UseMetricStoreUrl:
// 1. The setting is global at the Client level. It auto-appends the hash key (METRICS_STORE_AUTO_HASH).
//    Use it only when the destination Store is a Metricstore — for Metricstores with a large
//    time-series cardinality it improves time-series query performance.
// 2. When both Logstore and Metricstore writes coexist, the caller must manually set HashKey
//    to METRICS_STORE_AUTO_HASH to trigger the auto-hash write path for the Metricstore.
client.setUseMetricStoreUrl(true);
```

### Sample 2: Create a Logstore
```

String project = "your_project_name";
String logstore = "your_logstore";
int ttl_in_day = 3;
int shard_count = 10;
LogStore store = new LogStore(logstore, ttl_in_day, shard_count);
CreateLogStoreResponse res = client.CreateLogStore(project, store);

```

### Sample 3: Write data
```

int numLogGroup = 10;
/**
 * Send numLogGroup log packets to Log Service. Each packet contains 2 log lines.
 */
for (int i = 0; i < numLogGroup; i++) {
    List<LogItem> logGroup = new ArrayList<LogItem>();
    LogItem logItem = new LogItem((int) (new Date().getTime() / 1000));
    logItem.PushBack("level", "info");
    logItem.PushBack("name", String.valueOf(i));
    logItem.PushBack("message", "it's a test message");

    logGroup.add(logItem);

    LogItem logItem2 = new LogItem((int) (new Date().getTime() / 1000));
    logItem2.PushBack("level", "error");
    logItem2.PushBack("name", String.valueOf(i));
    logItem2.PushBack("message", "it's a test message");
    logGroup.add(logItem2);

    try {
        client.PutLogs(project, logStore, topic, logGroup, "");
    } catch (LogException e) {
        System.out.println("error code :" + e.GetErrorCode());
        System.out.println("error message :" + e.GetErrorMessage());
        System.out.println("error requestId :" + e.GetRequestId());
        throw e;
    }

}

```

### Sample 4: Read data
```

int shardId = 0;  // Read data from shard 0 only
GetCursorResponse res;
try {
    // Cursor for the first batch of logs received in the last hour
    long fromTime = (int)(System.currentTimeMillis()/1000.0 - 3600);
    res = client.GetCursor(project, logStore, shardId, fromTime);
    System.out.println("shard_id:" + shardId + " Cursor:" + res.GetCursor());
} catch (LogException e) {
    e.printStackTrace();
}

String cursor = res.GetCursor();
while(true) {
    BatchGetLogResponse logDataRes = client.BatchGetLog(
    project, logStore, shardId, 100, cursor);
    // Data returned by the server
    List<LogGroupData> logGroups = logDataRes.GetLogGroups();

    String nextCursor = logDataRes.GetNextCursor();  // Cursor for the next read
    System.out.print("The Next cursor:" + nextCursor);
    if (cursor.equals(nextCursor)) {
        break;
    }
    cursor = nextCursor;
}

```

## Maven dependency

During the transition, `master` continues to publish `0.6.x` releases that
retain fastjson, while `non-fastjson` publishes `0.6.x-non-fastjson` releases
without fastjson. The current non-fastjson version is a release, not a SNAPSHOT, and
must be selected explicitly:

```
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.161-non-fastjson.2</version>
</dependency>
```

Do not put `0.6.161` and `0.6.161-non-fastjson.2` in the same dependency graph.
They use the same Maven coordinates but expose different JSON APIs, and Maven
`ComparableVersion` sorts an unknown qualifier after the unqualified release,
so `0.6.161-non-fastjson.2` is newer than `0.6.161`. See [MIGRATION.md](MIGRATION.md)
for application migration.

## protobuf conflicts
If the project pulls in a conflicting `protobuf-java` version, use the special variant provided by Aliyun LOG Java SDK:
```
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.161-non-fastjson.2</version>
    <classifier>jar-with-dependencies</classifier>
    <exclusions>
        <exclusion>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

The resulting fat JAR is named
`aliyun-log-0.6.161-non-fastjson.2-jar-with-dependencies.jar`.

## FAQ
**Q**: Version conflict between `aliyun-log-java-sdk` and `aliyun-sls-xxx-inner` — symptom and fix.

**A**: These two jars cannot coexist in the same project. If one of your dependencies transitively pulls in `aliyun-sls-xxx-inner`, exclude it manually:
```
<dependency>
  <groupId>groupId1</groupId>
  <artifactId>artifactId1</artifactId>
  <version>version1</version>
  <exclusions>
    <exclusion>
      <groupId>com.aliyun.openservices</groupId>
      <artifactId>aliyun-sls-xxx-inner</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```
