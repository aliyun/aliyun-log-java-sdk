# log service java sdk
java sdk 是对所有log service 提供的API的封装，通过该sdk，可以调用所有log service。部分API文档请参考[文档中心](https://help.aliyun.com/document_detail/29007.html)。 
### 注意
1. 为了提高您系统的 IO 效率，请尽量不要直接使用 SDK 往日志服务中写数据，写数据标准做法参考文章 [**Aliyun LOG Java Producer 快速入门**](https://yq.aliyun.com/articles/682761)。
2. 要消费日志服务中的数据，请尽量不要直接使用SDK的拉数据接口，我们提供了一个高级消费库 [**Consumer Library**](https://help.aliyun.com/document_detail/28998.html)，该库屏蔽了日志服务的实现细节，并且提供了负载均衡、按序消费等高级功能。

### sample 1 : 构建client
```

String accessId = "your_access_id";
String accessKey = "your_access_key";
String host = "your_endpoint";
Client client = new Client(host, accessId, accessKey);

```

### sample 2 : 创建Logstore
```

String project = "your_project_name";
String logstore = "your_logstore";
int ttl_in_day = 3;
int shard_count = 10;
LogStore store = new LogStore(logstore, ttl_in_day, shard_count);
CreateLogStoreResponse res = client.CreateLogStore(project, store);

```

### sample 3 : 写数据
```

int numLogGroup = 10;
/**
 * 向log service发送一个日志包，每个日志包中，有2行日志
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

### sample 4 : 读取数据
```

int shardId = 0;  // 只读取0号shard的数据
GetCursorResponse res;
try {
    // 获取最近1个小时接收到的第一批日志的cursor位置
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
    // 读取到的数据
    List<LogGroupData> logGroups = logDataRes.GetLogGroups();

    String nextCursor = logDataRes.GetNextCursor();  // 下次读取的位置
    System.out.print("The Next cursor:" + nextCursor);
    if (cursor.equals(nextCursor)) {
        break;
    }
    cursor = nextCursor;
}

```

## Maven配置
```
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.64</version>
</dependency>
```

## protobuf 冲突
可以使用 Aliyun LOG java SDK 提供的一个特殊版本
```
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>aliyun-log</artifactId>
    <version>0.6.64</version>
    <classifier>jar-with-dependencies</classifier>
    <exclusions>
        <exclusion>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## FAQ
**Q**: `aliyun-log-java-sdk` 和 `aliyun-sls-xxx-inner` 版本冲突的问题及解决方案。

**A**: 这两个 jar 包不能共存于一个项目中，如果您发现您依赖的某个 jar 包引入了 `aliyun-sls-xxx-inner`，请手动排除。
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
