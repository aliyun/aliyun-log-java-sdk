package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class StartAuditJobRequest extends JobRequest {

    private static final long serialVersionUID = -1497695374277394324L;

    public StartAuditJobRequest(String project, String name) {
        super(project, name);
        SetParam(Consts.ACTION, Consts.START);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }
}
