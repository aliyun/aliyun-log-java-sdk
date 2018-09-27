package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Args;

public class EnableJobRequest extends Request {

    private String jobName;

    public EnableJobRequest(String project, String jobName) {
        super(project);
        Args.notNullOrEmpty(jobName, "Job name");
        this.jobName = jobName;
        SetParam(Consts.ACTION, Consts.ENABLE);
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
