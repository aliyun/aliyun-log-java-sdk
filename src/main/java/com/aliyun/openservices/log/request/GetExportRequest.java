package com.aliyun.openservices.log.request;

public class GetExportRequest extends GetJobRequest {
    private static final long serialVersionUID = 3794722065886702855L;

    public GetExportRequest(String project, String jobName) {
        super(project, jobName);
    }
}
