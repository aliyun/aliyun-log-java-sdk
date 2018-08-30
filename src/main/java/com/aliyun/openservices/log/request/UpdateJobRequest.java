package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.util.Args;

public class UpdateJobRequest extends Request {

    private Job job;

    public UpdateJobRequest(String project, Job job) {
        super(project);
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
