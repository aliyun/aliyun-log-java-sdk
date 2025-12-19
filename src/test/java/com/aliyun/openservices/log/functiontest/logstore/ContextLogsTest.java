package com.aliyun.openservices.log.functiontest.logstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetContextLogsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;

public class ContextLogsTest extends BaseDataTest {

    // putLogs->getContextLogs
    @Test
    public void testGetContextLogs() throws LogException {
        enableIndex();
        getContextLogs();
    }

    public void getContextLogs() throws LogException {
        int count = prepareLogs(5, 6);
        String startPackID = PACK_ID_PREFIX + (count / 2);
        String startPackMeta;
        GetLogsResponse logs = client.GetLogs(project, logStore.GetLogStoreName(),
                timestamp - 1800, timestamp + 1800, "", "__tag__:__pack_id__:" + startPackID + "|with_pack_meta");

        PackInfo info = extractPackInfo(logs.getLogs().get(0));
        startPackMeta = info.packMeta;
        GetContextLogsResponse contextLogs = client.getContextLogs(project, logStore.GetLogStoreName(),
                startPackID, startPackMeta, 10, 10);
        for (QueriedLog log : contextLogs.getLogs()) {
            // assertEquals("test-source", log.mSource); 暂时拿不到source
            for (LogContent mContent : log.mLogItem.mContents) {
                String mKey = mContent.mKey;
                String mValue = mContent.mValue;
                if (mKey.startsWith("key-")) {
                    if (!mValue.startsWith("value-") || !mKey.substring(4).equals(mValue.substring(6))) {
                        throw new RuntimeException("Inconsistent data");
                    }
                } else if ("__tag__:__extra_tag__".equals(mKey)) {
                    assertEquals("extra_tag_value", mValue);
                } else if ("__tag__:__pack_id__".equals(mKey)) {
                    assertTrue(mValue.startsWith(PACK_ID_PREFIX));
                }
            }
        }
        waitForSeconds(10);
        assertTrue(contextLogs.isCompleted());
        assertEquals(contextLogs.getTotalLines(), 21);
        assertEquals(contextLogs.getBackLines(), 10);
        assertEquals(contextLogs.getForwardLines(), 10);
        assertEquals(contextLogs.getLogs().size(), 21);
    }

    private PackInfo extractPackInfo(QueriedLog log) {
        PackInfo info = new PackInfo("", "");
        ArrayList<LogContent> contents = log.GetLogItem().GetLogContents();
        for (LogContent content : contents) {
            if ("__tag__:__pack_id__".equals(content.GetKey())) {
                info.packID = content.GetValue();
            } else if ("__pack_meta__".equals(content.GetKey())) {
                info.packMeta = content.GetValue();
            }
        }
        return info;
    }

    private class PackInfo {
        public String packID;
        public String packMeta;

        public PackInfo(String id, String meta) {
            this.packID = id;
            this.packMeta = meta;
        }
    }
}
