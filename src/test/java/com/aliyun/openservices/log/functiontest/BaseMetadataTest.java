package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.BatchGetLogRequest;
import com.aliyun.openservices.log.response.BatchGetLogResponse;
import com.aliyun.openservices.log.response.GetCursorResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class BaseMetadataTest extends FunctionTest {


    void ensureLogStoreEnabled(String project, String logStoreName, boolean enabled) {
        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(logStoreName);
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.setAppendMeta(enabled);
        reCreateLogStore(project, logStore);
    }

    List<Logs.LogGroup> pullAllLogGroups(String project, String logStore, int shardNum) throws LogException {
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
            BatchGetLogRequest request = new BatchGetLogRequest(project, logStore, shard, 1000, cursor);
            BatchGetLogResponse response1 = client.BatchGetLog(request);
            for (LogGroupData data : response1.GetLogGroups()) {
                results.add(data.GetLogGroup());
            }
            if (response1.GetNextCursor() == null || cursor.equals(response1.GetNextCursor())) {
                break;
            }
            cursor = response1.GetNextCursor();
        }
    }

    protected void checkLogGroup(Logs.LogGroup logGroup) {
        for (Logs.Log log : logGroup.getLogsList()) {
            assertEquals(log.getContentsCount(), 1);
            assertEquals("ID", log.getContents(0).getKey());
            assertTrue(log.getContents(0).getValue().startsWith("id_"));
        }
    }

    int countAppended(String project, String logStore, int numShard) throws LogException {
        List<Logs.LogGroup> logGroups = pullAllLogGroups(project, logStore, numShard);
        int n = 0;
        for (Logs.LogGroup group : logGroups) {
            boolean hasClientIp = false;
            boolean hasReceiveTime = false;
            System.out.println("Tag count: " + group.getLogTagsCount());
            for (Logs.LogTag tag : group.getLogTagsList()) {
                System.out.println(tag);
                if ("__client_ip__".equals(tag.getKey())) {
                    hasClientIp = true;
                }
                if ("__receive_time__".equals(tag.getKey())) {
                    hasReceiveTime = true;
                }
            }
            if (hasClientIp && hasReceiveTime) {
                n++;
            }
            checkLogGroup(group);
        }
        return n;
    }

}
