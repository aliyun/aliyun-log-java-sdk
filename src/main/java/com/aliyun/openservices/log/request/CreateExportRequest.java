package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class CreateExportRequest extends JobRequest {
    private static final long serialVersionUID = -1008414211953915043L;

    private final Export export;

    public CreateExportRequest(String project, Export export) {
        super(project);
        this.export = export;
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
        return export;
    }
}
