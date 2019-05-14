package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class CreateAlertRequest extends JobRequest {

    private static final long serialVersionUID = 3346010323520068092L;

    private Alert alert;

    public CreateAlertRequest(String project, Alert alert) {
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
