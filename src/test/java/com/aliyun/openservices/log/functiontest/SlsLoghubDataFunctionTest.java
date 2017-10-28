package com.aliyun.openservices.log.functiontest;

import static com.aliyun.openservices.log.common.Consts.CONST_GZIP_ENCODING;
import static com.aliyun.openservices.log.common.Consts.CONST_LZ4;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.aliyun.openservices.log.common.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.ListShardResponse;

public class SlsLoghubDataFunctionTest {
    static private final String endPoint = "http://cn-hangzhou-corp.sls.aliyuncs.com";
    static private final String akId = "";
    static private final String ak = "";
    static private final String project = "";
    static private final String logStore = "javasdk";
    static private final Client client = new Client(endPoint, akId, ak);
    private final int startTime = (int) (new Date().getTime() / 1000);
    private final String topic = "sls_java_topic_" + String.valueOf(startTime);
    private final String source = "127.0.0.1";
    private final int defaultShardNum = 2;

    @BeforeClass
    public static void SetupOnce() {
    }

    @AfterClass
    public static void CleanUpOnce() {
    }

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
        try {
            client.DeleteLogStore(project, logStore);
            Thread.sleep(60 * 1000);
        } catch (LogException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            client.CreateLogStore(project, logStoreRes);
            Thread.sleep(60 * 1000);
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Vector<LogItem> logGroupSample = new Vector<LogItem>();
        LogItem logItemSample = new LogItem(
                (int) (new Date().getTime() / 1000));
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
            assertTrue(false);
        }

        try {
            Thread.sleep(120 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
                            for(Logs.Log.Content content:newLogGroup.getLogs(0).getContentsList()) {
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
            assertTrue(e.GetErrorMessage(), false);
        }

        assertTrue("Verify failed", verified);

        try {
            client.MergeShards(project, logStore, 0);
            Thread.sleep(60 * 1000);
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        shardAPI();

        try {
            client.DeleteLogStore(project, logStore);
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            assertTrue(false);
        }
    }

    @Test
    public void testBatchGetLogFastPb() {
        LogStore logStoreRes = new LogStore(logStore, 1, defaultShardNum);
        try {
            client.DeleteLogStore(project, logStore);
            Thread.sleep(60 * 1000);
        } catch (LogException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            client.CreateLogStore(project, logStoreRes);
            Thread.sleep(60 * 1000);
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int caseStartTime = (int) (new Date().getTime() / 1000);
        String caseTopic = "sls_java_topic_" + String.valueOf(caseStartTime);
        Vector<LogItem> logGroupSample = new Vector<LogItem>();
        LogItem logItemSample = new LogItem(
                (int) (new Date().getTime() / 1000));
        logItemSample.PushBack("key", "value");
        logItemSample.PushBack("ID", "id");
        logGroupSample.add(logItemSample);

        try {
            PutLogsRequest request = new PutLogsRequest(project, logStore, caseTopic,
                    source, logGroupSample);
            request.SetCompressType(CompressType.GZIP);
            client.PutLogs(request);
        } catch (LogException e) {
            System.out.println("RID:" + e.GetRequestId());
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            assertTrue(false);
        }

        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int newCaseStartTime = (int) (new Date().getTime() / 1000);
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
                        logGroup.getCategory();
                        logGroup.getCategoryBytes();
                    }
                    if (logGroup.hasTopic()) {
                        logGroup.getTopic();
                        logGroup.getTopicBytes();
                    }
                    if (logGroup.hasMachineUUID()) {
                        logGroup.getMachineUUID();
                        logGroup.getMachineUUIDBytes();
                    }
                    if (logGroup.hasSource()) {
                        logGroup.getSource();
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
                        client.PutLogs(project, logStore, logGroupBytes, "", null);
                        client.PutLogs(project, logStore, logGroupBytes, CONST_LZ4, null);
                        client.PutLogs(project, logStore, logGroupBytes, CONST_GZIP_ENCODING, null);
                    } catch (LogException e) {
                        System.out.println("RID:" + e.GetRequestId());
                        System.out.println("ErrorCode:" + e.GetErrorCode());
                        System.out.println("ErrorMessage:" + e.GetErrorMessage());
                        assertTrue(false);
                    }
                }
            }

        } catch (LogException e) {
            assertTrue(e.GetErrorMessage(), false);
        }

        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
            assertTrue(e.GetErrorMessage(), false);
        }

        assertTrue("Verify failed", verified);

        try {
            client.DeleteLogStore(project, logStore);
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            assertTrue(false);
        }
    }

    private void shardAPI() {
        try {
            ListShardResponse listRes = client.ListShard(project, logStore);
            assertEquals("ShardNum does not match", defaultShardNum + 1, listRes.GetShards().size());
        } catch (LogException e) {
            assertTrue(e.GetErrorMessage(), false);
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
        try {
            client.DeleteLogStore(project, logStore);
            Thread.sleep(60 * 1000);
        } catch (LogException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            client.CreateLogStore(project, logStoreRes);
            Thread.sleep(60 * 1000);
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int caseStartTime = (int) (new Date().getTime() / 1000);
        String caseTopic = "sls_java_topic_" + String.valueOf(caseStartTime);
        Vector<LogItem> logGroupSample = new Vector<LogItem>();
        LogItem logItemSample = new LogItem(
                (int) (new Date().getTime() / 1000));
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
            assertTrue(false);
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
            assertTrue(false);
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
            assertTrue(false);
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
            assertTrue(false);
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
            assertTrue(false);
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
            assertTrue(false);
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
            assertTrue(false);
        }

        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int newCaseStartTime = (int) (new Date().getTime() / 1000);
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
            assertTrue(e.GetErrorMessage(), false);
        }

        assertTrue("Verify failed", verified);

        try {
            client.DeleteLogStore(project, logStore);
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            assertTrue(false);
        }
    }

}
