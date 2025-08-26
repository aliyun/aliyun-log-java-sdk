package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.http.client.HttpMethod;

/**
 * Request for getting a delete log store logs task status
 */
public class GetDeleteLogStoreLogsTaskRequest extends BasicRequest {

    private static final long serialVersionUID = 4731685092846173965L;

    private String logstore;
    private String taskId;

    /**
     * Construct a get delete log store logs task request
     *
     * @param project  project name
     * @param logstore log store name
     * @param taskId   task ID
     */
    public GetDeleteLogStoreLogsTaskRequest(String project, String logstore, String taskId) {
        super(project);
        this.logstore = logstore;
        this.taskId = taskId;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getUri() {
        return "/logstores/" + logstore + "/deletelogtasks/" + taskId;
    }
}
