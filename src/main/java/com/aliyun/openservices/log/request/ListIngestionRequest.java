package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.JobType;

public class ListIngestionRequest extends ListJobsRequest {

    private static final long serialVersionUID = -2311421285083476250L;

    public ListIngestionRequest(String project) {
        super(project, JobType.INGESTION);
    }
}
