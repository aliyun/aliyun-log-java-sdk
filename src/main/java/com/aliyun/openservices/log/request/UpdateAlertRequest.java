package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class UpdateAlertRequest extends JobRequest {

    private Alert alert;

    public UpdateAlertRequest(String project, Alert alert) {
        super(project);
        Args.notNull(alert, "alert");
        this.alert = alert;
        alert.validate();
        setName(alert.getName());
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public Object getBody() {
        return alert.makeJob();
    }
}
