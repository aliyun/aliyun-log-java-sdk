package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.DeleteLogStoreLogsTask;

import java.util.Map;

/**
 * Response for getting a delete log store logs task status
 */
public class GetDeleteLogStoreLogsTaskResponse extends Response {

    private static final long serialVersionUID = 7394851627048936285L;

    private DeleteLogStoreLogsTask task;

    /**
     * Construct the get delete log store logs task response
     *
     * @param headers response headers
     * @param task    delete log store logs task
     */
    public GetDeleteLogStoreLogsTaskResponse(Map<String, String> headers, DeleteLogStoreLogsTask task) {
        super(headers);
        this.task = task;
    }

    public DeleteLogStoreLogsTask getTask() {
        return task;
    }

    public void setTask(DeleteLogStoreLogsTask task) {
        this.task = task;
    }
}
