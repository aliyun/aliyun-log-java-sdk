package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.http.client.HttpMethod;

public class DeleteJobRunRequest extends JobRequest {

    private static final long serialVersionUID = 5653914929124984368L;
    private String jobRunId;

    public DeleteJobRunRequest(String project, String jobName, String jobRunId) {
        super(project, jobName);
        this.jobRunId = jobRunId;
    }

    public String getJobRunId() {
        return jobRunId;
    }

    public void setJobRunId(String jobRunId) {
        this.jobRunId = jobRunId;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
    }
}
