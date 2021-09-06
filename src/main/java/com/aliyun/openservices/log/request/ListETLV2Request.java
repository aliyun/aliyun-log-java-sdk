package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.JobType;

import java.io.Serializable;


public class ListETLV2Request extends ListJobsRequest implements Serializable {

    private static final long serialVersionUID = 1109698724031406237L;

    public ListETLV2Request(String project) {
        super(project, JobType.ETL);
    }
}
