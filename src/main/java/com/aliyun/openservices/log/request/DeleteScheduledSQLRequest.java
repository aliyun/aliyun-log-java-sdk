package com.aliyun.openservices.log.request;

public class DeleteScheduledSQLRequest extends DeleteJobRequest {
    private static final long serialVersionUID = 1160723344362479726L;
    public DeleteScheduledSQLRequest(String project, String jobName) {
        super(project, jobName);
    }
}