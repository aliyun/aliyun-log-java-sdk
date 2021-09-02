package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.MetricAggRuleItem;
import com.aliyun.openservices.log.common.MetricAggRules;
import com.aliyun.openservices.log.common.SubStore;
import com.aliyun.openservices.log.common.SubStoreKey;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateMetricAggRulesRequest;
import com.aliyun.openservices.log.request.DeleteMetricAggRulesRequest;
import com.aliyun.openservices.log.request.GetMetricAggRulesRequest;
import com.aliyun.openservices.log.request.ListMetricAggRulesRequest;
import com.aliyun.openservices.log.request.UpdateMetricAggRulesRequest;
import com.aliyun.openservices.log.response.CreateMetricAggRulesResponse;
import com.aliyun.openservices.log.response.DeleteMetricAggRulesResponse;
import com.aliyun.openservices.log.response.GetMetricAggRulesResponse;
import com.aliyun.openservices.log.response.ListMetricAggRulesResponse;
import com.aliyun.openservices.log.response.UpdateMetricAggRulesResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MetricAggRulesTest extends FunctionTest {
    private static final String TEST_PROJECT = "test-metric-agg-project-" + getNowTimestamp();
    private static final String TEST_LOG_STORE = "test_logstore";
    private static final String TEST_METRIC_STORE = "test_metricstore";
    private static final String TEST_METRIC_STORE1 = "test_metricstore1";

    @Before
    public void setUp() throws LogException {
        safeCreateProject(TEST_PROJECT, "test metric_agg_rules");

        client.CreateLogStore(TEST_PROJECT, new LogStore(TEST_LOG_STORE, 1, 2));

        List<SubStoreKey> list = new ArrayList<SubStoreKey>();
        list.add(new SubStoreKey("__name__", "text"));
        list.add(new SubStoreKey("__labels__", "text"));
        list.add(new SubStoreKey("__time_nano__", "long"));
        list.add(new SubStoreKey("__value__", "double"));
        SubStore subStore = new SubStore("prom", 30, 2, 2, list);

        LogStore metricStore = new LogStore(TEST_METRIC_STORE, 30, 2);
        metricStore.setTelemetryType("Metrics");
        client.CreateLogStore(TEST_PROJECT, metricStore);
        client.createSubStore(TEST_PROJECT, TEST_METRIC_STORE, subStore);

        LogStore metricStore1 = new LogStore(TEST_METRIC_STORE1, 30, 2);
        metricStore.setTelemetryType("Metrics");
        client.CreateLogStore(TEST_PROJECT, metricStore1);
        client.createSubStore(TEST_PROJECT, TEST_METRIC_STORE1, subStore);
    }

    @After
    public void clearData() {
        safeDeleteLogStore(TEST_PROJECT, TEST_LOG_STORE);
        safeDeleteLogStore(TEST_PROJECT, TEST_METRIC_STORE);
        safeDeleteLogStore(TEST_PROJECT, TEST_METRIC_STORE1);
        safeDeleteProjectWithoutSleep(TEST_PROJECT);
    }

    @Test
    public void testCrud() throws LogException {
        String testId = "metric_agg_rules_sql";
        MetricAggRules metricAggRules = createSqlConfig(testId);
        crud(testId, metricAggRules);

        testId = "metric_agg_rules_promql";
        metricAggRules = createPromqlConfig(testId);
        crud(testId, metricAggRules);

        int t = randomBetween(1, 10);
        for (int i = 0; i < t; i++) {
            String id = "metric_agg_rules_" + i;
            MetricAggRules m = createSqlConfig(id);
            testCreateMetricAggRules(m);
        }
        ListMetricAggRulesResponse listMetricAggRulesResponse = testListMetricAggRules();
        assertNotNull(listMetricAggRulesResponse);
        assertEquals(t, listMetricAggRulesResponse.getMetricAggRules().size());
    }

    private void crud(String testId, MetricAggRules metricAggRules) throws LogException {
        CreateMetricAggRulesResponse createMetricAggRulesResponse = testCreateMetricAggRules(metricAggRules);
        assertNotNull(createMetricAggRulesResponse);

        GetMetricAggRulesResponse getMetricAggRulesResponse = testGetMetricAggRules(testId);
        MetricAggRules metricAggRules1 = getMetricAggRulesResponse.getMetricAggRules();
        assertNotNull(createMetricAggRulesResponse);
        assertEquals(metricAggRules.getName(), metricAggRules1.getName());
        assertEquals(metricAggRules.getDesc(), metricAggRules1.getDesc());
        assertEquals(metricAggRules.getDestAccessKeyID(), metricAggRules1.getDestAccessKeyID());
        assertEquals(metricAggRules.getDestAccessKeySecret(), metricAggRules1.getDestAccessKeySecret());
        assertEquals(metricAggRules.getDestEndpoint(), metricAggRules1.getDestEndpoint());
        assertEquals(metricAggRules.getDestProject(), metricAggRules1.getDestProject());
        assertEquals(metricAggRules.getDestStore(), metricAggRules1.getDestStore());
        assertEquals(metricAggRules.getSrcAccessKeyID(), metricAggRules1.getSrcAccessKeyID());
        assertEquals(metricAggRules.getSrcAccessKeySecret(), metricAggRules1.getSrcAccessKeySecret());
        assertEquals(metricAggRules.getSrcStore(), metricAggRules1.getSrcStore());
        assertEquals(metricAggRules.getAggRules()[0], metricAggRules1.getAggRules()[0]);

        metricAggRules.setDesc("test_update");
        metricAggRules.setDestStore(TEST_METRIC_STORE1);
        metricAggRules.setSrcStore(TEST_METRIC_STORE);
        MetricAggRuleItem metricAggRuleItem = metricAggRules.getAggRules()[0];
        metricAggRuleItem.setBeginUnixTime(1610506300);
        metricAggRuleItem.setEndUnixTime(1610516300);
        metricAggRuleItem.setInterval(120);
        metricAggRuleItem.setDelaySeconds(120);
        metricAggRuleItem.setQueryType("sql");
        metricAggRuleItem.setQuery("* | select max(__time__) as time, COUNT_if(Status < 300) as ok1, count_if(Status >= 300) as not_ok1, Method as method1,UserAgent as agent1 from log  group by method1, agent1 limit 100000");
        metricAggRuleItem.setMetricNames(new String[]{
                "ok1",
                "not_ok1"
        });
        Map<String, String> map = new HashMap<String, String>();
        map.put("method1", "method1");
        map.put("agent1", "agent1");
        metricAggRuleItem.setLabelNames(map);
        metricAggRules.setAggRules(new MetricAggRuleItem[]{metricAggRuleItem});
        UpdateMetricAggRulesResponse updateMetricAggRulesResponse = testUpdateMetricAggRules(metricAggRules);
        assertNotNull(updateMetricAggRulesResponse);

        GetMetricAggRulesResponse getMetricAggRulesResponse1 = testGetMetricAggRules(testId);
        MetricAggRules metricAggRules2 = getMetricAggRulesResponse1.getMetricAggRules();
        assertNotNull(getMetricAggRulesResponse1);
        assertEquals(metricAggRules.getDesc(), metricAggRules2.getDesc());
        assertEquals(metricAggRules.getName(), metricAggRules2.getName());
        assertEquals(metricAggRules.getDesc(), metricAggRules2.getDesc());
        assertEquals(metricAggRules.getDestAccessKeyID(), metricAggRules2.getDestAccessKeyID());
        assertEquals(metricAggRules.getDestAccessKeySecret(), metricAggRules2.getDestAccessKeySecret());
        assertEquals(metricAggRules.getDestEndpoint(), metricAggRules2.getDestEndpoint());
        assertEquals(metricAggRules.getDestProject(), metricAggRules2.getDestProject());
        assertEquals(metricAggRules.getDestStore(), metricAggRules2.getDestStore());
        assertEquals(metricAggRules.getSrcAccessKeyID(), metricAggRules2.getSrcAccessKeyID());
        assertEquals(metricAggRules.getSrcAccessKeySecret(), metricAggRules2.getSrcAccessKeySecret());
        assertEquals(metricAggRules.getSrcStore(), metricAggRules2.getSrcStore());
        assertEquals(metricAggRules.getAggRules()[0], metricAggRules2.getAggRules()[0]);

        DeleteMetricAggRulesResponse deleteMetricAggRulesResponse = testDeleteMetricAggRules(testId);
        assertNotNull(deleteMetricAggRulesResponse);
    }

    private CreateMetricAggRulesResponse testCreateMetricAggRules(MetricAggRules metricAggRules) throws LogException {
        return client.createMetricAggRules(new CreateMetricAggRulesRequest(TEST_PROJECT, metricAggRules));
    }

    private GetMetricAggRulesResponse testGetMetricAggRules(String testId) throws LogException {
        return client.getMetricAggRules(new GetMetricAggRulesRequest(TEST_PROJECT, testId));
    }

    private UpdateMetricAggRulesResponse testUpdateMetricAggRules(MetricAggRules metricAggRules) throws LogException {
        return client.updateMetricAggRules(new UpdateMetricAggRulesRequest(TEST_PROJECT, metricAggRules));
    }

    private ListMetricAggRulesResponse testListMetricAggRules() throws LogException {
        return client.listMetricAggRules(new ListMetricAggRulesRequest(TEST_PROJECT));
    }

    private DeleteMetricAggRulesResponse testDeleteMetricAggRules(String testId) throws LogException {
        return client.deleteMetricAggRules(new DeleteMetricAggRulesRequest(TEST_PROJECT, testId));
    }

    private MetricAggRules createSqlConfig(String testId) {
        MetricAggRuleItem metricAggRuleItem = new MetricAggRuleItem();
        metricAggRuleItem.setName(testId);
        metricAggRuleItem.setQueryType("sql");
        metricAggRuleItem.setQuery("* | select max(__time__) as time, COUNT_if(Status < 500) as success, count_if(Status >= 500) as fail, count(1) as total, InvokerUid as aliuid, Project as project, LogStore as logstore from log  group by InvokerUid, Project, LogStore limit 100000");
        metricAggRuleItem.setTimeName("time");
        metricAggRuleItem.setMetricNames(new String[]{
                "success",
                "fail",
                "total"
        });
        Map<String, String> map = new HashMap<String, String>();
        map.put("aliuid", "aliuid");
        map.put("logstore", "logstore");
        map.put("project", "project");
        metricAggRuleItem.setLabelNames(map);
        metricAggRuleItem.setBeginUnixTime(1610506297);
        metricAggRuleItem.setEndUnixTime(-1);
        metricAggRuleItem.setInterval(60);
        metricAggRuleItem.setDelaySeconds(60);

        MetricAggRuleItem metricAggRuleItem1 = new MetricAggRuleItem();
        metricAggRuleItem1.setName("testId2");
        metricAggRuleItem1.setQueryType("sql");
        metricAggRuleItem1.setQuery("* | select max(__time__) as time, COUNT_if(Status < 300) as ok, count_if(Status >= 300) as not_ok, Method as method,UserAgent as agent from log  group by method, agent limit 100000");
        metricAggRuleItem1.setTimeName("time");
        metricAggRuleItem1.setMetricNames(new String[]{
                "ok",
                "not_ok"
        });
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("method", "method");
        map1.put("agent", "agent");
        metricAggRuleItem1.setLabelNames(map1);
        metricAggRuleItem1.setBeginUnixTime(1610506297);
        metricAggRuleItem1.setEndUnixTime(-1);
        metricAggRuleItem1.setInterval(30);
        metricAggRuleItem1.setDelaySeconds(30);

        MetricAggRules metricAggRules = new MetricAggRules();
        metricAggRules.setId(testId);
        metricAggRules.setName(testId);
        metricAggRules.setDesc("测试CreateMetricAggRules");
        metricAggRules.setSrcStore(TEST_LOG_STORE);
        metricAggRules.setSrcAccessKeyID(credentials.getAccessKeyId());
        metricAggRules.setSrcAccessKeySecret(credentials.getAccessKey());
        metricAggRules.setDestEndpoint(credentials.getEndpoint());
        metricAggRules.setDestProject(TEST_PROJECT);
        metricAggRules.setDestStore(TEST_METRIC_STORE1);
        metricAggRules.setDestAccessKeyID(credentials.getAccessKeyId());
        metricAggRules.setDestAccessKeySecret(credentials.getAccessKey());
        metricAggRules.setAggRules(new MetricAggRuleItem[]{metricAggRuleItem, metricAggRuleItem1});
        return metricAggRules;
    }

    private MetricAggRules createPromqlConfig(String testId) {
        MetricAggRuleItem metricAggRuleItem = new MetricAggRuleItem();
        metricAggRuleItem.setName(testId);
        metricAggRuleItem.setQueryType("promql");
        metricAggRuleItem.setQuery("* | SELECT promql_query('sum(sum_over_time(total[1m]))') FROM  metrics limit 1000");
        metricAggRuleItem.setTimeName("time");
        metricAggRuleItem.setMetricNames(new String[]{
                "total_count",
        });
        Map<String, String> map = new HashMap<String, String>();
        metricAggRuleItem.setLabelNames(map);
        metricAggRuleItem.setBeginUnixTime(1610506297);
        metricAggRuleItem.setEndUnixTime(-1);
        metricAggRuleItem.setInterval(60);
        metricAggRuleItem.setDelaySeconds(60);

        MetricAggRules metricAggRules = new MetricAggRules();
        metricAggRules.setId(testId);
        metricAggRules.setName(testId);
        metricAggRules.setDesc("测试CreateMetricAggRules");
        metricAggRules.setSrcStore(TEST_METRIC_STORE);
        metricAggRules.setSrcAccessKeyID(credentials.getAccessKeyId());
        metricAggRules.setSrcAccessKeySecret(credentials.getAccessKey());
        metricAggRules.setDestEndpoint(credentials.getEndpoint());
        metricAggRules.setDestProject(TEST_PROJECT);
        metricAggRules.setDestStore(TEST_METRIC_STORE1);
        metricAggRules.setDestAccessKeyID(credentials.getAccessKeyId());
        metricAggRules.setDestAccessKeySecret(credentials.getEndpoint());
        metricAggRules.setAggRules(new MetricAggRuleItem[]{metricAggRuleItem});
        return metricAggRules;
    }
}
