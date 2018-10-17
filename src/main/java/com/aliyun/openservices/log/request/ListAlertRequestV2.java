package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.JobType;

import java.util.Map;


public class ListAlertRequestV2 extends ListJobsRequest {

    private Boolean fired;

    public ListAlertRequestV2(String project) {
        super(project, JobType.ALERT);
    }

    public ListAlertRequestV2(String project, Boolean fired) {
        this(project);
        this.fired = fired;
    }

    public Boolean isFired() {
        return fired;
    }

    public void setFired(Boolean fired) {
        this.fired = fired;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (fired != null) {
            SetParam("fired", fired.toString());
        }
        return super.GetAllParams();
    }
}
