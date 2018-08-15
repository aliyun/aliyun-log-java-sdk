package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.common.FastLog;
import com.aliyun.openservices.log.common.FastLogContent;
import com.aliyun.openservices.log.common.FastLogGroup;
import com.aliyun.openservices.log.common.FastLogTag;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.common.TagContent;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.ListShardResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.After;
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


public class SlsLoghubDataFunctionTest extends FunctionTest {
    static private final String project = "project1";
    static private final String logStore = "javasdk2";
    private final int startTime = timestampNow();
    private final String topic = "sls_java_topic_" + String.valueOf(startTime);
    private final String source = "127.0.0.1";
    private final int defaultShardNum = 2;


    private boolean VerifyLogItem(LogItem logItem, LogItem logItemSample) {
        boolean ret = false;

        do {
            if (logItem.GetTime() != logItemSample.GetTime()) {
                break;
            } else {
                System.out.println("Time:" + logItem.GetTime());
                System.out.println("Time:" + logItemSample.GetTime());
            }

            if (logItem.GetLogContents().size() != logItemSample.GetLogContents().size()) {
                break;
            } else {
                System.out.println("Size:" + logItem.GetLogContents().size());
                System.out.println("Size:" + logItemSample.GetLogContents().size());
            }

            boolean fail = false;
            for (int i = 0; i < logItem.GetLogContents().size(); i++) {
                String key = logItem.GetLogContents().get(i).GetKey();
                String value = logItem.GetLogContents().get(i).GetValue();
                boolean hasKey = false;
                boolean hasValue = false;
                for (int j = 0; j < logItemSample.GetLogContents().size(); j++) {
                    if (key.equals(logItemSample.GetLogContents().get(j).GetKey())) {
                        hasKey = true;
                        if (value.equals(logItemSample.GetLogContents().get(j).GetValue())) {
                            hasValue = true;
                            /*
							System.out.println("Key:" + key);
							System.out.println("Key:" + logItemSample.GetLogContents().get(j).GetKey());
							System.out.println("Value:" + value);
							System.out.println("Value:" + logItemSample.GetLogContents().get(j).GetValue());
							*/
                            break;
                        } else {
                            break;
                        }
                    }
                }

                if (!hasKey || !hasValue) {
                    fail = true;
                }

                if (fail) {
                    break;
                }
            }

            if (fail) {
                break;
            }

            ret = true;
        } while (false);

        return ret;
    }

    @Test
    public void testLogData() {
        LogStore logStoreRes = new LogStore(logStore, 1, defaultShardNum);
        logStoreRes.setAppendMeta(randomBoolean());
        reCreateLogStore(project, logStoreRes);

        List<LogItem> logGroupSample = new ArrayList<LogItem>();
        LogItem logItemSample = new LogItem(timestampNow());
        logItemSample.PushBack("key", "value");
        logItemSample.PushBack("ID", "id");
        logGroupSample.add(logItemSample);

        try {
            PutLogsRequest request = new PutLogsRequest(project, logStore, topic,
                    source, logGroupSample);
            request.SetCompressType(CompressType.GZIP);
            client.PutLogs(request);
            //client.PutLogs(project, logStore, topic, logGroupSample, source);
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }

        waitForSeconds(120);

        boolean verified = false;
        try {
            GetCursorResponse cursorRes;
            for (int i = 0; i < defaultShardNum; i++) {
                cursorRes = client.GetCursor(project, logStore, i, CursorMode.BEGIN);
                System.out.println("Begin cursor:" + cursorRes.GetCursor());
                System.out.println("ShardId:" + i);
                cursorRes = client.GetCursor(project, logStore, i, startTime - 1);
                System.out.println("cursor:" + cursorRes.GetCursor());
                BatchGetLogResponse logDataRes = client.BatchGetLog(project, logStore, i, 1000, cursorRes.GetCursor());
                if (logDataRes.GetCount() > 0) {
                    List<LogGroupData> logGroups = logDataRes.GetLogGroups();

                    for (LogGroupData logGroup : logGroups) {
                        Logs.LogGroup newLogGroup = logGroup.GetLogGroup();
                        if (logGroup.GetAllLogs().size() == 1 && logGroup.GetTopic().equals(topic) &&
                                newLogGroup.getLogsCount() == 1 && newLogGroup.getTopic().equals(topic)) {
                            ArrayList<LogContent> logContents = new ArrayList<LogContent>();
                            for (Logs.Log.Content content : newLogGroup.getLogs(0).getContentsList()) {
                                logContents.add(new LogContent(content.getKey(), content.getValue()));
                            }
                            LogItem newLogItem = new LogItem(newLogGroup.getLogs(0).getTime(), logContents);
                            if (VerifyLogItem(logGroup.GetAllLogs().get(0), logItemSample) && VerifyLogItem(newLogItem, logItemSample)) {
                                verified = true;
                                break;
                            }
                        }
                    }

                    if (verified) {
                        break;
                    }
                }

            }

        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }

        assertTrue("Verify failed", verified);

        try {
            client.MergeShards(project, logStore, 0);
            waitForSeconds(60);
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }

        shardAPI();
    }

