package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ScheduledSQL;
import com.aliyun.openservices.log.http.client.HttpMethod;

public class UpdateScheduledSQLRequest extends JobRequest  {
    private static final long serialVersionUID = -4937524684760262192L;

    private final ScheduledSQL scheduledSQL;

    public UpdateScheduledSQLRequest(String project, ScheduledSQL scheduledSQL) {
        super(project);
        this.scheduledSQL = scheduledSQL;
        setName(scheduledSQL.getName());
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public Object getBody() {
        return scheduledSQL;
    }
}
