package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.MetaAPIBaseFunctionTest;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class StorageTtl extends MetaAPIBaseFunctionTest {
    @Test
    public void testTtl1() throws Exception {
        LogStore logStore1 = new LogStore();
        logStore1.SetLogStoreName("test-ttl-1");
        logStore1.SetShardCount(2);
        logStore1.SetTtl(100);
        logStore1.setHotTTL(10);
        logStore1.setInfrequentAccessTTL(30);

        try {
            client.CreateLogStore(TEST_PROJECT, logStore1);
            GetLogStoreResponse res = client.GetLogStore(TEST_PROJECT, "test-ttl-1");
            LogStore l = res.GetLogStore();
            assertEquals(10, l.getHotTTL());
            assertEquals(30, l.getInfrequentAccessTTL());

            logStore1.SetTtl(103);
            logStore1.setHotTTL(11);
            logStore1.setInfrequentAccessTTL(31);
            client.UpdateLogStore(TEST_PROJECT, logStore1);
            res = client.GetLogStore(TEST_PROJECT, "test-ttl-1");
            l = res.GetLogStore();
            assertEquals(11, l.getHotTTL());
            assertEquals(31, l.getInfrequentAccessTTL());
        } catch (LogException ex) {
            fail("Create logstore fail with ttls");
        }
    }

    @Test
    public void testTtl2() throws Exception {
        LogStore logStore1 = new LogStore();
        logStore1.SetLogStoreName("test-ttl-1");
        logStore1.SetShardCount(2);
        logStore1.SetTtl(90);
        logStore1.setHotTTL(10);
        logStore1.setInfrequentAccessTTL(20);

        try {
            client.CreateLogStore(TEST_PROJECT, logStore1);
            fail("error");

        } catch (LogException ex) {
            assertEquals(ex.getErrorCode(), "LogStoreInfoInvalid");
            assertEquals(ex.getMessage(), "infrequentAccessTTL must large than 30");
            assertEquals(ex.getHttpCode(), 400);
        }
    }

    @Test
    public void testTtl2u() throws Exception {
        LogStore logStore1 = new LogStore();
        logStore1.SetLogStoreName("test-ttl-1");
        logStore1.SetShardCount(2);
        logStore1.SetTtl(100);
        logStore1.setHotTTL(10);
        logStore1.setInfrequentAccessTTL(30);

        try {
            client.CreateLogStore(TEST_PROJECT, logStore1);
            GetLogStoreResponse res = client.GetLogStore(TEST_PROJECT, "test-ttl-1");
            LogStore l = res.GetLogStore();
            assertEquals(10, l.getHotTTL());
            assertEquals(30, l.getInfrequentAccessTTL());

            logStore1.SetTtl(90);
            logStore1.setHotTTL(10);
            logStore1.setInfrequentAccessTTL(20);
            client.UpdateLogStore(TEST_PROJECT, logStore1);
            fail("error");

        } catch (LogException ex) {
            assertEquals(ex.getErrorCode(), "LogStoreInfoInvalid");
            assertEquals(ex.getMessage(), "infrequentAccessTTL must large than 30");
            assertEquals(ex.getHttpCode(), 400);
        }
    }

    @Test
    public void testTtl4() throws Exception {
        LogStore logStore1 = new LogStore();
        logStore1.SetLogStoreName("test-ttl-1");
        logStore1.SetShardCount(2);
        logStore1.SetTtl(80);
        logStore1.setHotTTL(10);
        logStore1.setInfrequentAccessTTL(30);
        try {
            client.CreateLogStore(TEST_PROJECT, logStore1);
            fail("error");

        } catch (LogException ex) {
            assertEquals(ex.getErrorCode(), "LogStoreInfoInvalid");
            assertEquals(ex.getMessage(), "archiveTTL must large than 60");
            assertEquals(ex.getHttpCode(), 400);
        }
    }

    @Test
    public void testTtl4u() throws Exception {
        LogStore logStore1 = new LogStore();
        logStore1.SetLogStoreName("test-ttl-1");
        logStore1.SetShardCount(2);
        logStore1.SetTtl(100);
        logStore1.setHotTTL(10);
        logStore1.setInfrequentAccessTTL(30);

        try {
            client.CreateLogStore(TEST_PROJECT, logStore1);
            GetLogStoreResponse res = client.GetLogStore(TEST_PROJECT, "test-ttl-1");
            LogStore l = res.GetLogStore();
            assertEquals(10, l.getHotTTL());
            assertEquals(30, l.getInfrequentAccessTTL());
            logStore1.SetTtl(80);
            logStore1.setHotTTL(10);
            logStore1.setInfrequentAccessTTL(30);
            client.UpdateLogStore(TEST_PROJECT, logStore1);
            fail("error");

        } catch (LogException ex) {
            assertEquals(ex.getErrorCode(), "LogStoreInfoInvalid");
            assertEquals(ex.getMessage(), "archiveTTL must large than 60");
            assertEquals(ex.getHttpCode(), 400);
        }
    }

    @Test
    public void testTtl6() throws Exception {
        LogStore logStore1 = new LogStore();
        logStore1.SetLogStoreName("test-ttl-1");
        logStore1.SetShardCount(2);
        logStore1.SetTtl(80);
        logStore1.setHotTTL(10);
        try {
            client.CreateLogStore(TEST_PROJECT, logStore1);
            GetLogStoreResponse res = client.GetLogStore(TEST_PROJECT, "test-ttl-1");
            LogStore l = res.GetLogStore();
            assertEquals(10, l.getHotTTL());
            assertEquals(-1, l.getInfrequentAccessTTL());

        } catch (LogException ex) {
            fail("error");
        }
    }

    @Test
    public void testTtl6u() throws Exception {
        LogStore logStore1 = new LogStore();
        logStore1.SetLogStoreName("test-ttl-1");
        logStore1.SetShardCount(2);
        logStore1.SetTtl(100);
        logStore1.setHotTTL(10);
        logStore1.setInfrequentAccessTTL(30);

        try {
            client.CreateLogStore(TEST_PROJECT, logStore1);
            GetLogStoreResponse res = client.GetLogStore(TEST_PROJECT, "test-ttl-1");
            LogStore l = res.GetLogStore();
            assertEquals(10, l.getHotTTL());
            assertEquals(30, l.getInfrequentAccessTTL());
            logStore1.SetTtl(109);
            logStore1.setHotTTL(9);
            logStore1.setInfrequentAccessTTL(-1);
            client.UpdateLogStore(TEST_PROJECT, logStore1);
            res = client.GetLogStore(TEST_PROJECT, "test-ttl-1");
            l = res.GetLogStore();
            assertEquals(9, l.getHotTTL());
            assertEquals(30, l.getInfrequentAccessTTL());
        } catch (LogException ex) {
            fail("error");

        }
    }

    @Test
    public void testTtl7u() throws Exception {
        LogStore logStore1 = new LogStore();
        logStore1.SetLogStoreName("test-ttl-1");
        logStore1.SetShardCount(2);
        logStore1.SetTtl(100);
        logStore1.setHotTTL(10);
        logStore1.setInfrequentAccessTTL(30);

        try {
            client.CreateLogStore(TEST_PROJECT, logStore1);
            GetLogStoreResponse res = client.GetLogStore(TEST_PROJECT, "test-ttl-1");
            LogStore l = res.GetLogStore();
            assertEquals(10, l.getHotTTL());
            assertEquals(30, l.getInfrequentAccessTTL());
            logStore1.SetTtl(80);
            logStore1.setHotTTL(9);
            logStore1.setInfrequentAccessTTL(-1);
            client.UpdateLogStore(TEST_PROJECT, logStore1);
        } catch (LogException ex) {
            assertEquals(ex.getErrorCode(), "ParameterInvalid");
            assertEquals(ex.getMessage(), "archiveTtl must larger than 60");
            assertEquals(ex.getHttpCode(), 400);
        }
    }
}
