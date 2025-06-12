package com.aliyun.openservices.log.functiontest.metricstore;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.MetricDownSamplingConfig;
import com.aliyun.openservices.log.common.MetricRemoteWriteConfig;
import com.aliyun.openservices.log.common.MetricDownSamplingConfig.MetricDownSamplingStatus;
import com.aliyun.openservices.log.common.MetricParallelConfig;
import com.aliyun.openservices.log.common.MetricsConfig;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.logstore.FunctionTest;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.GetMetricsConfigResponse;
import com.aliyun.openservices.log.response.ListMetricsConfigResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MetricsConfigFunctionTest extends FunctionTest {
    static String PROJECTEXIST = makeProjectName();
    static String PROJECTNOTEXIST = makeProjectName();
    static String LOGSTOREEXIST = "logstore_exist";
    static String METRICSEXIST = "metrics_exist";
    static String METRICSNOTEXIST = "metrics_notexist";
    static String CONFIG_STRING = "{\n"
        + "    \"query_cache_config\" : {\n"
        + "        \"enable\" : true\n"
        + "    },\n"
        + "    \"parallel_config\" : {\n"
        + "        \"enable\" : true,\n"
        + "        \"mode\" : \"static\",\n"
        + "        \"time_piece_interval\" : 86400,\n"
        + "        \"time_piece_count\" : 8,\n"
        + "        \"parallel_count_per_host\" : 2,\n"
        + "        \"total_parallel_count\" : 64\n"
        + "    },\n"
        + "    \"pushdown_config\" : {\n"
        + "        \"enable\" : true\n"
        + "    },\n"
        + "    \"remote_write_config\" : {\n"
        + "        \"enable\" : true,\n"
        + "        \"history_interval\" : 500,\n"
        + "        \"future_interval\" : 600,\n"
        + "        \"replica_field\" : \"xzz_test\",\n"
        + "        \"replica_timeout_seconds\" : 30\n"
        + "    },\n"
        + "    \"downsampling_config\": {\n"
        + "        \"base\": {\n"
        + "            \"create_time\": 12345678901,\n"
        + "            \"ttl\": 7,\n"
        + "            \"resolution_seconds\": 30\n"
        + "        },\n"
        + "        \"downsampling\": [\n"
        + "            {\n"
        + "                \"create_time\": 12345678903,\n"
        + "                \"ttl\": 365,\n"
        + "                \"resolution_seconds\": 3600\n"
        + "            },\n"
        + "            {\n"
        + "                \"create_time\": 12345678904,\n"
        + "                \"ttl\": 3650,\n"
        + "                \"resolution_seconds\": 86400\n"
        + "            }\n"
        + "        ]\n"
        + "    }\n"
        + "}";
    static MetricsConfig CONFIG = JSONObject.parseObject(CONFIG_STRING, MetricsConfig.class);
    static String CONFIGWRONG_STRING = "wrong";
    static String PROJECTNOTEXISTERROR = "ProjectNotExist";
    static String LOGSTORENOTEXISTERROR = "LogStoreNotExist";
    static String PARAMETERINVALID = "ParameterInvalid";
    static String METRICSCONFIGNOTEXIST = "MetricsConfigNotExist";
    static String METRICSCONFIGALREADYEXIST = "MetricsConfigAlreadyExist";
    static String NOTSUPPORTED = "NotSupported";

    @Before
    public void setUp() throws LogException, InterruptedException {
        client.CreateProject(PROJECTEXIST, "");
        Thread.sleep(1000 * 10);
        client.createMetricStore(PROJECTEXIST, new LogStore(METRICSEXIST, 1, 1));
        Thread.sleep(1000 * 10);
        client.CreateLogStore(PROJECTEXIST, new LogStore(LOGSTOREEXIST, 1, 1));
        Thread.sleep(1000 * 10);
    }

    @After
    public void clearData() throws LogException {
        client.DeleteProject(PROJECTEXIST);
    }

    @Test
    public void testMetricsConfigValue() {
        Assert.assertTrue(CONFIG.getQueryCacheConfig().isEnable());
        MetricParallelConfig parallelConfig = CONFIG.getParallelConfig();
        Assert.assertTrue(CONFIG.getPushdownConfig().isEnable());
        Assert.assertTrue(parallelConfig.isEnable());
        Assert.assertEquals(parallelConfig.getMode(), "static");
        Assert.assertEquals(parallelConfig.getTimePieceInterval(), 86400);
        Assert.assertEquals(parallelConfig.getTimePieceCount(), 8);
        Assert.assertEquals(parallelConfig.getParallelCountPerHost(), 2);
        Assert.assertEquals(parallelConfig.getTotalParallelCount(), 64);
        MetricRemoteWriteConfig remoteWriteConfig = CONFIG.getRemoteWriteConfig();
        Assert.assertEquals(remoteWriteConfig.getHistoryInterval(), 500);
        Assert.assertEquals(remoteWriteConfig.getFutureInterval(), 600);
        Assert.assertEquals(remoteWriteConfig.getReplicaField(), "xzz_test");
        Assert.assertEquals(remoteWriteConfig.getReplicaTimeoutSeconds(), 30);
        MetricDownSamplingConfig downSamplingConfig = CONFIG.getDownSamplingConfig();
        MetricDownSamplingStatus base = downSamplingConfig.getBase();
        List<MetricDownSamplingStatus> downsampling = downSamplingConfig.getDownsampling();

        Assert.assertEquals(base.getTtl(), 7);
        Assert.assertEquals(base.getResolutionSeconds(), 30);
        Assert.assertEquals(base.getCreateTime(), 12345678901L);
        Assert.assertEquals(downsampling.size(), 2);
        Assert.assertEquals(downsampling.get(1).getTtl(), 3650);
        Assert.assertEquals(downsampling.get(1).getResolutionSeconds(), 86400);
        Assert.assertEquals(downsampling.get(1).getCreateTime(), 12345678904L);
    }

    @Test
    public void testCreateMetricsConfig() {
        try {
            client.createMetricsConfig(new CreateMetricsConfigRequest(PROJECTNOTEXIST, METRICSEXIST, CONFIG));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(PROJECTNOTEXISTERROR, e.GetErrorCode());
        }
        try {
            client.createMetricsConfig(new CreateMetricsConfigRequest(PROJECTEXIST, LOGSTOREEXIST, CONFIG));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(NOTSUPPORTED, e.GetErrorCode());
        }
        try {
            client.createMetricsConfig(new CreateMetricsConfigRequest(PROJECTEXIST, METRICSNOTEXIST, CONFIG));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(LOGSTORENOTEXISTERROR, e.GetErrorCode());
        }
//        try {
//            client.createMetricsConfig(new CreateMetricsConfigRequest(PROJECTEXIST, METRICSEXIST, CONFIGWRONG));
//            Assert.fail("should fail");
//        } catch (LogException e) {
//            Assert.assertEquals(PARAMETERINVALID, e.GetErrorCode());
//        }

        try {
            client.createMetricsConfig(new CreateMetricsConfigRequest(PROJECTEXIST, METRICSEXIST, CONFIG));
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
        try {
            client.createMetricsConfig(new CreateMetricsConfigRequest(PROJECTEXIST, METRICSEXIST, CONFIG));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(METRICSCONFIGALREADYEXIST, e.GetErrorCode());
        }
        try {
            client.deleteMetricsConfig(new DeleteMetricsConfigRequest(PROJECTEXIST, METRICSEXIST));
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
    }

    @Test
    public void testUpdateMetricsConfig() {
        try {
            client.updateMetricsConfig(new UpdateMetricsConfigRequest(PROJECTNOTEXIST, METRICSEXIST, CONFIG));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(PROJECTNOTEXISTERROR, e.GetErrorCode());
        }
        try {
            client.updateMetricsConfig(new UpdateMetricsConfigRequest(PROJECTEXIST, LOGSTOREEXIST, CONFIG));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(NOTSUPPORTED, e.GetErrorCode());
        }
        try {
            client.updateMetricsConfig(new UpdateMetricsConfigRequest(PROJECTEXIST, METRICSNOTEXIST, CONFIG));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(LOGSTORENOTEXISTERROR, e.GetErrorCode());
        }
//        try {
//            client.updateMetricsConfig(new UpdateMetricsConfigRequest(PROJECTEXIST, METRICSEXIST, CONFIGWRONG));
//            Assert.fail("should fail");
//        } catch (LogException e) {
//            Assert.assertEquals(PARAMETERINVALID, e.GetErrorCode());
//        }
        try {
            client.updateMetricsConfig(new UpdateMetricsConfigRequest(PROJECTEXIST, METRICSEXIST, CONFIG));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(METRICSCONFIGNOTEXIST, e.GetErrorCode());
        }

        try {
            client.createMetricsConfig(new CreateMetricsConfigRequest(PROJECTEXIST, METRICSEXIST, CONFIG));
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
        try {
            client.updateMetricsConfig(new UpdateMetricsConfigRequest(PROJECTEXIST, METRICSEXIST, CONFIG));
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
        try {
            client.deleteMetricsConfig(new DeleteMetricsConfigRequest(PROJECTEXIST, METRICSEXIST));
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
    }

    @Test
    public void testDeleteMetricsConfig() {
        try {
            client.deleteMetricsConfig(new DeleteMetricsConfigRequest(PROJECTNOTEXIST, METRICSEXIST));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(PROJECTNOTEXISTERROR, e.GetErrorCode());
        }
        try {
            client.deleteMetricsConfig(new DeleteMetricsConfigRequest(PROJECTEXIST, LOGSTOREEXIST));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(NOTSUPPORTED, e.GetErrorCode());
        }
        try {
            client.deleteMetricsConfig(new DeleteMetricsConfigRequest(PROJECTEXIST, METRICSNOTEXIST));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(LOGSTORENOTEXISTERROR, e.GetErrorCode());
        }
        try {
            client.deleteMetricsConfig(new DeleteMetricsConfigRequest(PROJECTEXIST, METRICSEXIST));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(METRICSCONFIGNOTEXIST, e.GetErrorCode());
        }
    }

    @Test
    public void testGetMetricsConfig() {
        try {
            client.getMetricsConfig(new GetMetricsConfigRequest(PROJECTNOTEXIST, METRICSEXIST));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(PROJECTNOTEXISTERROR, e.GetErrorCode());
        }
        try {
            client.getMetricsConfig(new GetMetricsConfigRequest(PROJECTEXIST, LOGSTOREEXIST));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(NOTSUPPORTED, e.GetErrorCode());
        }
        try {
            client.getMetricsConfig(new GetMetricsConfigRequest(PROJECTEXIST, METRICSNOTEXIST));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(LOGSTORENOTEXISTERROR, e.GetErrorCode());
        }
        try {
            client.getMetricsConfig(new GetMetricsConfigRequest(PROJECTEXIST, METRICSEXIST));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(METRICSCONFIGNOTEXIST, e.GetErrorCode());
        }

        try {
            client.createMetricsConfig(new CreateMetricsConfigRequest(PROJECTEXIST, METRICSEXIST, CONFIG));
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
        try {
            GetMetricsConfigResponse response = client
                    .getMetricsConfig(new GetMetricsConfigRequest(PROJECTEXIST, METRICSEXIST));
            // todo: fix this, override equals
            // boolean isEquals = CONFIG.equals(response.getMetricsConfig());
            boolean isEquals = isConfigEquals(CONFIG, response.getMetricsConfig());
            Assert.assertTrue(isEquals);
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
        try {
            client.deleteMetricsConfig(new DeleteMetricsConfigRequest(PROJECTEXIST, METRICSEXIST));
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
    }

    @Test
    public void testListMetricsConfig() {
        try {
            client.listMetricsConfig(new ListMetricsConfigRequest(PROJECTNOTEXIST));
            Assert.fail("should fail");
        } catch (LogException e) {
            Assert.assertEquals(PROJECTNOTEXISTERROR, e.GetErrorCode());
        }

        try {
            ListMetricsConfigResponse response = client.listMetricsConfig(new ListMetricsConfigRequest(PROJECTEXIST));
            Assert.assertEquals(response.getMetricsConfigList().size(), 0);
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
        try {
            client.createMetricsConfig(new CreateMetricsConfigRequest(PROJECTEXIST, METRICSEXIST, CONFIG));
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
        try {
            ListMetricsConfigResponse response = client.listMetricsConfig(new ListMetricsConfigRequest(PROJECTEXIST));
            Assert.assertEquals(response.getMetricsConfigList().size(), 1);
            ListMetricsConfigResponse.MetricsConfigWrap metricsConfigWrap = response.getMetricsConfigList().get(0);
            Assert.assertEquals(metricsConfigWrap.getMetricStore(), METRICSEXIST);
            // todo: fix this, override equals
            // boolean isEquals = CONFIG.equals(metricsConfigWrap.getMetricsConfig());
            boolean isEquals = isConfigEquals(CONFIG, metricsConfigWrap.getMetricsConfig());
            Assert.assertTrue(isEquals);

            response = client.listMetricsConfig(new ListMetricsConfigRequest(PROJECTEXIST, 2, 5));
            Assert.assertEquals(response.getMetricsConfigList().size(), 0);

            response = client.listMetricsConfig(new ListMetricsConfigRequest(PROJECTEXIST, 0, 5, "wrong"));
            Assert.assertEquals(response.getMetricsConfigList().size(), 0);
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
    }

    private static boolean isConfigEquals(MetricsConfig expected, MetricsConfig actual) {
        if (expected == actual) {
            return true;
        }
        if (null == expected || null == actual) {
            return false;
        }
        return JSON.toJSONString(expected).equals(JSON.toJSONString(actual));

    }
}
