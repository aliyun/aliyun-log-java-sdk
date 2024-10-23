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

public class SlsLoghubDataFunctionTest extends DataVerify {
    static private final String project = TEST_PROJECT;
    static private final String logStore = "java-sdk";
    private final int startTime = getNowTimestamp();
    private final String topic = "sls_java_topic_" + startTime;
    private final String source = "127.0.0.1";
    private final int defaultShardNum = 2;

    @Test
    public void testLogData() throws LogException {
        client.getClientConfiguration().setRetryDisabled(true);
        LogStore logStoreRes = new LogStore(logStore, 1, defaultShardNum);
        logStoreRes.setAppendMeta(randomBoolean());
        client.CreateLogStore(project, logStoreRes);
        waitOneMinutes();
        List<LogItem> logGroupSample = new ArrayList<LogItem>();
        LogItem logItemSample = new LogItem(getNowTimestamp());
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

        waitForSeconds(20);

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
            waitOneMinutes();
        } catch (LogException e) {
            System.out.println("ErrorCode:" + e.GetErrorCode());
            System.out.println("ErrorMessage:" + e.GetErrorMessage());
            fail();
        }

        shardAPI();
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

}
