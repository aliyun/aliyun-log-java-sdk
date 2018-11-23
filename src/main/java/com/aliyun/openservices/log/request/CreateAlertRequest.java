package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class CreateAlertRequest extends JobRequest {

    private Alert alert;

    public CreateAlertRequest(String project, Alert alert) {
        super(project);
        Args.notNull(alert, "alert");
        this.alert = alert;
        alert.validate();
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
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
