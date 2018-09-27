package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.Utils;

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
            parameters.put(Consts.DISABLE_UNTIL, String.valueOf(Utils.getTimestamp(disableUntil)));
        }
        parameters.put(Consts.ACTION, Consts.DISABLE);
        return parameters;
    }
}
