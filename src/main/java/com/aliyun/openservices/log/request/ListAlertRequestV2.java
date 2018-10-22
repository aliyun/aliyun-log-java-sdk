package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.JobType;


public class ListAlertRequestV2 extends ListJobsRequest {

    public ListAlertRequestV2(String project) {
        super(project, JobType.ALERT);
    }
}
