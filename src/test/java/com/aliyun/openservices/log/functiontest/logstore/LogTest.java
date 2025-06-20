package com.aliyun.openservices.log.functiontest.logstore;


import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PullLogsRequest;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.PullLogsResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LogTest extends FunctionTest {

    protected static String TEST_PROJECT = makeProjectName();

    @BeforeClass
    public static void setUp() {
        createProjectIfNotExist(TEST_PROJECT, "For Intg testing");
    }

    protected void ensureLogStoreEnabled(String project, String logStoreName, boolean enabled) {
        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(logStoreName);
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.setAppendMeta(enabled);
        createOrUpdateLogStore(project, logStore);
    }

    protected List<FastLogGroup> pullAllLogGroups(String project, String logStore, int shardNum) throws LogException {
        List<FastLogGroup> groups = new ArrayList<>();
        for (int i = 0; i < shardNum; i++) {
            pullForShard(project, logStore, i, groups);
        }
        return groups;
    }

    private void pullForShard(String project, String logStore, int shard, List<FastLogGroup> results) throws LogException {
        GetCursorResponse cursorResponse = client.GetCursor(project, logStore, shard, Consts.CursorMode.BEGIN);
        String cursor = cursorResponse.GetCursor();
        while (true) {
            PullLogsRequest request = new PullLogsRequest(project, logStore, shard, 1000, cursor);
            PullLogsResponse response = client.pullLogs(request);
            List<LogGroupData> logGroupDataList = response.getLogGroups();
            if (!logGroupDataList.isEmpty()) {
                Assert.assertTrue(response.getCursorTime() > 0);
            } else {
                Assert.assertEquals(0, response.getCursorTime());
            }
            for (LogGroupData data : logGroupDataList) {
                results.add(data.GetFastLogGroup());
            }
            final String nextCursor = response.getNextCursor();
            if (cursor.equals(nextCursor)) {
                Assert.assertTrue(shard + "$" + cursor, response.isEndOfCursor());
            }
            if (nextCursor == null || nextCursor.equals(cursor)) {
                break;
            }
            cursor = nextCursor;
        }
    }

    protected void checkLogGroup(FastLogGroup logGroup) {
        for (FastLog log : logGroup.getLogs()) {
            assertEquals(log.getContentsCount(), 1);
            assertEquals("ID", log.getContents(0).getKey());
            assertTrue(log.getContents(0).getValue().startsWith("id_"));
        }
    }

    protected int countAppended(String project, String logStore, int numShard, Predicate predicate) throws LogException {
        List<FastLogGroup> logGroups = pullAllLogGroups(project, logStore, numShard);
        int n = 0;
        for (FastLogGroup group : logGroups) {
            if (predicate.test(group)) {
                ++n;
            }
            checkLogGroup(group);
        }
        return n;
    }

    protected int countLogGroupWithClientIpTag(String project, String logStore, int numShard) throws LogException {
        Predicate predicate = new Predicate() {
            @Override
            public boolean test(FastLogGroup group) {
                int clientIp = 0;
                int receiveTime = 0;
                System.out.println("Tag count: " + group.getLogTagsCount());
                for (int i = 0; i < group.getLogTagsCount(); ++i) {
                    FastLogTag tag = group.getLogTags(i);
                    if ("__client_ip__".equals(tag.getKey())) {
                        ++clientIp;
                    }
                    if ("__receive_time__".equals(tag.getKey())) {
                        ++receiveTime;
                    }
                }
                return clientIp == 1 && receiveTime == 1;
            }
        };
        return countAppended(project, logStore, numShard, predicate);
    }

    public interface Predicate {

        boolean test(FastLogGroup logGroup);
    }

    @AfterClass
    public static void tearDown() {
        safeDeleteProjectWithoutSleep(TEST_PROJECT);
    }
}
