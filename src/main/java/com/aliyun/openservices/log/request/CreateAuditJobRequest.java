package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.AuditJob;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class CreateAuditJobRequest extends JobRequest {

    private static final long serialVersionUID = -8067935948107016912L;

    private final AuditJob auditJob;

    public CreateAuditJobRequest(String project, AuditJob auditJob) {
        super(project);
        this.auditJob = auditJob;
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
        return auditJob;
    }
}
