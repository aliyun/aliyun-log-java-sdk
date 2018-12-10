package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.http.client.HttpMethod;

public class DeleteJobRequest extends JobRequest {

    private static final long serialVersionUID = 232724638727965575L;

    public DeleteJobRequest(String project, String jobName) {
        super(project, jobName);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
    }

}
