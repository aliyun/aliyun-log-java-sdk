package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class UpdateAlertRequest extends JobRequest {

    private static final long serialVersionUID = -4981510450288166817L;

    private Alert alert;

    public UpdateAlertRequest(String project, Alert alert) {
        super(project);
        setAlert(alert);
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        Args.notNull(alert, "alert");
        alert.validate();
        this.alert = alert;
        setName(alert.getName());
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
