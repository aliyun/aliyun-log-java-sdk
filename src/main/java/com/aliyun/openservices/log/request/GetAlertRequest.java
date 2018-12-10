package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.http.client.HttpMethod;

public class GetAlertRequest extends JobRequest {

    private static final long serialVersionUID = -6270563487558925198L;

    public GetAlertRequest(String project, String name) {
        super(project, name);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
