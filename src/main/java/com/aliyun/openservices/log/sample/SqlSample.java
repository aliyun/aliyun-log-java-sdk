package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.response.GetLogsResponse;

public class SqlSample {

    private static final String ENDPOINT = "which endpoint your project in";
    private static final String ACCESS_KEY_ID = "";
    private static final String ACCESS_KEY = "";
    private static final Client client = new Client(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY);

    private static final String PROJECT = "your-project";
    private static final String LOGSTORE = "your-logstore";
    private static final int from = (int)(System.currentTimeMillis()/1000 - 900);
    private static final int to = (int)(System.currentTimeMillis()/1000);
    private static final String QUERY = "* | SELECT count(1) pv FROM log";

    // 开启完全精确SQL
    public static void testFullCompleteSql() throws LogException {
        GetLogsRequest request = new GetLogsRequest(PROJECT, LOGSTORE, from, to, "", QUERY);
        request.SetSession("allow_incomplete=false");
        GetLogsResponse response = client.GetLogs(request);
        System.out.println("RequestId:" + response.GetRequestId());
        System.out.println("Complete:" + response.IsCompleted());
    }

    // 指定SQL最大执行时间（防止某个大Query占用过多资源，影响其他Query执行）
    public static void testSqlMaxRunTime() throws LogException {
        System.out.println("from:" + from);
        System.out.println("to:" + to);
        GetLogsRequest request = new GetLogsRequest(PROJECT, LOGSTORE, from, to, "", QUERY);
        request.SetSession("query_max_run_time=100ms");
        GetLogsResponse response = client.GetLogs(request);
        System.out.println("RequestId:" + response.GetRequestId());
        System.out.println("ElapsedMilliSecond:" + response.getElapsedMilliSecond());
    }

    public static void main(String[] args) throws LogException {
        testFullCompleteSql();
        testSqlMaxRunTime();
    }
}
