package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogGroup;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.TagContent;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.MetaAPIBaseFunctionTest;
import com.aliyun.openservices.log.request.BatchPutLogsRequest;
import com.aliyun.openservices.log.request.PutLogsRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

public class BatchPutLogsTest extends MetaAPIBaseFunctionTest {

    private static final String LOGSTORE = "test";
    private static final String project = TEST_PROJECT;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        assertTrue(safeCreateLogStore(TEST_PROJECT, new LogStore("test", 1, 1))); 
        waitOneMinutes();
    }

    @Test
    public void testWriteLogs() throws Exception{
        testBatchWrite();
        testWrite();
        testWriteSizeLimit();
    }

    public void testBatchWrite() throws LogException, InterruptedException {
        List<LogItem> logItems = new ArrayList<LogItem>();
        for (int i = 0; i < 10; i++) {
            LogItem logItem = new LogItem();
            logItem.PushBack("test", String.valueOf(i));
            logItem.PushBack("test2", String.valueOf(i));
            logItems.add(logItem);
        }
        List<TagContent> tags = new ArrayList<TagContent>();
        tags.add(new TagContent("tag1", "tagValue"));
        LogGroup logGroup1 = new LogGroup(logItems, tags, "topic1", null);

        LogGroup logGroup2 = new LogGroup(logItems, tags, "topic12", null);

        List<LogGroup> logGroups = new ArrayList<LogGroup>();
        logGroups.add(logGroup1);
        logGroups.add(logGroup2);
        String hashKey = Math.random() > 0.5 ? "a" : null;
        BatchPutLogsRequest request = new BatchPutLogsRequest(project, LOGSTORE, logGroups, hashKey);
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

    public void testWrite() throws LogException, InterruptedException {
        List<LogItem> logItems = new ArrayList<LogItem>();
        for (int i = 0; i < 10; i++) {
            LogItem logItem = new LogItem();
            logItem.PushBack("test", String.valueOf(i));
            logItem.PushBack("test2", String.valueOf(i));
            logItems.add(logItem);
        }

        PutLogsRequest request = new PutLogsRequest(project, LOGSTORE, "topic", logItems);
        request.SetSource("1.1.1.1");
        request.setCompressType(Consts.CompressType.LZ4);
        client.PutLogs(request);

        request.setCompressType(Consts.CompressType.ZSTD);
        request.SetSource("1.1.1.1");
        client.PutLogs(request);

        request.setCompressType(Consts.CompressType.GZIP);
        request.SetSource("1.1.1.1");
        client.PutLogs(request);

        request.setCompressType(Consts.CompressType.NONE);
        request.SetSource("1.1.1.1");
        client.PutLogs(request);

    }

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
        try {
            BatchPutLogsRequest request = new BatchPutLogsRequest(project, LOGSTORE, logGroups, null);
            client.batchPutLogs(request);
            fail();
        } catch (LogException e) {
            Assert.assertEquals(e.getErrorCode(), "PostBodyTooLarge");
        }

    }
}
