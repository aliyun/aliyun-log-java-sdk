package com.aliyun.openservices.log.request;

public class GetIngestionRequest extends GetJobRequest {

    private static final long serialVersionUID = -7857479157195487854L;

    public GetIngestionRequest(String project, String jobName) {
        super(project, jobName);
    }

}
