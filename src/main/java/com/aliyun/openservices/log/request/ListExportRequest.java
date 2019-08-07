package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.JobType;

public class ListExportRequest extends ListJobsRequest {

    private static final long serialVersionUID = 6299990485725371068L;

    public ListExportRequest(String project) {
        super(project, JobType.EXPORT);
    }
}
