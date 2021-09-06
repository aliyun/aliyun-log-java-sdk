package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateIndexRequest;
import com.aliyun.openservices.log.request.PullLogsRequest;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.PullLogsResponse;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public abstract class BaseDataTest extends FunctionTest {
    protected String project;
    protected LogStore logStore;
    protected int timestamp;
    protected String PACK_ID_PREFIX;
    protected int SHARD_COUNT = 4;

    @Before
    public void ensureDataReady() {
        timestamp = getNowTimestamp();
        PACK_ID_PREFIX = "ABCDEF" + timestamp + "-";
        project = "test-project-" + timestamp;
        logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(SHARD_COUNT);
        logStore.SetLogStoreName("test-logstore-" + timestamp);
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(randomBoolean());
        createOrUpdateLogStore(project, logStore);
    }

    @After
    public void cleanUp() {
        safeDeleteLogStore(project, logStore.GetLogStoreName());
        safeDeleteProject(project);
    }

    protected int prepareLogs() throws LogException {
        int logGroupCount = randomBetween(50, 100);
        for (int i = 1; i <= logGroupCount; i++) {
            List<LogItem> logItems = new ArrayList<LogItem>(10);
            for (int j = 1; j <= 10; j++) {
                LogItem logItem = new LogItem(timestamp);
                logItem.PushBack("key-" + j, "value-" + j);
                logItems.add(logItem);
            }
            List<TagContent> tags = new ArrayList<TagContent>(2);
            tags.add(new TagContent("__pack_id__", PACK_ID_PREFIX + i));
            tags.add(new TagContent("__extra_tag__", "extra_tag_value"));
            PutLogsRequest request = new PutLogsRequest(project, logStore.GetLogStoreName(), "", "test-source", logItems);
            request.SetTags(tags);
            client.PutLogs(request);
        }
        waitForSeconds(10);
        return logGroupCount;
    }

    protected int verifyPull() throws LogException {
        int logGroupSize = 0;
        int logGroupSizeByPull;
        for (int i = 0; i < SHARD_COUNT; i++) {
            String cur;
            GetCursorResponse endCur = client.GetCursor(project, logStore.GetLogStoreName(), i, Consts.CursorMode.END);
            GetCursorResponse cursor = client.GetCursor(project, logStore.GetLogStoreName(), i, Consts.CursorMode.BEGIN);
            cur = cursor.GetCursor();
            do {
                PullLogsResponse pullLogs = client.pullLogs(new PullLogsRequest(project, logStore.GetLogStoreName(), i, 10, cur));
                cur = pullLogs.getNextCursor();
                logGroupSizeByPull = pullLogs.getCount();
                logGroupSize += logGroupSizeByPull;
                for (LogGroupData logGroup : pullLogs.getLogGroups()) {
                    for (Logs.Log log : logGroup.GetLogGroup().getLogsList()) {
                        for (Logs.Log.Content content : log.getContentsList()) {
                            String key = content.getKey();
                            String value = content.getValue();
                            if (!key.startsWith("key-") || !value.startsWith("value-") || !key.substring(4).equals(value.substring(6))) {
                                throw new RuntimeException("Inconsistent data");
                            }
                        }
                    }
                }
            } while (!endCur.GetCursor().equals(cur));
        }
        return logGroupSize;
    }

    protected int verifyGet() throws LogException {
        int totalSize = 0;
        int size;
        do {
            GetLogsResponse logs = client.GetLogs(project, logStore.GetLogStoreName(), timestamp - 1800,
                    timestamp + 1800, "", "", 100, totalSize, true);
            size = logs.GetCount();
            totalSize += size;
            for (QueriedLog log : logs.GetLogs()) {
                for (LogContent mContent : log.mLogItem.mContents) {
                    String mKey = mContent.mKey;
                    String mValue = mContent.mValue;
                    if (mKey.startsWith("key-")) {
                        if (!mValue.startsWith("value-") || !mKey.substring(4).equals(mValue.substring(6))) {
                            throw new RuntimeException("Inconsistent data");
                        }
                    }
                }
            }
        } while (size != 0);
        return totalSize;
    }

    protected void enableIndex() throws LogException {
        Index index = new Index();
        index.SetTtl(7);
        index.setMaxTextLen(0);
        index.setLogReduceEnable(false);
        List<String> list = Arrays.asList(",", " ", "'", "\"", ";", "=", "(", ")", "[", "]", "{", "}", "?", "@", "&", "<", ">", "/", ":", "\n", "\t", "\r");
        IndexKeys indexKeys = new IndexKeys();
        for (int i = 1; i <= 10; i++) {
            indexKeys.AddKey("key-"+i, new IndexKey(list,false, "text", ""));
        }
        index.SetKeys(indexKeys);
        try {
            client.CreateIndex(new CreateIndexRequest(project, logStore.GetLogStoreName(), index));
        } catch (LogException e) {
            fail("Enable Index Failed!");
        }
        waitOneMinutes();
    }

}
