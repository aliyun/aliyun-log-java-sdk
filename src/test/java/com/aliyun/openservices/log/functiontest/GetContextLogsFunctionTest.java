package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.aliyun.openservices.log.response.GetContextLogsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
@Ignore
public class GetContextLogsFunctionTest extends LogTest {
    private final String PACK_ID_PREFIX = "ABCDEF" + getNowTimestamp() + "-";
    private final int PACKAGE_SIZE = 100;

    private int writeData(String project, String logstore) {
        int logGroupCount = randomBetween(5, 10);
        for (int i = 0; i < logGroupCount; ++i) {
            List<LogItem> logItems = new ArrayList<LogItem>(PACKAGE_SIZE);
            for (int j = 0; j < PACKAGE_SIZE; j++) {
                LogItem logItem = new LogItem(getNowTimestamp());
                logItem.PushBack("ID", "id_" + j);
                logItems.add(logItem);
            }
            List<TagContent> tags = new ArrayList<TagContent>(2);
            tags.add(new TagContent("__pack_id__", PACK_ID_PREFIX + i));
            tags.add(new TagContent("__extra_tag__", "extra_tag_value"));
            PutLogsRequest request = new PutLogsRequest(project, logstore, "", logItems);
            request.SetTags(tags);

            try {
                client.PutLogs(request);
            } catch (LogException e) {
                fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
            }
        }
        return logGroupCount;
    }

    private class PackInfo {
        public String packID;
        public String packMeta;

        public PackInfo(String id, String meta) {
            this.packID = id;
            this.packMeta = meta;
        }
    }

    private PackInfo extractPackInfo(QueriedLog log) {
        PackInfo ret = new PackInfo("", "");
        ArrayList<LogContent> contents = log.GetLogItem().GetLogContents();
        for (int i = 0; i < contents.size(); ++i) {
            LogContent content = contents.get(i);
            if (content.GetKey().equals("__tag__:__pack_id__")) {
                ret.packID = content.GetValue();
            } else if (content.GetKey().equals("__pack_meta__")) {
                ret.packMeta = content.GetValue();
            }
        }
        return ret;
    }

    @Test
    public void test() throws Exception {
        String logstore = "context-logstore-" + getNowTimestamp();
        ensureLogStoreEnabled(TEST_PROJECT, logstore, randomBoolean());
        {
            Index index = new Index();
            index.SetTtl(7);
            IndexLine line = new IndexLine();
            line.SetCaseSensitive(false);
            List<String> token = new ArrayList<String>();
            token.add(";");
            line.SetToken(token);
            List<String> includeKeys = new ArrayList<String>();
            includeKeys.add("ID");
            line.SetIncludeKeys(includeKeys);
            index.SetLine(line);
            client.CreateIndex(TEST_PROJECT, logstore, index);
            waitOneMinutes();
        }

        int logGroupCount = writeData(TEST_PROJECT, logstore);
        Thread.sleep(5 * 1000);

        // Get the pack ID and meta of the start log.
        int startIndex = logGroupCount / 2;
        String startPackID = PACK_ID_PREFIX + startIndex;
        String startPackMeta;
        {
            GetLogsResponse response = client.GetLogs(TEST_PROJECT, logstore,
                    getNowTimestamp() - 120, getNowTimestamp() + 60, "",
                    "__tag__:__pack_id__:" + startPackID + "|with_pack_meta");
            assertEquals(response.GetCount(), 100);
            PackInfo info = extractPackInfo(response.GetLogs().get(0));
            assertEquals(info.packID, startPackID);
            startPackMeta = info.packMeta;
        }

        // getContextLogs: both directions.
        GetContextLogsResponse response = client.getContextLogs(TEST_PROJECT, logstore,
                startPackID, startPackMeta, 100, 100);
        assertTrue(response.isCompleted());
        assertEquals(response.getTotalLines(), 201);
        assertEquals(response.getBackLines(), 100);
        assertEquals(response.getForwardLines(), 100);
        assertEquals(response.getLogs().size(), 201);
        List<QueriedLog> logs = response.getLogs();

        // Use the first log to fetch backward continuously.
        {
            int totalCount = (startIndex - 1) * PACKAGE_SIZE;
            int STEP_SIZE = 30;

            QueriedLog log = logs.get(0);
            while (true) {
                PackInfo info = extractPackInfo(log);
                response = client.getContextLogs(TEST_PROJECT, logstore,
                        info.packID, info.packMeta, STEP_SIZE, 0);
                int expectedCount = Math.min(STEP_SIZE, totalCount);
                assertTrue(response.isCompleted());
                assertEquals(response.getTotalLines(), expectedCount + 1);
                assertEquals(response.getBackLines(), expectedCount);
                assertEquals(response.getForwardLines(), 0);

                totalCount -= expectedCount;
                log = response.getLogs().get(0);
                if (0 == totalCount) {
                    break;
                }
            }

            // No log is returned.
            PackInfo info = extractPackInfo(log);
            response = client.getContextLogs(TEST_PROJECT, logstore,
                    info.packID, info.packMeta, STEP_SIZE, 0);
            assertTrue(response.isCompleted());
            assertEquals(response.getTotalLines(), 0);
        }

        // Use the last log to fetch forward continuously.
        {
            int totalCount = (logGroupCount - startIndex - 1) * PACKAGE_SIZE - 1;
            int STEP_SIZE = 30;

            QueriedLog log = logs.get(logs.size() - 1);
            while (true) {
                PackInfo info = extractPackInfo(log);
                response = client.getContextLogs(TEST_PROJECT, logstore,
                        info.packID, info.packMeta, 0, STEP_SIZE);
                int expectedCount = Math.min(STEP_SIZE, totalCount);
                assertTrue(response.isCompleted());
                assertEquals(response.getTotalLines(), expectedCount + 1);
                assertEquals(response.getBackLines(), 0);
                assertEquals(response.getForwardLines(), expectedCount);

                totalCount -= expectedCount;
                log = response.getLogs().get(response.getLogs().size() - 1);
                if (0 == totalCount) {
                    break;
                }
            }

            // No log is returned.
            PackInfo info = extractPackInfo(log);
            response = client.getContextLogs(TEST_PROJECT, logstore,
                    info.packID, info.packMeta, 0, STEP_SIZE);
            assertTrue(response.isCompleted());
            assertEquals(response.getTotalLines(), 0);
        }
    }
}
