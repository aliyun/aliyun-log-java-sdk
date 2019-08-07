package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class UpdateExportRequest extends JobRequest  {
    private static final long serialVersionUID = -4937524684760262192L;

    private final Export export;

    public UpdateExportRequest(String project, Export export) {
        super(project);
        this.export = export;
        setName(export.getName());
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public Object getBody() {
        return export;
    }
}
