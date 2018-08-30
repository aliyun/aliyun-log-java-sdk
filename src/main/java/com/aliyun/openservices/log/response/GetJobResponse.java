package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.util.Args;

import java.util.Map;

public class GetJobResponse extends Response {

    private Job job;

    public GetJobResponse(Map<String, String> headers, Job job) {
        super(headers);
        Args.notNull(job, "Job");
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
