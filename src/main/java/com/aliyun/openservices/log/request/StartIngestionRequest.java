package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class StartIngestionRequest extends JobRequest {

    private static final long serialVersionUID = -8079583732775408634L;

    public StartIngestionRequest(String project, String name) {
        super(project, name);
        SetParam(Consts.ACTION, Consts.START);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }
}
