package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.common.FastLog;
import com.aliyun.openservices.log.common.FastLogContent;
import com.aliyun.openservices.log.common.FastLogGroup;
import com.aliyun.openservices.log.common.FastLogTag;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.common.TagContent;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.others.DataVerify;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aliyun.openservices.log.common.Consts.CONST_GZIP_ENCODING;
import static com.aliyun.openservices.log.common.Consts.CONST_LZ4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BatchGetLogFastPbTest extends DataVerify {
    static private final String project = TEST_PROJECT;
    static private final String logStore = "javasdk2";
    private final String source = "127.0.0.1";
    private final int defaultShardNum = 2;

    @Test
    public void testBatchGetLogFastPb() {
        LogStore logStoreRes = new LogStore(logStore, 1, defaultShardNum);
        boolean includeMeta = randomBoolean();
        logStoreRes.setAppendMeta(includeMeta);
        reCreateLogStore(project, logStoreRes);

        int caseStartTime = getNowTimestamp();
        String caseTopic = "sls_java_topic_" + caseStartTime;
        List<LogItem> logGroupSample = new ArrayList<LogItem>();
        LogItem logItemSample = new LogItem(getNowTimestamp());
        logItemSample.PushBack("key", "value");
        logItemSample.PushBack("ID", "id");
        logGroupSample.add(logItemSample);

        TagContent testTag = new TagContent("Tag", "value");
        PutLogsRequest request = new PutLogsRequest(project, logStore, caseTopic,
                source, logGroupSample);
        request.SetTags(Collections.singletonList(testTag));
        request.SetCompressType(CompressType.GZIP);
        try {
            client.PutLogs(request);
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }

        waitForSeconds(3);

        int newCaseStartTime = getNowTimestamp();
        boolean verified = true;
        try {
            GetCursorResponse cursorRes;
            for (int i = 0; i < defaultShardNum; i++) {
                cursorRes = client.GetCursor(project, logStore, i, CursorMode.BEGIN);
                System.out.println("ShardId:" + i);
                System.out.println("Begin cursor:" + cursorRes.GetCursor());
                cursorRes = client.GetCursor(project, logStore, i, caseStartTime - 1);
                System.out.println("cursor:" + cursorRes.GetCursor());
                BatchGetLogResponse logDataRes = client.BatchGetLog(project, logStore, i, 1, cursorRes.GetCursor());
                for (int lgIdx = 0; lgIdx < logDataRes.GetCount(); ++lgIdx) {
                    FastLogGroup logGroup = logDataRes.GetLogGroup(lgIdx).GetFastLogGroup();
                    System.out.println("----------------\nlogGroup: " + lgIdx);
                    if (logGroup.hasCategory()) {
                        System.out.println("Category:");
                        System.out.println(logGroup.getCategory());
                        logGroup.getCategoryBytes();
                    }
                    if (logGroup.hasTopic()) {
                        System.out.println("Topic:");
                        System.out.println(logGroup.getTopic());
                        logGroup.getTopicBytes();
                    }
                    if (logGroup.hasMachineUUID()) {
                        System.out.println("MachineUUID:");
                        System.out.println(logGroup.getMachineUUID());
                        logGroup.getMachineUUIDBytes();
                    }
                    if (logGroup.hasSource()) {
                        System.out.println("Source:");
                        System.out.println(logGroup.getSource());
                        logGroup.getSourceBytes();
                    }
                    System.out.println("Tags");
                    for (int tagIdx = 0; tagIdx < logGroup.getLogTagsCount(); ++tagIdx) {
                        FastLogTag logtag = logGroup.getLogTags(tagIdx);
                        System.out.println(String.format("\t%s\t:\t%s", logtag.getKey(), logtag.getValue()));
                        logtag.getKeyBytes();
                        logtag.getValueBytes();
                    }
                    if (logGroup.getLogsCount() == 0) {
                        verified = false;
                    }
                    for (int lIdx = 0; lIdx < logGroup.getLogsCount(); ++lIdx) {
                        FastLog log = logGroup.getLogs(lIdx);
                        LogItem logItem = new LogItem();
                        logItem.SetTime(log.getTime());
                        System.out.println("--------\nLog: " + lIdx + ", time: " + log.getTime() + ", GetContentCount: " + log.getContentsCount());
                        for (int cIdx = 0; cIdx < log.getContentsCount(); ++cIdx) {
                            FastLogContent content = log.getContents(cIdx);
                            logItem.PushBack(content.getKey(), content.getValue());
                            System.out.println(content.getKey() + "\t:\t" + content.getValue());
                            content.getKeyBytes();
                            content.getValueBytes();
                        }
                        if (!VerifyLogItem(logItem, logItemSample)) {
                            verified = false;
                        }
                    }
                    try {
                        byte[] logGroupBytes = logGroup.getBytes();

                        Logs.LogGroup logGroup1 = Logs.LogGroup.parseFrom(logGroupBytes);
                        verifyLogGroupEquals(logGroup, logGroup1);

                        client.PutLogs(project, logStore, logGroupBytes, "", null);
                        client.PutLogs(project, logStore, logGroupBytes, CONST_LZ4, null);
                        client.PutLogs(project, logStore, logGroupBytes, CONST_GZIP_ENCODING, null);
                    } catch (LogException e) {
                        System.out.println("RID:" + e.GetRequestId());
                        System.out.println("ErrorCode:" + e.GetErrorCode());
                        System.out.println("ErrorMessage:" + e.GetErrorMessage());
                        fail();
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                        System.out.println("Illegal message:");
                        System.out.println(e.getUnfinishedMessage().toString());
                        fail();
                    }
                }
            }

        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }

        waitForSeconds(3);

        System.out.println("######################################");
        try {
            GetCursorResponse cursorRes;
            for (int i = 0; i < defaultShardNum; i++) {
                cursorRes = client.GetCursor(project, logStore, i, CursorMode.BEGIN);
                System.out.println("ShardId:" + i);
                System.out.println("Begin cursor:" + cursorRes.GetCursor());
                cursorRes = client.GetCursor(project, logStore, i, newCaseStartTime - 1);
                System.out.println("cursor:" + cursorRes.GetCursor());
                BatchGetLogResponse logDataRes = client.BatchGetLog(project, logStore, i, 1, cursorRes.GetCursor());
                for (int lgIdx = 0; lgIdx < logDataRes.GetCount(); ++lgIdx) {
                    System.out.println("----------------\nlogGroup: " + lgIdx);
                    FastLogGroup logGroup = logDataRes.GetLogGroup(lgIdx).GetFastLogGroup();
                    System.out.println(String.format("\tcategory\t:\t%s\n\tsource\t:\t%s\n\ttopic\t:\t%s\n\tmachineUUID\t:\t%s",
                            logGroup.getCategory(), logGroup.getSource(), logGroup.getTopic(), logGroup.getMachineUUID()));
                    System.out.println("Tags");
                    for (int tagIdx = 0; tagIdx < logGroup.getLogTagsCount(); ++tagIdx) {
                        FastLogTag tag = logGroup.getLogTags(tagIdx);
                        System.out.println(String.format("\t%s\t:\t%s", tag.getKey(), tag.getValue()));
                    }
                    if (logGroup.getLogsCount() < 1) {
                        verified = false;
                    }
                    for (int lIdx = 0; lIdx < logGroup.getLogsCount(); ++lIdx) {
                        FastLog log = logGroup.getLogs(lIdx);
                        LogItem logItem = new LogItem();
                        logItem.SetTime(log.getTime());
                        System.out.println("--------\nLog: " + lIdx + ", time: " + log.getTime() + ", GetContentCount: " + log.getContentsCount());
                        for (int cIdx = 0; cIdx < log.getContentsCount(); ++cIdx) {
                            FastLogContent content = log.getContents(cIdx);
                            System.out.println(content.getKey() + "\t:\t" + content.getValue());
                            logItem.PushBack(content.getKey(), content.getValue());
                        }
                        if (!VerifyLogItem(logItem, logItemSample)) {
                            verified = false;
                        }
                    }
                }
            }

        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }

        assertTrue("Verify failed", verified);
    }

    private static void verifyLogGroupEquals(FastLogGroup logGroup1, Logs.LogGroup logGroup2) {
        assertFalse(logGroup1.hasCategory());
        assertFalse(logGroup2.hasCategory());
        verifyPbStringEquals(logGroup1.hasSource(), logGroup2.hasSource(), logGroup1.getSource(), logGroup2.getSource());
        verifyPbStringEquals(logGroup1.hasTopic(), logGroup2.hasTopic(), logGroup1.getTopic(), logGroup2.getTopic());
        verifyPbStringEquals(logGroup1.hasMachineUUID(), logGroup2.hasMachineUUID(), logGroup1.getMachineUUID(), logGroup2.getMachineUUID());

        assertEquals(logGroup1.getLogsCount(), logGroup2.getLogsCount());
        assertEquals(logGroup1.getLogsCount(), 1);
        for (int x = 0; x < logGroup1.getLogsCount(); x++) {
            FastLog log1 = logGroup1.getLogs(x);
            Logs.Log log2 = logGroup2.getLogs(x);
            assertEquals(log1.getTime(), log2.getTime());
            for (int j = 0; j < log1.getContentsCount(); j++) {
                FastLogContent content1 = log1.getContents(j);
                Logs.Log.Content content2 = log2.getContents(j);
                assertEquals(content1.getKey(), content2.getKey());
                assertEquals(content1.getValue(), content2.getValue());
            }
        }
        assertEquals(logGroup1.getLogTagsCount(), logGroup2.getLogTagsCount());
        for (int x = 0; x < logGroup1.getLogTagsCount(); x++) {
            assertEquals(logGroup1.getLogTags(x).getKey(), logGroup2.getLogTags(x).getKey());
            assertEquals(logGroup1.getLogTags(x).getValue(), logGroup2.getLogTags(x).getValue());
        }
    }

    private static void verifyPbStringEquals(boolean fastHas, boolean normalHas, String fast, String normal) {
        assertEquals(fastHas, normalHas);
        if (fastHas) {
            assertEquals(fast, normal);
        } else {
            assertNull(fast);
        }
    }
}
