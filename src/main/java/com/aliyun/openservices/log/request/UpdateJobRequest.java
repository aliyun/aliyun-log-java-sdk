package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class UpdateJobRequest extends JobRequest {

    private Job job;

    public UpdateJobRequest(String project, Job job) {
        super(project);
        Args.notNull(job, "Job");
        this.job = job;
        setName(job.getName());
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public Object getBody() {
        return job;
    }
}
