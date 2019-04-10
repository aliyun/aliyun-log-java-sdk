package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;

/**
 * Disable job request.
 */
public class DisableJobRequest extends JobRequest {

    private static final long serialVersionUID = 9008431160488152118L;

    public DisableJobRequest(String project, String jobName) {
        super(project, jobName);
        SetParam(Consts.ACTION, Consts.DISABLE);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }
}
