package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;


public class StopJobScheduleRequest extends BasicRequest {

    private static final long serialVersionUID = 6682564224765131553L;

    private final String scheduleId;

    public StopJobScheduleRequest(String project, String scheduleId) {
        super(project);
        this.scheduleId = scheduleId;
        SetParam(Consts.ACTION, Consts.STOP);
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
