package com.aliyun.openservices.log.functiontest.metricstore;


import com.aliyun.openservices.log.common.MetricStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.logstore.FunctionTest;
import com.aliyun.openservices.log.request.CreateMetricStoreRequest;
import com.aliyun.openservices.log.request.DeleteMetricStoreRequest;
import com.aliyun.openservices.log.request.GetMetricStoreRequest;
import com.aliyun.openservices.log.request.UpdateMetricStoreRequest;
import com.aliyun.openservices.log.response.CreateMetricStoreResponse;
import com.aliyun.openservices.log.response.DeleteMetricStoreResponse;
import com.aliyun.openservices.log.response.GetMetricStoreResponse;
import com.aliyun.openservices.log.response.UpdateMetricStoreResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MetricStoreTest extends FunctionTest {
    static int timestamp = getNowTimestamp();
    static String PROJECT = makeProjectName();
    static String METRICSTORE = "test-metric-store-" + timestamp;

    @Before
    public void setUp() {
        safeCreateProject(PROJECT, "metric store test");
    }

    @After
    public void clearData() {
        safeDeleteProjectWithoutSleep(PROJECT);
    }

    @Test
    public void CRUDMetricStore() throws LogException {
        MetricStore metricStore = new MetricStore(METRICSTORE, 30, 2);
        CreateMetricStoreResponse createMetricStoreRes = client.createMetricStore(new CreateMetricStoreRequest(PROJECT, metricStore));
        assertNotNull(createMetricStoreRes);

        GetMetricStoreResponse getMetricStoreRes = client.getMetricStore(new GetMetricStoreRequest(PROJECT, METRICSTORE));
        assertNotNull(getMetricStoreRes);
        MetricStore metricStore1 = getMetricStoreRes.getMetricStore();
        assertEquals(30, metricStore1.getTtl());

        metricStore.setTtl(15);
        UpdateMetricStoreResponse updateMetricStoreRes = client.updateMetricStore(new UpdateMetricStoreRequest(PROJECT, metricStore));
        assertNotNull(updateMetricStoreRes);

        getMetricStoreRes = client.getMetricStore(new GetMetricStoreRequest(PROJECT, METRICSTORE));
        assertNotNull(getMetricStoreRes);
        metricStore = getMetricStoreRes.getMetricStore();
        assertEquals(15, metricStore.getTtl());

        DeleteMetricStoreResponse deleteMetricStoreRes = client.deleteMetricStore(new DeleteMetricStoreRequest(PROJECT, METRICSTORE));
        assertNotNull(deleteMetricStoreRes);
    }
}
