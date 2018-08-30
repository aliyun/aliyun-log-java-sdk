package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.util.Args;

public class GetJobRequest extends Request {

    private String jobName;

    public GetJobRequest(String project, String jobName) {
        super(project);
        Args.notNullOrEmpty(jobName, "Job name");
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
