package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.http.client.HttpMethod;

public class GetAlertRequestV2 extends JobRequest {

    public GetAlertRequestV2(String project, String name) {
        super(project, name);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
