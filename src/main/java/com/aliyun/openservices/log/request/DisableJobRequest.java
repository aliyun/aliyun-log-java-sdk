package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.util.Args;

import java.util.Date;
import java.util.Map;

public class DisableJobRequest extends Request {

    private String jobName;
    // Disable for a while
    private Date disableUntil;

    public DisableJobRequest(String project, String jobName) {
        super(project);
        Args.notNullOrEmpty(jobName, "Job name");
        this.jobName = jobName;
        SetParam("action", "Disable");
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Date getDisableUntil() {
        return disableUntil;
    }

    public void setDisableUntil(Date disableUntil) {
        this.disableUntil = disableUntil;
    }

    @Override
    public Map<String, String> GetAllParams() {
        Map<String, String> parameters = super.GetAllParams();
        if (disableUntil != null) {
            parameters.put("disableUntil", String.valueOf(disableUntil.getTime()));
        }
        return parameters;
    }
}
