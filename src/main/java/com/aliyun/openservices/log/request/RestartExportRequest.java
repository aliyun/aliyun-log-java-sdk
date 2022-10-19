package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class RestartExportRequest extends JobRequest {

    private static final long serialVersionUID = -3202261940937644490L;
    private final Export export;

    public RestartExportRequest(String project, Export export) {
        super(project);
        this.export = export;
        setName(export.getName());
        SetParam(Consts.ACTION, Consts.RESTART);
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
