package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.http.client.HttpMethod;

public abstract class BasicRequest extends Request {

    public BasicRequest(String project) {
        super(project);
    }

    public abstract HttpMethod getMethod();

    public abstract String getUri();

    public Object getBody() {
        return null;
    }
}
