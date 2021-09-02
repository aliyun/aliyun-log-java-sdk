package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.JobType;
public class ListScheduledSQLRequest extends ListJobsRequest {
    private static final long serialVersionUID = 6298790485725371068L;
    public ListScheduledSQLRequest(String project) {
        super(project, JobType.SCHEDULED_SQL);
    }
}