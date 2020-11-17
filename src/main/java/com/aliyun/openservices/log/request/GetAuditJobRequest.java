package com.aliyun.openservices.log.request;

public class GetAuditJobRequest extends GetJobRequest {

    private static final long serialVersionUID = -6107246142570678183L;

    public GetAuditJobRequest(String project, String jobName) {
        super(project, jobName);
    }

}
