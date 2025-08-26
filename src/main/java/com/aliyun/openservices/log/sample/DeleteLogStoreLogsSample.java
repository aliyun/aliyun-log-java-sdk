package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.DeleteLogStoreLogsTask;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.DeleteLogStoreLogsRequest;
import com.aliyun.openservices.log.request.GetDeleteLogStoreLogsTaskRequest;
import com.aliyun.openservices.log.request.ListDeleteLogStoreLogsTasksRequest;
import com.aliyun.openservices.log.response.DeleteLogStoreLogsResponse;
import com.aliyun.openservices.log.response.GetDeleteLogStoreLogsTaskResponse;
import com.aliyun.openservices.log.response.ListDeleteLogStoreLogsTasksResponse;

import java.util.List;

/**
 * Sample code for Delete Log Store Logs APIs
 */
public class DeleteLogStoreLogsSample {

    private static final String ENDPOINT = "your-sls-endpoint";
    private static final String ACCESS_KEY_ID = "your-access-key-id";
    private static final String ACCESS_KEY_SECRET = "your-access-key-secret";
    private static final String PROJECT = "your-project";
    private static final String LOGSTORE = "your-logstore";

    public static void main(String[] args) {
        Client client = new Client(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        try {
            // Example 1: Delete logs from log store
            deleteLogStoreLogsSample(client);

            // Example 2: Get delete task status
            getDeleteTaskSample(client, "sample-task-id");

            // Example 3: List delete tasks
            listDeleteTasksSample(client);

        } catch (LogException e) {
            System.err.println("LogException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sample for deleting logs from log store
     */
    public static void deleteLogStoreLogsSample(Client client) throws LogException {
        System.out.println("=== Delete Log Store Logs Sample ===");

        // Set time range (unix timestamp)
        int from = (int) (System.currentTimeMillis() / 1000) - 86400; // 24 hours ago
        int to = (int) (System.currentTimeMillis() / 1000); // now
        String query = "*"; // delete all logs, you can specify more specific query

        DeleteLogStoreLogsRequest request = new DeleteLogStoreLogsRequest(PROJECT, LOGSTORE, from, to, query);
        DeleteLogStoreLogsResponse response = client.deleteLogStoreLogs(request);

        System.out.println("Delete task created successfully!");
        System.out.println("Task ID: " + response.getTaskId());
        System.out.println("Request ID: " + response.GetRequestId());
    }

    /**
     * Sample for getting delete task status
     */
    public static void getDeleteTaskSample(Client client, String taskId) throws LogException {
        System.out.println("=== Get Delete Task Status Sample ===");

        GetDeleteLogStoreLogsTaskRequest request = new GetDeleteLogStoreLogsTaskRequest(PROJECT, LOGSTORE, taskId);
        GetDeleteLogStoreLogsTaskResponse response = client.getDeleteLogStoreLogsTask(request);

        DeleteLogStoreLogsTask task = response.getTask();
        System.out.println("Task details:");
        System.out.println("  Task ID: " + task.getTaskId());
        System.out.println("  Progress: " + task.getProgress() + "%");
        System.out.println("  Query: " + task.getQuery());
        System.out.println("  From: " + task.getFrom());
        System.out.println("  To: " + task.getTo());
        System.out.println("  Error Code: " + task.getErrorCode());
        System.out.println("  Error Message: " + task.getErrorMessage());
    }

    /**
     * Sample for listing delete tasks
     */
    public static void listDeleteTasksSample(Client client) throws LogException {
        System.out.println("=== List Delete Tasks Sample ===");

        int offset = 0;
        int size = 10;
        ListDeleteLogStoreLogsTasksRequest request = new ListDeleteLogStoreLogsTasksRequest(PROJECT, LOGSTORE, offset, size);
        ListDeleteLogStoreLogsTasksResponse response = client.listDeleteLogStoreLogsTasks(request);

        System.out.println("Total tasks: " + response.getTotal());
        List<DeleteLogStoreLogsTask> tasks = response.getTasks();

        for (DeleteLogStoreLogsTask task : tasks) {
            System.out.println("Task: " + task.getTaskId() + 
                             ", Progress: " + task.getProgress() + "%" +
                             ", Query: " + task.getQuery());
        }
    }
}
