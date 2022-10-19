package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class RestartIngestionRequest extends JobRequest {

    private static final long serialVersionUID = -3202261940937644490L;
    private final Ingestion ingestion;

    public RestartIngestionRequest(String project, Ingestion ingestion) {
        super(project);
        this.ingestion = ingestion;
        setName(ingestion.getName());
        SetParam(Consts.ACTION, Consts.RESTART);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public Object getBody() {
        return ingestion;
    }
}
