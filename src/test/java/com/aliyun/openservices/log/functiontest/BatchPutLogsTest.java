package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogGroup;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.TagContent;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.BatchPutLogsRequest;
import com.aliyun.openservices.log.request.PutLogsRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BatchPutLogsTest extends MetaAPIBaseFunctionTest {

    private static final String LOGSTORE = "test";
    @Test
    public void testBatchWrite() throws LogException {
        List<LogItem> logItems = new ArrayList<LogItem>();
        for (int i = 0; i < 10; i++) {
            LogItem logItem = new LogItem();
            logItem.PushBack("test", String.valueOf(i));
            logItem.PushBack("test2", String.valueOf(i));
            logItems.add(logItem);
        }
        List<TagContent> tags = new ArrayList<TagContent>();
        tags.add(new TagContent("tag1", "tagValue"));
        LogGroup logGroup1 = new LogGroup(logItems, tags, "topic1", "");

        LogGroup logGroup2 = new LogGroup(logItems, tags, "topic12", "source2");

        List<LogGroup> logGroups = new ArrayList<LogGroup>();
        logGroups.add(logGroup1);
        logGroups.add(logGroup2);
        String hashKey = Math.random() > 0.5 ? "a" : null;
        BatchPutLogsRequest request = new BatchPutLogsRequest(TEST_PROJECT, LOGSTORE, logGroups, hashKey);
        request.setCompressType(Consts.CompressType.LZ4);
        client.batchPutLogs(request);
        request.setCompressType(Consts.CompressType.ZSTD);
        client.batchPutLogs(request);
        try {
            request.setCompressType(Consts.CompressType.GZIP);
            client.batchPutLogs(request);
            Assert.fail();
        } catch (LogException e) {

        }
        try {
            request.setCompressType(Consts.CompressType.NONE);
            client.batchPutLogs(request);
            Assert.fail();
        } catch (LogException e) {

        }
    }

    @Test
    public void testWrite() throws LogException {
        List<LogItem> logItems = new ArrayList<LogItem>();
        for (int i = 0; i < 10; i++) {
            LogItem logItem = new LogItem();
            logItem.PushBack("test", String.valueOf(i));
            logItem.PushBack("test2", String.valueOf(i));
            logItems.add(logItem);
        }

        PutLogsRequest request = new PutLogsRequest(TEST_PROJECT, LOGSTORE, "topic", logItems);
        request.setCompressType(Consts.CompressType.LZ4);
        client.PutLogs(request);

        request.setCompressType(Consts.CompressType.ZSTD);
        client.PutLogs(request);

        request.setCompressType(Consts.CompressType.GZIP);
        client.PutLogs(request);

        request.setCompressType(Consts.CompressType.NONE);
        client.PutLogs(request);

    }

    @Test
    public void testWriteSizeLimit() throws LogException {
        List<LogItem> logItems = new ArrayList<LogItem>();
        for (int i = 0; i < 20; i++) {
            LogItem logItem = new LogItem();
            logItem.PushBack("test", String.valueOf(i));
            logItem.PushBack("test2", String.valueOf(i));
            StringBuilder largeValue = new StringBuilder();
            for (int j = 0; j < 1024 * 1024; j++) {
                largeValue.append("a");
            }
            logItem.PushBack("largeContent", largeValue.toString());
            logItems.add(logItem);
        }
        List<TagContent> tags = new ArrayList<TagContent>();
        tags.add(new TagContent("tag1", "tagValue"));
        LogGroup logGroup1 = new LogGroup(logItems, tags, "topic1", "");

        List<LogGroup> logGroups = new ArrayList<LogGroup>();
        logGroups.add(logGroup1);

        BatchPutLogsRequest request = new BatchPutLogsRequest(TEST_PROJECT, LOGSTORE, logGroups, null);
        client.batchPutLogs(request);
    }
}
