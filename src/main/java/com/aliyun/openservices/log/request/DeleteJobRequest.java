package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.util.Args;

public class DeleteJobRequest extends Request {

    private String jobName;

    public DeleteJobRequest(String project, String jobName) {
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
