package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.BatchGetLogRequest;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class PullLogsFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = "project-to-append-metadata";


    private void ensureLogStoreEnabled(String logStoreName, boolean enabled) {
        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(logStoreName);
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.setAppendMeta(enabled);
        reCreateLogStore(TEST_PROJECT, logStore);
    }

    private int writeData(String logStore) {
        int round = randomBetween(10, 20);
        for (int i = 0; i < round; i++) {
            List<LogItem> logGroup = new ArrayList<LogItem>(600);
            for (int j = 0; j < 600; j++) {
                LogItem logItem = new LogItem(getNowTimestamp());
                logItem.PushBack("ID", "id_" + String.valueOf(i * 600 + j));
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

    private int countAppended(String logStore, int shard) throws LogException {
        GetCursorResponse cursorResponse = client.GetCursor(TEST_PROJECT, logStore, shard, Consts.CursorMode.BEGIN);
        String cursor = cursorResponse.GetCursor();
        int n = 0;

        while (true) {
            BatchGetLogRequest request = new BatchGetLogRequest(TEST_PROJECT, logStore, shard, 1000, cursor);
            BatchGetLogResponse response1 = client.BatchGetLog(request);
            for (LogGroupData data : response1.GetLogGroups()) {
                Logs.LogGroup group = data.GetLogGroup();
                ++n;
                for (Logs.Log log : group.getLogsList()) {
                    assertEquals(log.getContentsCount(), 1);
                    assertEquals("ID", log.getContents(0).getKey());
                    assertTrue(log.getContents(0).getValue().startsWith("id_"));
                }
            }
            if (response1.GetNextCursor() == null || cursor.equals(response1.GetNextCursor())) {
                break;
            }
            cursor = response1.GetNextCursor();
        }
        return n;
    }

    @Test
    public void testNewData() throws Exception {
        String logStore = "logstore-append-meta";
        ensureLogStoreEnabled(logStore, true);
        int written = writeData(logStore);
        assertEquals(written, countAppended(logStore, 0) + countAppended(logStore, 1));
    }


    @Test
    public void testMixData() throws Exception {
        String logStore = "logstore-append-meta";
        ensureLogStoreEnabled(logStore, false);
        writeData(logStore);
        assertEquals(0, countAppended(logStore, 0) + countAppended(logStore, 1));

        GetLogStoreResponse response = client.GetLogStore(TEST_PROJECT, logStore);
        LogStore logStore1 = response.GetLogStore();
        logStore1.setAppendMeta(true);
        client.UpdateLogStore(TEST_PROJECT, logStore1);
        waitOneMinutes();

        int written = writeData(logStore);
        assertEquals(written, countAppended(logStore, 0) + countAppended(logStore, 1));

        logStore1 = response.GetLogStore();
        logStore1.setAppendMeta(false);
        client.UpdateLogStore(TEST_PROJECT, logStore1);
        waitOneMinutes();

        writeData(logStore);
        assertEquals(written, countAppended(logStore, 0) + countAppended(logStore, 1));
    }

}
