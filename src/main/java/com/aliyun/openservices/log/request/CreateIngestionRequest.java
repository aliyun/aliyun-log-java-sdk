package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class CreateIngestionRequest extends JobRequest {

    private static final long serialVersionUID = 2446091027821298748L;

    private final Ingestion ingestion;

    public CreateIngestionRequest(String project, Ingestion ingestion) {
        super(project);
        this.ingestion = ingestion;
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
        return ingestion;
    }
}
