package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;

/**
 * Enable job request.
 */
public class EnableJobRequest extends JobRequest {

    public EnableJobRequest(String project, String jobName) {
        super(project, jobName);
        SetParam(Consts.ACTION, Consts.ENABLE);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }
}
