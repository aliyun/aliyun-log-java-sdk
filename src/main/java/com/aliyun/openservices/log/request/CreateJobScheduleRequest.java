package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.http.client.HttpMethod;


public class CreateJobScheduleRequest extends BasicRequest {

    private static final long serialVersionUID = 4645953330661712600L;

    private final JobSchedule jobSchedule;

    public CreateJobScheduleRequest(String project, JobSchedule schedule) {
        super(project);
        this.jobSchedule = schedule;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUri() {
        return Consts.JOB_SCHEDULE_URI;
    }

    @Override
    public Object getBody() {
        return jobSchedule;
    }
}
