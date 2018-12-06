package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class CreateJobRequest extends JobRequest {

    private Job job;

    public CreateJobRequest(String project, Job job) {
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

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUri() {
        return Consts.JOB_URI;
    }

    @Override
    public Object getBody() {
        return job;
    }
}
