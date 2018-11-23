package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.JobType;


public class ListAlertRequest extends ListJobsRequest {

    public ListAlertRequest(String project) {
        super(project, JobType.ALERT);
    }
}
