package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.request.CreateMaterializedViewRequest;
import com.aliyun.openservices.log.request.ListMaterializedViewsRequest;
import com.aliyun.openservices.log.response.ListMaterializedViewsResponse;

public class MaterializedViewTest
{
    private static final Client CLIENT = new Client(
            "cn-hangzhou-devcommon-intranet.sls.aliyuncs.com",
            System.getProperty("accessId"),
            System.getProperty("accessKey")
    );

    private static final String PROJECT_NAME = "chuzhi-presto-test";

    public static void main(String[] args) throws Exception {
        testCreateMv();
        // testDeleteMv();
        testLocalListMVs();
    }

    public static void testCreateMv() throws Exception {
        String logstore = "perf_test_1";

        CreateMaterializedViewRequest request = new CreateMaterializedViewRequest(
                PROJECT_NAME,
                "perf_test_1_mv_7",
                logstore,
                "not xyz | SELECT batchId % 10 as g, count(*) from log group by g",
                5,
                (int)(System.currentTimeMillis() / 1000),
                9
        );
        CLIENT.createMaterializedView(request);
    }

    public static void testCreateMvWithTS() throws Exception {
        String logstore = "perf_test_1";

        CreateMaterializedViewRequest request = new CreateMaterializedViewRequest(
                PROJECT_NAME,
                "perf_test_1_mv_8",
                logstore,
                "not xyz | select __time__ - __time__ % 600 as TGroup, max(from_unixtime(__time__)) as UT, max(from_unixtime(__time__, 'Asia/Shanghai')) as UTZ, max(date(from_unixtime(__time__))) as DT from log group by TGroup",
                5,
                (int)(System.currentTimeMillis() / 1000),
                9
        );
        CLIENT.createMaterializedView(request);
    }

    public static void testDeleteMv() throws Exception {
        CLIENT.deleteMaterializedView(PROJECT_NAME, "perf_test_5_mv");
    }

    public static void testLocalListMVs() throws Exception {
        ListMaterializedViewsRequest request = new ListMaterializedViewsRequest(PROJECT_NAME, "", 0, 100);
        ListMaterializedViewsResponse response = CLIENT.listMaterializedViews(request);
        System.out.println("materializedViews: " + response.getMaterializedViews());
    }
}
