package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.http.client.HttpMethod;

public class GetReportRequest extends JobRequest {
    
    private static final long serialVersionUID = 1080990115502510494L;

    public GetReportRequest(String project, String name) {
        super(project, name);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
