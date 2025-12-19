package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PullLogsRequest;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.ListShardResponse;
import com.aliyun.openservices.log.response.PullLogsResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

class PullLogsSample {
    private final String endPoint = "";
    private final String akId = "your_access_id";
    private final String ak = "your_access_key";
    private final Client client = new Client(endPoint, akId, ak);
    private final String project = "your_project_name";
    private final String logStore = "your_log_store";

    public static void main(String[] args) {
        // ------------------------Data API------------------------
        PullLogsSample sample = new PullLogsSample();
        // ------------------------Shard------------------------
        List<Integer> shardIds = sample.listShard();

        if (shardIds != null && shardIds.size() > 0) {
            // choose a shard to getCursor and pullLogs
            Integer shardId = shardIds.get(0);
            sample.getCursor(shardId);
            sample.pullLogs(shardId);
            sample.pullLogsWithID(shardId);
        }
    }

    public void getCursor(int shardId) {
        GetCursorResponse res;
        try {
            long fromTime = (int) (System.currentTimeMillis() / 1000.0 - 3600);
            res = client.GetCursor(project, logStore, shardId, fromTime);
            System.out.println("Cursor:" + res.GetCursor());

            res = client.GetCursor(project, logStore, shardId, CursorMode.BEGIN);
            System.out.println("Cursor:" + res.GetCursor());

            res = client.GetCursor(project, logStore, shardId, CursorMode.END);
            System.out.println("shard_id:" + shardId + " Cursor:" + res.GetCursor());
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public void pullLogs(int shardId) {
        try {
            GetCursorResponse cursorRes = client.GetCursor(project,
                    logStore, shardId, CursorMode.BEGIN);
            String cursor = cursorRes.GetCursor();
            int iteration = 100;

            for (int i = 0; i < iteration; i++) {
                PullLogsRequest request = new PullLogsRequest(project, logStore, shardId, 1000, cursor);
                PullLogsResponse response = client.pullLogs(request);
                String next_cursor = response.getNextCursor();
                System.out.print("The Next cursor:" + next_cursor);

                List<LogGroupData> logGroups = response.getLogGroups();
                for (LogGroupData logGroup : logGroups) {
                    FastLogGroup fastLogGroup = logGroup.getFastLogGroup();
                    System.out.println("Source:" + fastLogGroup.getSource());
                    System.out.println("Topic:" + fastLogGroup.getTopic());
                    for (FastLog log : fastLogGroup.getLogs()) {
                        System.out.println("LogTime:" + log.getTime());
                        List<FastLogContent> contents = log.getContents();
                        for (FastLogContent content : contents) {
                            System.out.println(content.getKey() + ":" + content.getValue());
                        }
                    }
                }

                if (cursor.equals(next_cursor)) {
                    break;
                }
                cursor = next_cursor;
            }

        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> listShard() {
        try {
            ListShardResponse res = client.ListShard(project, logStore);
            System.out.println("RequestId:" + res.GetRequestId());
            return res.GetShards().stream().map(Shard::getShardId).collect(Collectors.toList());
        } catch (LogException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String encodeCursor(long offset) {
        byte[] cursorAsBytes = Base64.getEncoder().encode(String.valueOf(offset).getBytes(StandardCharsets.UTF_8));
        return new String(cursorAsBytes, StandardCharsets.UTF_8);
    }

    public void pullLogsWithID(int shardId) {
        try {
            GetCursorResponse cursorRes = client.GetCursor(project,
                    logStore, shardId, CursorMode.BEGIN);
            String cursor = cursorRes.GetCursor();
            PullLogsRequest request = new PullLogsRequest(project, logStore, shardId, 1000, cursor);
            PullLogsResponse response = client.pullLogs(request);

            String next_cursor = response.getNextCursor();
            System.out.print("The Next cursor:" + next_cursor);
            List<LogGroupData> logGroups = response.getLogGroups();
            if (logGroups.isEmpty()) {
                return;
            }
            long offset = Long.parseLong(response.getReadLastCursor()) - logGroups.size() + 1;
            String lastCursor = encodeCursor(offset);
            String seqNoPrefix = shardId + "|";
            for (LogGroupData logGroup : logGroups) {
                FastLogGroup fastLogGroup = logGroup.getFastLogGroup();
                List<FastLog> logs = fastLogGroup.getLogs();
                for (int lIdx = 0; lIdx < logs.size(); lIdx++) {
                    FastLog log = logs.get(lIdx);
                    System.out.println("LogTime:" + log.getTime());
                    List<FastLogContent> contents = log.getContents();
                    for (FastLogContent content : contents) {
                        System.out.println(content.getKey() + ":" + content.getValue());
                    }
                    String logId = seqNoPrefix + lastCursor + "|" + lIdx;
                    System.out.println("LogId:" + logId);
                }
                lastCursor = encodeCursor(++offset);
            }
        } catch (LogException e) {
            e.printStackTrace();
        }
    }
}
