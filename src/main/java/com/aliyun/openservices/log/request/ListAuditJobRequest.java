package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.JobType;

public class ListAuditJobRequest extends ListJobsRequest {

    private static final long serialVersionUID = -7084774518396260346L;

    public ListAuditJobRequest(String project) {
        super(project, JobType.AUDIT_JOB);
    }
}
