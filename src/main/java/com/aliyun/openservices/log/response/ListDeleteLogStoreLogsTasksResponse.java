package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.DeleteLogStoreLogsTask;

import java.util.List;
import java.util.Map;

/**
 * Response for listing delete log store logs tasks
 */
public class ListDeleteLogStoreLogsTasksResponse extends Response {

    private static final long serialVersionUID = 9182756304829476153L;

    private int total;
    private List<DeleteLogStoreLogsTask> tasks;

    /**
     * Construct the list delete log store logs tasks response
     *
     * @param headers response headers
     * @param total   total number of tasks
     * @param tasks   list of delete log store logs tasks
     */
    public ListDeleteLogStoreLogsTasksResponse(Map<String, String> headers, int total, List<DeleteLogStoreLogsTask> tasks) {
        super(headers);
        this.total = total;
        this.tasks = tasks;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DeleteLogStoreLogsTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<DeleteLogStoreLogsTask> tasks) {
        this.tasks = tasks;
    }
}
