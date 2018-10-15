package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class PullLogsFunctionTest extends BaseMetadataTest {

    private int writeData(String logStore) {
        int round = randomBetween(1, 10);
        for (int i = 0; i < round; i++) {
            List<LogItem> logGroup = new ArrayList<LogItem>(10);
            for (int j = 0; j < 10; j++) {
                LogItem logItem = new LogItem(getNowTimestamp());
                logItem.PushBack("ID", "id_" + String.valueOf(i * 10 + j));
                logGroup.add(logItem);
            }
            String topic = "topic_" + String.valueOf(i);
            try {
                client.PutLogs(TEST_PROJECT, logStore, topic, logGroup, "");
            } catch (LogException e) {
                fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
            }
        }
        return round;
    }

    @Test
    public void testPullLogs() throws Exception {
        String logStore = "logstore-" + getNowTimestamp();
        ensureLogStoreEnabled(TEST_PROJECT, logStore, randomBoolean());
        int written = writeData(logStore);
        assertEquals(written, countAppended(TEST_PROJECT, logStore, 2, new Predicate() {
            @Override
            public boolean test(Logs.LogGroup logGroup) {
                return true;
            }
        }));
    }

    @Test
    public void testNewDataWillAppendClientIp() throws Exception {
        String logStore = "logstore-" + getNowTimestamp();
        ensureLogStoreEnabled(TEST_PROJECT, logStore, true);
        int written = writeData(logStore);
        assertEquals(written, countLogGroupWithClientIpTag(TEST_PROJECT, logStore, 2));
    }

    @Test
    public void testMixDataWillAppendClientIp() throws Exception {
        String logStore = "logstore-" + getNowTimestamp();
        ensureLogStoreEnabled(TEST_PROJECT, logStore, false);
        writeData(logStore);
        assertEquals(0, countLogGroupWithClientIpTag(TEST_PROJECT, logStore, 2));

        GetLogStoreResponse response = client.GetLogStore(TEST_PROJECT, logStore);
        LogStore logStore1 = response.GetLogStore();
        logStore1.setAppendMeta(true);
        client.UpdateLogStore(TEST_PROJECT, logStore1);
        waitOneMinutes();

        int written = writeData(logStore);
        assertEquals(written, countLogGroupWithClientIpTag(TEST_PROJECT, logStore, 2));

        logStore1 = response.GetLogStore();
        logStore1.setAppendMeta(false);
        client.UpdateLogStore(TEST_PROJECT, logStore1);
        waitOneMinutes();

        writeData(logStore);
        assertEquals(written, countLogGroupWithClientIpTag(TEST_PROJECT, logStore, 2));
    }
}
