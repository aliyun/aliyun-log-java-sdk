package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.FastLog;
import com.aliyun.openservices.log.common.FastLogContent;
import com.aliyun.openservices.log.common.FastLogGroup;
import com.aliyun.openservices.log.common.FastLogTag;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.TagContent;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.others.DataVerify;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.aliyun.openservices.log.response.GetCursorResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LogTagsTest extends DataVerify {
    static private final String project = TEST_PROJECT;
    static private final String logStore = "javasdk2";
    private final String source = "127.0.0.1";

    @Test
    public void testLogTags() {
        LogStore logStoreRes = new LogStore(logStore, 1, 1);
        logStoreRes.setAppendMeta(randomBoolean());
        reCreateLogStore(project, logStoreRes);

        int caseStartTime = getNowTimestamp();
        String caseTopic = "sls_java_topic_" + caseStartTime;
        List<LogItem> logGroupSample = new ArrayList<LogItem>();
        LogItem logItemSample = new LogItem(getNowTimestamp());
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
                    System.out.println("--------\nLog: " + lIdx + ", time: " + log.getTime() + ", GetContentCount: "
                            + log.getContentsCount());
                    for (int cIdx = 0; cIdx < log.getContentsCount(); ++cIdx) {
                        FastLogContent content = log.getContents(cIdx);
                        logItem.PushBack(content.getKey(), content.getValue());
                        // System.out.println(content.getKey() + "\t:\t" + content.getValue());
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
}
