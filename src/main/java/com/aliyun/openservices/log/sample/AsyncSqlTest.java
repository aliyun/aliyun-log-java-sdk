package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.request.DeleteAsyncSqlRequest;
import com.aliyun.openservices.log.request.GetAsyncSqlRequest;
import com.aliyun.openservices.log.response.GetAsyncSqlResponse;
import com.aliyun.openservices.log.request.SubmitAsyncSqlRequest;
import com.aliyun.openservices.log.response.SubmitAsyncSqlResponse;

import java.util.concurrent.TimeUnit;

public class AsyncSqlTest {

    public static void main(String[] args) throws Exception {
        String accessId = System.getProperty("accessId");
        String accessKey = System.getProperty("accessKey");

        String host = "cn-hangzhou-devcommon-intranet.sls.aliyuncs.com";
        String project = "chuzhi-presto-test";
        String logstore = "perf_test_1";

        int queryRange = 10 * 360 * 24 * 24 * 60;
        int end = (int) (System.currentTimeMillis() / 1000);

        Client client = new Client(host, accessId, accessKey);
        SubmitAsyncSqlRequest request = new SubmitAsyncSqlRequest(
                project,
                logstore,
                "* | select key_0 as KEY0, long_0 as LONG0, double_0, double_2 from log limit 100",
                end - queryRange,
                end);
        SubmitAsyncSqlResponse response = client.submitAsyncSql(request);
        System.out.println(response.getQueryId() + ", " + response.isRunning() + ", " + response.getErrorCode());

        GetAsyncSqlRequest getReq = new GetAsyncSqlRequest(project, response.getQueryId(), 0, 0);
        GetAsyncSqlResponse getRsp = null;
        while(true) {
            // 这里可能抛出异常, 比如网络不可用或者服务端busy之类的, 需要考虑重试getAsyncSql
            getRsp = client.getAsyncSql(getReq);

            if (getRsp.isFailed()) {
                // SQL执行已经失败(不需要再重试getAsyncSql), 需要根据具体的ErrorCode来决定是否需要重新发起全新的SQL请求来进行retry
                throw new RuntimeException("async SQL failed, " + getRsp.getErrorCode() + ", " + getRsp.getErrorMessage());
            }

            if (getRsp.isSuccessful()) {
                break;
            }
            System.out.println("async sql not ready yet");
            TimeUnit.SECONDS.sleep(1);
        }

        System.out.println("total result rows: " + getRsp.getMeta().getResultRows() + ", " + getRsp.getRows().size());
        System.out.println("column names: " + getRsp.getColumnNames());

        final int batchSize = 10;
        for (int offset = 0; offset < getRsp.getMeta().getResultRows(); offset += getRsp.getRows().size()) {
            getReq = new GetAsyncSqlRequest(project, response.getQueryId(), offset, batchSize);
            getRsp = client.getAsyncSql(getReq);

            System.out.println("offset: " + offset + ", " + getRsp.getRows().size());
            getRsp.getRows().forEach(System.out::println);

            if (getRsp.getRows().isEmpty()) {
                break;
            }
        }

        DeleteAsyncSqlRequest deleteReq = new DeleteAsyncSqlRequest(project, response.getQueryId());
        client.deleteAsyncSql(deleteReq);
    }

}
