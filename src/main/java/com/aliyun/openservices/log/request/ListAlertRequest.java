package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.JobType;


public class ListAlertRequest extends ListJobsRequest {

    private static final long serialVersionUID = 2857657915141931959L;

    public ListAlertRequest(String project) {
        super(project, JobType.ALERT);
    }
}