    @Test
    public void testBatchGetLogFastPb() {
        LogStore logStoreRes = new LogStore(logStore, 1, defaultShardNum);
        boolean includeMeta = randomBoolean();
        logStoreRes.setAppendMeta(includeMeta);
        reCreateLogStore(project, logStoreRes);

        int caseStartTime = timestampNow();
        String caseTopic = "sls_java_topic_" + String.valueOf(caseStartTime);
        List<LogItem> logGroupSample = new ArrayList<LogItem>();
        LogItem logItemSample = new LogItem(timestampNow());
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

        int newCaseStartTime = timestampNow();
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

    private void shardAPI() {
        try {
            ListShardResponse listRes = client.ListShard(project, logStore);
            assertEquals("ShardNum does not match", defaultShardNum + 1, listRes.GetShards().size());
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }

        try {
            client.ListShard(project, logStore);
        } catch (LogException e) {
            assertEquals("SLSLogStoreNotExist", e.GetErrorCode());
            assertEquals("logstore xxn not exist", e.GetErrorMessage());
        }
    }

    @Test
    public void testLogTags() {
        LogStore logStoreRes = new LogStore(logStore, 1, 1);
        logStoreRes.setAppendMeta(randomBoolean());
        reCreateLogStore(project, logStoreRes);

        int caseStartTime = timestampNow();
        String caseTopic = "sls_java_topic_" + String.valueOf(caseStartTime);
        List<LogItem> logGroupSample = new ArrayList<LogItem>();
        LogItem logItemSample = new LogItem(timestampNow());
        logItemSample.PushBack("key", "value");
        logItemSample.PushBack("ID", "id");
        logGroupSample.add(logItemSample);

        System.out.println("0#send pb, with tag, no uuid");
        try {
            PutLogsRequest request = new PutLogsRequest(project, logStore, caseTopic,
                    source, logGroupSample);
            request.SetCompressType(CompressType.GZIP);
            ArrayList<TagContent> tags = new ArrayList<TagContent>();
            tags.add(new TagContent("1#1", "xxx"));
            tags.add(new TagContent("1#2", "yyy"));
            request.SetTags(tags);
            client.PutLogs(request);
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }
        System.out.println("1#send pb, with tag and uuid");
        try {
            PutLogsRequest request = new PutLogsRequest(project, logStore, caseTopic,
                    source, logGroupSample);
            request.SetCompressType(CompressType.GZIP);
            ArrayList<TagContent> tags = new ArrayList<TagContent>();
            tags.add(new TagContent("2#1", "xxx"));
            tags.add(new TagContent("2#2", "yyy"));
            request.SetTags(tags);
            client.EnableUUIDTag();
            client.PutLogs(request);
            client.DisableUUIDTag();
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }
        System.out.println("2#send pb, no tag, with uuid");
        try {
            PutLogsRequest request = new PutLogsRequest(project, logStore, caseTopic,
                    source, logGroupSample);
            request.SetCompressType(CompressType.GZIP);
            client.EnableUUIDTag();
            client.PutLogs(request);
            client.DisableUUIDTag();
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }
        System.out.println("3#send json, with tag, no uuid");
        try {
            PutLogsRequest request = new PutLogsRequest(project, logStore, caseTopic,
                    source, logGroupSample);
            request.SetCompressType(CompressType.GZIP);
            ArrayList<TagContent> tags = new ArrayList<TagContent>();
            tags.add(new TagContent("3#1", "xxx"));
            tags.add(new TagContent("3#2", "yyy"));
            request.SetTags(tags);
            request.setContentType(Consts.CONST_SLS_JSON);
            client.PutLogs(request);
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }
        System.out.println("4#send json, with tag and uuid");
        try {
            PutLogsRequest request = new PutLogsRequest(project, logStore, caseTopic,
                    source, logGroupSample);
            request.SetCompressType(CompressType.GZIP);
            request.setContentType(Consts.CONST_SLS_JSON);
            ArrayList<TagContent> tags = new ArrayList<TagContent>();
            tags.add(new TagContent("4#1", "xxx"));
            tags.add(new TagContent("4#2", "yyy"));
            request.SetTags(tags);
            client.EnableUUIDTag();
            client.PutLogs(request);
            client.DisableUUIDTag();
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }
        System.out.println("5#send json, no tag, with uuid");
        try {
            PutLogsRequest request = new PutLogsRequest(project, logStore, caseTopic,
                    source, logGroupSample);
            request.SetCompressType(CompressType.GZIP);
            request.setContentType(Consts.CONST_SLS_JSON);
            client.EnableUUIDTag();
            client.PutLogs(request);
            client.DisableUUIDTag();
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }
        System.out.println("6#send pb, no tag, no uuid");
        try {
            PutLogsRequest request = new PutLogsRequest(project, logStore, caseTopic,
                    source, logGroupSample);
            request.SetCompressType(CompressType.LZ4);
            client.PutLogs(request);
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }

        waitForSeconds(3);

        boolean verified = true;
        try {
            GetCursorResponse cursorRes;
            cursorRes = client.GetCursor(project, logStore, 0, caseStartTime - 1);
            System.out.println("begin cursor:" + cursorRes.GetCursor());
            BatchGetLogResponse logDataRes = client.BatchGetLog(project, logStore, 0, 10, cursorRes.GetCursor());
            assertEquals(logDataRes.GetCount(), 7);
            for (int lgIdx = 0; lgIdx < logDataRes.GetCount(); ++lgIdx) {
                FastLogGroup logGroup = logDataRes.GetLogGroup(lgIdx).GetFastLogGroup();
                System.out.println("----------------\nlogGroup: " + lgIdx);
                if (logGroup.hasCategory()) {
                    System.out.println("category: " + logGroup.getCategory());
                    logGroup.getCategoryBytes();
                }
                if (logGroup.hasTopic()) {
                    System.out.println("topic: " + logGroup.getTopic());
                    logGroup.getTopicBytes();
                }
                if (logGroup.hasMachineUUID()) {
                    System.out.println("machineUUID: " + logGroup.getMachineUUID());
                    logGroup.getMachineUUIDBytes();
                }
                if (logGroup.hasSource()) {
                    System.out.println("source: " + logGroup.getSource());
                    logGroup.getSourceBytes();
                }
                for (int tagIdx = 0; tagIdx < logGroup.getLogTagsCount(); ++tagIdx) {
                    FastLogTag logtag = logGroup.getLogTags(tagIdx);
                    System.out.println(String.format("Tag%d: %s\t%s", tagIdx, logtag.getKey(), logtag.getValue()));
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
                        //System.out.println(content.getKey() + "\t:\t" + content.getValue());
                        content.getKeyBytes();
                        content.getValueBytes();
                    }
                    if (!VerifyLogItem(logItem, logItemSample)) {
                        verified = false;
                    }
                }
            }
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }

        assertTrue("Verify failed", verified);
    }

    @After
    public void tearDown() {
        try {
            client.DeleteLogStore(project, logStore);
            waitForSeconds(60);
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }
    }
}
