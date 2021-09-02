package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.ScheduledSQL;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class CreateScheduledSQLRequest extends JobRequest {
    private static final long serialVersionUID = -1008414222953915043L;
    private final ScheduledSQL scheduledSql;

    public CreateScheduledSQLRequest(String project, ScheduledSQL scheduledSql) {
        super(project);
        this.scheduledSql = scheduledSql;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUri() {
        return Consts.JOB_URI;
    }

    @Override
    public Object getBody() {
        return scheduledSql;
    }
}