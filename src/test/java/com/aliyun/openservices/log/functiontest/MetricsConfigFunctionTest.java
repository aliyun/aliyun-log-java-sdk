package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.MetricsConfig;
import com.aliyun.openservices.log.exception.LogException;
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
    static String CONFIG_STRING = "{\"project\":\"xzz-test\",\"metricStore\":\"xzz-test-metric\",\"downSamplingConfig\":{\"base\":{\"create_time\":12345678901,\"ttl\":7,\"resolution_seconds\":30},\"downsampling\":[{\"create_time\":12345678902,\"ttl\":30,\"resolution_seconds\":300},{\"create_time\":12345678903,\"ttl\":365,\"resolution_seconds\":3600},{\"create_time\":12345678904,\"ttl\":3650,\"resolution_seconds\":86400}]}}";
    static MetricsConfig CONFIG = JSONObject.parseObject(CONFIG_STRING, MetricsConfig.class);
    static String CONFIGWRONG_STRING = "wrong";
    static String PROJECTNOTEXISTERROR = "ProjectNotExist";
    static String LOGSTORENOTEXISTERROR = "LogStoreNotExist";
    static String PARAMETERINVALID = "ParameterInvalid";
    static String METRICSCONFIGNOTEXIST = "MetricsConfigNotExist";
    static String METRICSCONFIGALREADYEXIST = "MetricsConfigAlreadyExist";

    @Before
    public void setUp() throws LogException {
        client.CreateProject(PROJECTEXIST, "");
        client.createMetricStore(PROJECTEXIST, new LogStore(METRICSEXIST, 1, 1));
        client.CreateLogStore(PROJECTEXIST, new LogStore(LOGSTOREEXIST, 1, 1));
    }

    @After
    public void clearData() throws LogException {
        client.DeleteProject(PROJECTEXIST);
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
            Assert.assertEquals(PARAMETERINVALID, e.GetErrorCode());
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
            Assert.assertEquals(PARAMETERINVALID, e.GetErrorCode());
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
            Assert.assertEquals(PARAMETERINVALID, e.GetErrorCode());
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
            Assert.assertEquals(PARAMETERINVALID, e.GetErrorCode());
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
            GetMetricsConfigResponse response = client.getMetricsConfig(new GetMetricsConfigRequest(PROJECTEXIST, METRICSEXIST));
            boolean isEquals = CONFIG.equals(response.getMetricsConfig());
            Assert.assertTrue(isEquals);
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
            boolean isEquals = CONFIG.equals(metricsConfigWrap.getMetricsConfig());
            Assert.assertTrue(isEquals);

            response = client.listMetricsConfig(new ListMetricsConfigRequest(PROJECTEXIST, 2, 5));
            Assert.assertEquals(response.getMetricsConfigList().size(), 0);

            response = client.listMetricsConfig(new ListMetricsConfigRequest(PROJECTEXIST, 0, 5, "wrong"));
            Assert.assertEquals(response.getMetricsConfigList().size(), 0);
        } catch (LogException e) {
            Assert.fail("should not fail");
        }
    }
}
