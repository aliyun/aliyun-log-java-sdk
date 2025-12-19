package com.aliyun.openservices.log.response;

import java.util.Map;

/**
 * Response for deleting logs from a log store
 */
public class DeleteLogStoreLogsResponse extends Response {

    private static final long serialVersionUID = 5628394057281649372L;

    private String taskId;

    /**
     * Construct the delete log store logs response
     *
     * @param headers response headers
     * @param taskId  task ID
     */
    public DeleteLogStoreLogsResponse(Map<String, String> headers, String taskId) {
        super(headers);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
