package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;


public class GetJobScheduleRequest extends BasicRequest {

    private static final long serialVersionUID = 9147787347508453643L;

    private final String jobScheduleId;

    public GetJobScheduleRequest(String project, String jobScheduleId) {
        super(project);
        this.jobScheduleId = jobScheduleId;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getUri() {
        return Consts.JOB_SCHEDULE_URI + "/" + jobScheduleId;
    }
}
