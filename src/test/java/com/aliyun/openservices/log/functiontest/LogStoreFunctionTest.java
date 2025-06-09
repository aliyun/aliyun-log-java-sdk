package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.EncryptConf;
import com.aliyun.openservices.log.common.EncryptUserCmkConf;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.ListLogStoresRequest;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LogStoreFunctionTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testCreateLogStore() throws Exception {
        Thread.sleep(1000 * 60);
        LogStore logStore1 = new LogStore();
        logStore1.SetLogStoreName("");
        logStore1.SetShardCount(2);
        logStore1.SetTtl(7);
        boolean appendMeta = randomBoolean();
        logStore1.setAppendMeta(appendMeta);
        boolean webTracking = randomBoolean();
        logStore1.setEnableWebTracking(webTracking);
        try {
            client.CreateLogStore(TEST_PROJECT, logStore1);
            fail("Create invalid logstore should fail");
        } catch (LogException ex) {
            assertEquals(ex.getErrorCode(), "LogStoreInfoInvalid");
            assertEquals(ex.getMessage(), "logstore name  is invalid");
            assertEquals(ex.getHttpCode(), 400);
        }

        LogStore logStore = new LogStore();
        logStore.SetLogStoreName("logstore-for-testing1");
        logStore.SetShardCount(2);
        logStore.SetTtl(7);
        logStore.setAppendMeta(appendMeta);
        logStore.setEnableWebTracking(webTracking);
        logStore.setmAutoSplit(randomBoolean());

        if (logStore.ismAutoSplit()) {
            logStore.setmMaxSplitShard(1000);
            try {
                client.CreateLogStore(TEST_PROJECT, logStore);
                fail("Create invalid logstore should fail");
            } catch (LogException ex) {
                assertEquals(ex.getErrorCode(), "LogStoreInfoInvalid");
                assertEquals(ex.getMessage(), "maxSplitShard must be within range [1, 256]");
                assertEquals(ex.getHttpCode(), 400);
            }
            logStore.setmMaxSplitShard(randomBetween(1, 64));
        }

        EncryptConf encryptConf = new EncryptConf(true);
        encryptConf.setEncryptType("typex");
        EncryptUserCmkConf cmkConf = new EncryptUserCmkConf();
        cmkConf.setArn("roleArn");
        cmkConf.setCmkKeyId("cmdk");
        cmkConf.setRegionId("cn-hangzhou");
        logStore.SetEncryptConf(encryptConf);

        try {
            client.CreateLogStore(TEST_PROJECT, logStore);
            fail("Create invalid encrypt type should fail");
        } catch (LogException ex) {
            assertEquals(ex.GetErrorCode(), "LogStoreInfoInvalid");
            assertEquals(ex.GetErrorMessage(), "The encrypt_conf is invalid, [encrypt_type] key is missing or not supported");
            assertEquals(ex.GetHttpCode(), 400);
        }
//        encryptConf.setEncryptType("aes_ofb");

        // TODO test encrypt conf
        logStore.SetEncryptConf(null);

        if (randomBoolean()) {
            logStore.setTelemetryType("metrics");
        }

        client.CreateLogStore(TEST_PROJECT, logStore);

        GetLogStoreResponse response = client.GetLogStore(TEST_PROJECT, "logstore-for-testing1");
        LogStore logStore2 = response.GetLogStore();
        assertEquals(appendMeta, logStore2.isAppendMeta());
        assertEquals(7, logStore2.GetTtl());
        assertEquals(2, logStore2.GetShardCount());
        assertEquals("logstore-for-testing1", logStore2.GetLogStoreName());
        assertEquals(appendMeta, logStore2.isAppendMeta());
        assertEquals(webTracking, logStore2.isEnableWebTracking());
        assertEquals(logStore.getTelemetryType(), logStore2.getTelemetryType());
        assertEquals(logStore.getUsedStorage(), 0);
        assertEquals(logStore.getProductType(), logStore2.getProductType());
        assertEquals(logStore.getEncryptConf(), logStore2.getEncryptConf());
        assertEquals("standard", logStore2.getMode());
        int now = getNowTimestamp();
        assertTrue(Math.abs(now - logStore2.GetCreateTime()) < 60);
        assertTrue(Math.abs(now - logStore2.GetLastModifyTime()) < 60);

        try {
            client.CreateLogStore(TEST_PROJECT, logStore);
            fail("Create duplicate logstore should fail");
        } catch (LogException ex) {
            assertEquals(ex.getErrorCode(), "LogStoreAlreadyExist");
            assertEquals(ex.getMessage(), "logstore logstore-for-testing1 already exists");
            assertEquals(ex.getHttpCode(), 400);
        }

        logStore = new LogStore();
        logStore.SetLogStoreName("logstore-for-testing2");
        logStore.SetShardCount(3);
        logStore.SetTtl(30);
        logStore.setAppendMeta(false);
        logStore.setEnableWebTracking(false);
        logStore.setMode("standard");
        client.CreateLogStore(TEST_PROJECT, logStore);

        response = client.GetLogStore(TEST_PROJECT, "logstore-for-testing2");
        LogStore logStore3 = response.GetLogStore();
        assertFalse(logStore3.isAppendMeta());
        assertEquals(30, logStore3.GetTtl());
        assertEquals(3, logStore3.GetShardCount());
        assertEquals("logstore-for-testing2", logStore3.GetLogStoreName());
        assertFalse(logStore3.isEnableWebTracking());
        assertEquals(logStore.getTelemetryType(), logStore3.getTelemetryType());
        assertEquals(logStore.getUsedStorage(), 0);
        assertEquals(logStore.getProductType(), logStore3.getProductType());
        assertEquals(logStore.getEncryptConf(), logStore3.getEncryptConf());
        assertEquals(logStore.getMode(), logStore3.getMode());
        assertEquals("standard", logStore3.getMode());
        assertTrue(Math.abs(now - logStore3.GetCreateTime()) < 60);
        assertTrue(Math.abs(now - logStore3.GetLastModifyTime()) < 60);


        logStore = new LogStore();
        logStore.SetLogStoreName("logstore-for-testing3");
        logStore.SetShardCount(3);
        logStore.SetTtl(30);
        logStore.setAppendMeta(false);
        logStore.setEnableWebTracking(false);
        logStore.setMode("lite");
        client.CreateLogStore(TEST_PROJECT, logStore);

        response = client.GetLogStore(TEST_PROJECT, "logstore-for-testing3");
        logStore3 = response.GetLogStore();
        assertFalse(logStore3.isAppendMeta());
        assertEquals(30, logStore3.GetTtl());
        assertEquals(3, logStore3.GetShardCount());
        assertEquals("logstore-for-testing3", logStore3.GetLogStoreName());
        assertFalse(logStore3.isEnableWebTracking());
        assertEquals(logStore.getTelemetryType(), logStore3.getTelemetryType());
        assertEquals(logStore.getUsedStorage(), 0);
        assertEquals(logStore.getProductType(), logStore3.getProductType());
        assertEquals(logStore.getEncryptConf(), logStore3.getEncryptConf());
        assertEquals("lite", logStore3.getMode());
        assertEquals(logStore.getMode(), logStore3.getMode());
        assertTrue(Math.abs(now - logStore3.GetCreateTime()) < 60);
        assertTrue(Math.abs(now - logStore3.GetLastModifyTime()) < 60);

        logStore = new LogStore();
        logStore.SetLogStoreName("logstore-for-testing4");
        logStore.SetShardCount(3);
        logStore.SetTtl(30);
        logStore.setAppendMeta(false);
        logStore.setEnableWebTracking(false);
        logStore.setMode("query");
        client.CreateLogStore(TEST_PROJECT, logStore);
        response = client.GetLogStore(TEST_PROJECT, "logstore-for-testing4");
        logStore3 = response.GetLogStore();
        assertEquals("logstore-for-testing4", logStore3.GetLogStoreName());
        assertEquals("query", logStore3.getMode());

        ListLogStoresRequest req = new ListLogStoresRequest(TEST_PROJECT, 0,10,"");
        ListLogStoresResponse res = client.ListLogStores(req);
        assertEquals(res.GetLogStores().size(), 4);

        req = new ListLogStoresRequest(TEST_PROJECT, 0,10);
        req.SetMode("query");
        res = client.ListLogStores(req);
        assertEquals(res.GetLogStores().size(), 1);

        req = new ListLogStoresRequest(TEST_PROJECT, 0,10);
        req.SetMode("lite");
        res = client.ListLogStores(req);
        assertEquals(res.GetLogStores().size(), 1);

        req = new ListLogStoresRequest(TEST_PROJECT, 0,10);
        req.SetMode("standard");
        res = client.ListLogStores(req);
        assertEquals(res.GetLogStores().size(), 2);

        req = new ListLogStoresRequest(TEST_PROJECT, 0,10);
        req.SetMode("xxxxx");
        res = client.ListLogStores(req);
        assertEquals(res.GetLogStores().size(), 0);

        client.DeleteLogStore(TEST_PROJECT, "logstore-for-testing1");
        client.DeleteLogStore(TEST_PROJECT, "logstore-for-testing2");
        client.DeleteLogStore(TEST_PROJECT, "logstore-for-testing3");
        client.DeleteLogStore(TEST_PROJECT, "logstore-for-testing4");
    }

    @Test
    public void testUpdateLogStore() throws Exception {
        String logstoreName = "logstore-for-testing1";

        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(logstoreName);
        logStore.SetShardCount(2);
        logStore.SetTtl(7);
        logStore.setAppendMeta(false);
        client.CreateLogStore(TEST_PROJECT, logStore);

        GetLogStoreResponse response = client.GetLogStore(TEST_PROJECT, logstoreName);
        LogStore logStore1 = response.GetLogStore();
        assertEquals(logStore1.isAppendMeta(), logStore.isAppendMeta());
        assertEquals(7, logStore1.GetTtl());
        assertEquals(2, logStore1.GetShardCount());
        assertEquals(logstoreName, logStore1.GetLogStoreName());

        logStore = new LogStore();
        logStore.SetLogStoreName(logstoreName);
        logStore.SetShardCount(3);
        logStore.SetTtl(30);
        logStore.setAppendMeta(true);
        client.UpdateLogStore(TEST_PROJECT, logStore);

        response = client.GetLogStore(TEST_PROJECT, logstoreName);
        LogStore logStore2 = response.GetLogStore();
        assertEquals(logStore.isAppendMeta(), logStore2.isAppendMeta());
        assertEquals(30, logStore2.GetTtl());
        // shard count cannot be changed via update logstore API
        assertEquals(2, logStore2.GetShardCount());
        assertEquals(logstoreName, logStore2.GetLogStoreName());
        assertEquals("standard", logStore2.getMode());

        logStore = new LogStore();
        logStore.SetLogStoreName(logstoreName);
        logStore.SetShardCount(3);
        logStore.SetTtl(30);
        logStore.setMode("standard");
        logStore.setAppendMeta(true);
        client.UpdateLogStore(TEST_PROJECT, logStore);

        logStore = new LogStore();
        logStore.SetLogStoreName(logstoreName);
        logStore.SetShardCount(3);
        logStore.SetTtl(30);
        logStore.setMode("lite");
        logStore.setAppendMeta(true);
        try {
            client.UpdateLogStore(TEST_PROJECT, logStore);
            fail("update logstore mode should fail");
        } catch (LogException ex) {
            assertEquals(ex.getErrorCode(), "NotSupported");
            assertEquals(ex.getMessage(), "The logstore mode or telemetryType is not supported for current metering mode.");
            assertEquals(ex.getHttpCode(), 400);
        }

        logstoreName = "logstore-for-testing2";
        logStore = new LogStore();
        logStore.SetLogStoreName(logstoreName);
        logStore.SetShardCount(2);
        logStore.SetTtl(7);
        logStore.setAppendMeta(false);
        logStore.setMode("lite");
        client.CreateLogStore(TEST_PROJECT, logStore);

        logStore = new LogStore();
        logStore.SetLogStoreName(logstoreName);
        logStore.SetShardCount(3);
        logStore.SetTtl(30);
        logStore.setAppendMeta(true);
        logStore.setMode("lite");
        client.UpdateLogStore(TEST_PROJECT, logStore);

        response = client.GetLogStore(TEST_PROJECT, logstoreName);
        logStore2 = response.GetLogStore();
        assertEquals("lite", logStore.getMode());

        logStore = new LogStore();
        logStore.SetLogStoreName(logstoreName);
        logStore.SetShardCount(3);
        logStore.SetTtl(30);
        logStore.setAppendMeta(true);
        logStore.setMode("standard");
        client.UpdateLogStore(TEST_PROJECT, logStore);
        client.DeleteLogStore(TEST_PROJECT, "logstore-for-testing1");
        client.DeleteLogStore(TEST_PROJECT, "logstore-for-testing2");
    }
}
