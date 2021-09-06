package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.AuditJob;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class UpdateAuditJobRequest extends JobRequest  {

    private static final long serialVersionUID = -1401765527073991857L;

    private final AuditJob auditJob;

    public UpdateAuditJobRequest(String project, AuditJob auditJob) {
        super(project);
        this.auditJob = auditJob;
        setName(auditJob.getName());
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public Object getBody() {
        return auditJob;
    }
}
