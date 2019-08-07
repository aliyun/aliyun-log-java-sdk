package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class UpdateIngestionRequest extends JobRequest {

    private static final long serialVersionUID = -11092421175733273L;

    private final Ingestion ingestion;

    public UpdateIngestionRequest(String project, Ingestion ingestion) {
        super(project);
        this.ingestion = ingestion;
        setName(ingestion.getName());
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
