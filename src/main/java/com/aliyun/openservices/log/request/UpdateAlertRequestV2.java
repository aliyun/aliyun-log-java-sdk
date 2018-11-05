package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.AlertV2;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class UpdateAlertRequestV2 extends JobRequest {

    private AlertV2 alert;

    public UpdateAlertRequestV2(String project, AlertV2 alert) {
        super(project);
        Args.notNull(alert, "alert");
        this.alert = alert;
        alert.validate();
        setName(alert.getName());
    }

    public AlertV2 getAlert() {
        return alert;
    }

    public void setAlert(AlertV2 alert) {
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
