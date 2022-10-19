package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetLogStoreRequest;
import com.aliyun.openservices.log.request.UpdateLogStoreRequest;
import com.aliyun.openservices.log.response.CreateLogStoreResponse;
import com.aliyun.openservices.log.response.DeleteLogStoreResponse;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import com.aliyun.openservices.log.response.UpdateLogStoreResponse;
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
        LogStore metricStore = new LogStore(METRICSTORE, 30, 2);
        CreateLogStoreResponse createMetricStoreRes = client.createMetricStore(PROJECT, metricStore);
        assertNotNull(createMetricStoreRes);

        GetLogStoreResponse getMetricStoreRes = client.getMetricStore(new GetLogStoreRequest(PROJECT, METRICSTORE));
        assertNotNull(getMetricStoreRes);
        LogStore metricStore1 = getMetricStoreRes.GetLogStore();
        assertEquals(30, metricStore1.GetTtl());

        metricStore.SetTtl(15);
        UpdateLogStoreResponse updateMetricStoreRes = client.updateMetricStore(new UpdateLogStoreRequest(PROJECT, metricStore));
        assertNotNull(updateMetricStoreRes);

        getMetricStoreRes = client.getMetricStore(new GetLogStoreRequest(PROJECT, METRICSTORE));
        assertNotNull(getMetricStoreRes);
        metricStore = getMetricStoreRes.GetLogStore();
        assertEquals(15, metricStore.GetTtl());

        DeleteLogStoreResponse deleteMetricStoreRes = client.deleteMetricStore(PROJECT, METRICSTORE);
        assertNotNull(deleteMetricStoreRes);
    }
}
