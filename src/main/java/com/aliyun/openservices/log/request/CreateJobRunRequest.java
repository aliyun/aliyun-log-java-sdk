package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.JobRun;
import com.aliyun.openservices.log.http.client.HttpMethod;

import java.io.Serializable;


public class CreateJobRunRequest extends JobRequest implements Serializable {

    private static final long serialVersionUID = 4645953330661712600L;

    private JobRun jobRun;

    public CreateJobRunRequest(String project, JobRun jobRun) {
        super(project);
        this.jobRun = jobRun;
    }

    public JobRun getJobRun() {
        return jobRun;
    }

    public void setJobRun(JobRun jobRun) {
        this.jobRun = jobRun;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUri() {
        return Consts.JOB_RUN_URI;
    }

    @Override
    public Object getBody() {
        return jobRun;
    }
}
