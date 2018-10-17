package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;

/**
 * Disable job request.
 */
public class DisableJobRequest extends JobRequest {

    public DisableJobRequest(String project, String jobName) {
        super(project, jobName);
        SetParam(Consts.ACTION, Consts.DISABLE);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }
}
