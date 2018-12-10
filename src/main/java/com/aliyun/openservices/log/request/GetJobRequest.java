package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.http.client.HttpMethod;

public class GetJobRequest extends JobRequest {

    private static final long serialVersionUID = 9147787347508453643L;

    public GetJobRequest(String project, String jobName) {
        super(project, jobName);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
