package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.JobType;

public class ListRebuildIndexRequest extends ListJobsRequest {

    private static final long serialVersionUID = 8998948994921812733L;

    public ListRebuildIndexRequest(String project) {
        super(project, JobType.REBUILD_INDEX);
    }
}
