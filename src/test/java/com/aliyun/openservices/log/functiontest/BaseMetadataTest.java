package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PullLogsRequest;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.PullLogsResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseMetadataTest extends FunctionTest {

    static String TEST_PROJECT = "project-intg-test-" + getNowTimestamp();

    @BeforeClass
    public static void setUp() {
        createProjectIfNotExist(TEST_PROJECT, "For Intg testing");
    }

    void ensureLogStoreEnabled(String project, String logStoreName, boolean enabled) {
        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(logStoreName);
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.setAppendMeta(enabled);
        createOrUpdateLogStore(project, logStore);
    }

    private List<Logs.LogGroup> pullAllLogGroups(String project, String logStore, int shardNum) throws LogException {
        List<Logs.LogGroup> groups = new ArrayList<Logs.LogGroup>();
        for (int i = 0; i < shardNum; i++) {
            pullForShard(project, logStore, i, groups);
        }
        return groups;
    }

    private void pullForShard(String project, String logStore, int shard, List<Logs.LogGroup> results) throws LogException {
        GetCursorResponse cursorResponse = client.GetCursor(project, logStore, shard, Consts.CursorMode.BEGIN);
        String cursor = cursorResponse.GetCursor();
        while (true) {
            PullLogsRequest request = new PullLogsRequest(project, logStore, shard, 1000, cursor);
            PullLogsResponse response = client.pullLogs(request);
            for (LogGroupData data : response.getLogGroups()) {
                results.add(data.GetLogGroup());
            }
            final String nextCursor = response.getNextCursor();
            if (nextCursor == null || nextCursor.equals(cursor)) {
                break;
            }
            cursor = nextCursor;
        }
    }

    protected void checkLogGroup(Logs.LogGroup logGroup) {
        for (Logs.Log log : logGroup.getLogsList()) {
            assertEquals(log.getContentsCount(), 1);
            assertEquals("ID", log.getContents(0).getKey());
            assertTrue(log.getContents(0).getValue().startsWith("id_"));
        }
    }

    int countAppended(String project, String logStore, int numShard, Predicate predicate) throws LogException {
        List<Logs.LogGroup> logGroups = pullAllLogGroups(project, logStore, numShard);
        int n = 0;
        for (Logs.LogGroup group : logGroups) {
            if (predicate.test(group)) {
                ++n;
            }
            checkLogGroup(group);
        }
        return n;
    }

    int countLogGroupWithClientIpTag(String project, String logStore, int numShard) throws LogException {
        Predicate predicate = new Predicate() {
            @Override
            public boolean test(Logs.LogGroup group) {
                int clientIp = 0;
                int receiveTime = 0;
                System.out.println("Tag count: " + group.getLogTagsCount());
                for (Logs.LogTag tag : group.getLogTagsList()) {
                    System.out.println(tag);
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

        boolean test(Logs.LogGroup logGroup);
    }

    @AfterClass
    public static void tearDown() {
        safeDeleteProject(TEST_PROJECT);
    }
}
