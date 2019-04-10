package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;


public class StartJobScheduleRequest extends BasicRequest {

    private static final long serialVersionUID = -8079583732775408634L;

    private final String scheduleId;

    public StartJobScheduleRequest(String project, String scheduleId) {
        super(project);
        this.scheduleId = scheduleId;
        SetParam(Consts.ACTION, Consts.START);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public String getUri() {
        return Consts.JOB_SCHEDULE_URI + "/" + scheduleId;
    }
}
