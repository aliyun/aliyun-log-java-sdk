package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.AlertV2;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class CreateAlertRequestV2 extends JobRequest {

    private AlertV2 alert;

    public CreateAlertRequestV2(String project, AlertV2 alert) {
        super(project);
        Args.notNull(alert, "alert");
        this.alert = alert;
        alert.validate();
    }

    public AlertV2 getAlert() {
        return alert;
    }

    public void setAlert(AlertV2 alert) {
        this.alert = alert;
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
        return alert.makeJob();
    }
}
