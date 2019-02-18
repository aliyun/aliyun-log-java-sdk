package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.JobType;


public class ListReportRequest extends ListJobsRequest {

    private static final long serialVersionUID = 6468677368738903369L;

    public ListReportRequest(String project) {
        super(project, JobType.REPORT);
    }
}
