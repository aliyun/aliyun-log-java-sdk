package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetLogStoreMeteringModeRequest;
import com.aliyun.openservices.log.request.UpdateLogStoreMeteringModeRequest;
import com.aliyun.openservices.log.response.GetLogStoreMeteringModeResponse;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import org.junit.Assert;
import org.junit.Test;

public class UpdateLogStoreMeteringModeFunctionTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testCreateLogStore() throws Exception {
        String logstoreName = "log1";
        LogStore logStore = new LogStore(logstoreName, 1, 1);
        cleanUp(logstoreName);
        client.CreateLogStore(TEST_PROJECT, logStore);
        GetLogStoreResponse getLogStoreResponse = client.GetLogStore(TEST_PROJECT, logstoreName);
        Assert.assertEquals(getLogStoreResponse.GetLogStore().getMode(), "standard");
        GetLogStoreMeteringModeResponse response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_FUNCTION);

        // metering mode not changed is OK
        client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName, Consts.CHARGE_BY_FUNCTION));

        String logstoreName2 = "log2";
        LogStore logStore2 = new LogStore(logstoreName2, 1, 1);
        cleanUp(logstoreName2);
        logStore2.setMode("query");
        client.CreateLogStore(TEST_PROJECT, logStore2);
        getLogStoreResponse = client.GetLogStore(TEST_PROJECT, logstoreName2);
        Assert.assertEquals(getLogStoreResponse.GetLogStore().getMode(), "query");
        response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName2));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_FUNCTION);
        try {
            client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName2, Consts.CHARGE_BY_DATA_INGEST));
            Assert.fail();
        } catch (LogException ex) {
            Assert.assertEquals(ex.getMessage(), "The metring mode is not supported for current mode or telemetryType.");
        }
        String logstoreName3 = "log2";
        LogStore logStore3 = new LogStore(logstoreName2, 1, 1);
        cleanUp(logstoreName3);
        logStore3.setMode("lite");
        client.CreateLogStore(TEST_PROJECT, logStore3);
        getLogStoreResponse = client.GetLogStore(TEST_PROJECT, logstoreName3);
        Assert.assertEquals(getLogStoreResponse.GetLogStore().getMode(), "lite");
        response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName3));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_FUNCTION);
        try {
            client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName3, Consts.CHARGE_BY_DATA_INGEST));
            Assert.fail();
        } catch (LogException ex) {
            Assert.assertEquals(ex.getMessage(), "The metring mode is not supported for current mode or telemetryType.");
        }

        try {
            client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName, Consts.CHARGE_BY_DATA_INGEST));
            Assert.fail();
        } catch (LogException ex) {
//            Assert.assertEquals(ex.getMessage(), "This operation is forbidden, please contact SLS support.");
            Assert.assertEquals(ex.getMessage(), "The service code is not opened: slsingest");
        }

        String metricstore = "metric1";
        LogStore logStore1 = new LogStore(metricstore, 1, 1);
        logStore1.setTelemetryType("Metrics");
        cleanUp(metricstore);
        client.CreateLogStore(TEST_PROJECT, logStore1);
        response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, metricstore));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_FUNCTION);
        client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName, Consts.CHARGE_BY_FUNCTION));

        try {
            client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, metricstore, Consts.CHARGE_BY_DATA_INGEST));
            Assert.fail();
        } catch (LogException ex) {
            Assert.assertEquals(ex.getMessage(), "The metring mode is not supported for current mode or telemetryType.");
        }
    }

    @Test
    public void testCreateLogStoreOpenedIngest() throws Exception {
        String logstoreName = "log1";
        LogStore logStore = new LogStore(logstoreName, 1, 1);
        cleanUp(logstoreName);
        client.CreateLogStore(TEST_PROJECT, logStore);
        GetLogStoreResponse getLogStoreResponse = client.GetLogStore(TEST_PROJECT, logstoreName);
        Assert.assertEquals(getLogStoreResponse.GetLogStore().getMode(), "standard");
        GetLogStoreMeteringModeResponse response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_DATA_INGEST);

        // metering mode not changed is OK
        client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName, Consts.CHARGE_BY_DATA_INGEST));
        response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_DATA_INGEST);

        client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName, Consts.CHARGE_BY_FUNCTION));
        response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_FUNCTION);

        try {
            // Not in whitelist
            client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName, Consts.CHARGE_BY_DATA_INGEST));
            Assert.fail();
        } catch (LogException ex) {
            Assert.assertEquals(ex.getMessage(), "This operation is forbidden, please contact SLS support.");
//            Assert.assertEquals(ex.getMessage(), "The service code is not opened: slsingest");
        }

        String logstoreName2 = "log2";
        LogStore logStore2 = new LogStore(logstoreName2, 1, 1);
        cleanUp(logstoreName2);
        logStore2.setMode("query");
        client.CreateLogStore(TEST_PROJECT, logStore2);
        getLogStoreResponse = client.GetLogStore(TEST_PROJECT, logstoreName2);
        Assert.assertEquals(getLogStoreResponse.GetLogStore().getMode(), "query");
        response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName2));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_FUNCTION);
        try {
            client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName2, Consts.CHARGE_BY_DATA_INGEST));
            Assert.fail();
        } catch (LogException ex) {
            Assert.assertEquals(ex.getMessage(), "The metring mode is not supported for current mode or telemetryType.");
        }
        String logstoreName3 = "log2";
        LogStore logStore3 = new LogStore(logstoreName2, 1, 1);
        cleanUp(logstoreName3);
        logStore3.setMode("lite");
        client.CreateLogStore(TEST_PROJECT, logStore3);
        getLogStoreResponse = client.GetLogStore(TEST_PROJECT, logstoreName3);
        Assert.assertEquals(getLogStoreResponse.GetLogStore().getMode(), "lite");
        response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName3));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_FUNCTION);
        try {
            client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName3, Consts.CHARGE_BY_DATA_INGEST));
            Assert.fail();
        } catch (LogException ex) {
            Assert.assertEquals(ex.getMessage(), "The metring mode is not supported for current mode or telemetryType.");
        }

        String metricstore = "metric1";
        LogStore logStore1 = new LogStore(metricstore, 1, 1);
        logStore1.setTelemetryType("Metrics");
        cleanUp(metricstore);
        client.CreateLogStore(TEST_PROJECT, logStore1);
        response = client.getLogStoreMeteringMode(new GetLogStoreMeteringModeRequest(TEST_PROJECT, metricstore));
        Assert.assertEquals(response.getMeteringMode(), Consts.CHARGE_BY_FUNCTION);
        client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, logstoreName, Consts.CHARGE_BY_FUNCTION));

        try {
            client.updateLogStoreMeteringMode(new UpdateLogStoreMeteringModeRequest(TEST_PROJECT, metricstore, Consts.CHARGE_BY_DATA_INGEST));
            Assert.fail();
        } catch (LogException ex) {
            Assert.assertEquals(ex.getMessage(), "The metring mode is not supported for current mode or telemetryType.");
        }
    }

    private void cleanUp(String logstore) throws LogException {
        try {
            client.DeleteLogStore(TEST_PROJECT, logstore);
        } catch (LogException ex) {
            if (ex.GetHttpCode() != 404)
                throw ex;
        }
    }
}
