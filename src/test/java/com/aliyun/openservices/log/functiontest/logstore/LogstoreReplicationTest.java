package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.MetaAPIBaseFunctionTest;
import com.aliyun.openservices.log.response.GetLogstoreReplicationResponse;
import com.aliyun.openservices.log.response.SetLogstoreReplicationResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LogstoreReplicationTest extends MetaAPIBaseFunctionTest {
    private static String logstoreName = "data-replica-test";

    @Test
    public void testReplicatiionTest() throws LogException {
        try {
            client.DeleteLogStore(TEST_PROJECT, logstoreName);
        } catch (LogException ex) {
            ;
        }

        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(logstoreName);
        logStore.SetShardCount(2);
        logStore.SetTtl(7);
        client.CreateLogStore(TEST_PROJECT, logStore);

        GetLogstoreReplicationResponse res1 = client.getLogstoreReplication(TEST_PROJECT, logstoreName);
        assertEquals(false, res1.getEnable());

        SetLogstoreReplicationResponse res2 = client.setLogstoreReplication(TEST_PROJECT, logstoreName, true);

        GetLogstoreReplicationResponse res3 = client.getLogstoreReplication(TEST_PROJECT, logstoreName);
        assertEquals(true, res3.getEnable());

        SetLogstoreReplicationResponse res4 = client.setLogstoreReplication(TEST_PROJECT, logstoreName, false);

        GetLogstoreReplicationResponse res5 = client.getLogstoreReplication(TEST_PROJECT, logstoreName);
        assertEquals(false, res5.getEnable());
    }
}
