package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;

public abstract class JobRequest extends Request {

    private String name;

    public JobRequest(String project) {
        super(project);
    }

    public JobRequest(String project, String name) {
        super(project);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract HttpMethod getMethod();

    public String getUri() {
        return Consts.JOB_URI + "/" + name;
    }

    public Object getBody() {
        return null;
    }
}
