package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.JobType;


public class ListAlertHistoryRequest extends ListJobHistoryRequest {

    public ListAlertHistoryRequest(String project) {
        super(project);
        setType(JobType.Alert);
    }
}
