package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.EncryptConf;
import com.aliyun.openservices.log.common.EncryptUserCmkConf;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LogStoreFunctionTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testCreateLogStore() throws Exception {
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
            assertEquals(ex.GetErrorCode(), "LogStoreInfoInvalid");
            assertEquals(ex.GetErrorMessage(), "logstore name  is invalid");
            assertEquals(ex.GetHttpCode(), 400);
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
                assertEquals(ex.GetErrorCode(), "LogStoreInfoInvalid");
                assertEquals(ex.GetErrorMessage(), "maxSplitShard must be within range [1, 64]");
                assertEquals(ex.GetHttpCode(), 400);
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
            assertEquals(ex.GetErrorMessage(), "The encrypt_conf is invalid, [encrypt_type] key is missing or not supportedtypex");
            assertEquals(ex.GetHttpCode(), 400);
        }
//        encryptConf.setEncryptType("aes_ofb");

        // TODO test encrypt conf
        logStore.SetEncryptConf(null);
        logStore.setArchiveSeconds(randomInt(100));
        try {
            client.CreateLogStore(TEST_PROJECT, logStore);
            fail("Create invalid archive seconds should fail");
        } catch (LogException ex) {
            assertEquals(ex.GetErrorCode(), "ParameterInvalid");
            assertEquals(ex.GetErrorMessage(), "invalid archive seconds " + logStore.getArchiveSeconds());
            assertEquals(ex.GetHttpCode(), 400);
        }

        logStore.setArchiveSeconds(randomBetween(86400, logStore.GetTtl() * 86400));

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
        assertEquals(logStore.getArchiveSeconds(), logStore2.getArchiveSeconds());
        assertEquals(logStore.getUsedStorage(), 0);
        assertEquals(logStore.getProductType(), logStore2.getProductType());
        assertEquals(logStore.getEncryptConf(), logStore2.getEncryptConf());
        int now = getNowTimestamp();
        assertTrue(Math.abs(now - logStore2.GetCreateTime()) < 60);
        assertTrue(Math.abs(now - logStore2.GetLastModifyTime()) < 60);

        try {
            client.CreateLogStore(TEST_PROJECT, logStore);
            fail("Create duplicate logstore should fail");
        } catch (LogException ex) {
            assertEquals(ex.GetErrorCode(), "LogStoreAlreadyExist");
            assertEquals(ex.GetErrorMessage(), "logstore logstore-for-testing1 already exists");
            assertEquals(ex.GetHttpCode(), 400);
        }

        logStore = new LogStore();
        logStore.SetLogStoreName("logstore-for-testing2");
        logStore.SetShardCount(3);
        logStore.SetTtl(30);
        logStore.setAppendMeta(false);
        logStore.setEnableWebTracking(false);
        client.CreateLogStore(TEST_PROJECT, logStore);

        response = client.GetLogStore(TEST_PROJECT, "logstore-for-testing2");
        LogStore logStore3 = response.GetLogStore();
        assertFalse(logStore3.isAppendMeta());
        assertEquals(30, logStore3.GetTtl());
        assertEquals(3, logStore3.GetShardCount());
        assertEquals("logstore-for-testing2", logStore3.GetLogStoreName());
        assertFalse(logStore3.isEnableWebTracking());
        assertEquals(logStore.getTelemetryType(), logStore3.getTelemetryType());
        assertEquals(logStore.getArchiveSeconds(), logStore3.getArchiveSeconds());
        assertEquals(logStore.getUsedStorage(), 0);
        assertEquals(logStore.getProductType(), logStore3.getProductType());
        assertEquals(logStore.getEncryptConf(), logStore3.getEncryptConf());
        assertTrue(Math.abs(now - logStore3.GetCreateTime()) < 60);
        assertTrue(Math.abs(now - logStore3.GetLastModifyTime()) < 60);

        client.DeleteLogStore(TEST_PROJECT, "logstore-for-testing1");
        client.DeleteLogStore(TEST_PROJECT, "logstore-for-testing2");
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

        client.DeleteLogStore(TEST_PROJECT, logstoreName);
    }
}
