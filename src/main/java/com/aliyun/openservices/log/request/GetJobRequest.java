package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.http.client.HttpMethod;

public class GetJobRequest extends JobRequest {

    public GetJobRequest(String project, String jobName) {
        super(project, jobName);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
