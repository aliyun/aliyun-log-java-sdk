package com.aliyun.openservices.log.request;

public class GetScheduledSQLRequest extends GetJobRequest {
    private static final long serialVersionUID = 3794722076586702855L;
    public GetScheduledSQLRequest(String project, String jobName) {
        super(project, jobName);
    }
}