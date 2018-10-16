package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.AlertV2;
import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.common.JobType;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class UpdateAlertRequestV2 extends JobRequest {

    private AlertV2 alert;

    public UpdateAlertRequestV2(String project, AlertV2 alert) {
        super(project);
        Args.notNull(alert, "alert");
        this.alert = alert;
        setName(alert.getName());
    }

    public AlertV2 getAlert() {
        return alert;
    }

    public void setAlert(AlertV2 alert) {
        this.alert = alert;
    }

    public Job toJob() {
        Job job = new Job();
        job.setType(JobType.Alert);
        job.setName(alert.getName());
        job.setSchedule(alert.getSchedule());
        job.setConfiguration(alert.getConfiguration());
        return job;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public Object getBody() {
        Job job = new Job();
        job.setType(JobType.Alert);
        job.setName(alert.getName());
        job.setSchedule(alert.getSchedule());
        job.setConfiguration(alert.getConfiguration());
        return job;
    }
}
