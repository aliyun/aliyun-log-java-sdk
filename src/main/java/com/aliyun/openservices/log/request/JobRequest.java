package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Args;

public abstract class JobRequest extends BasicRequest {

    private String name;

    public JobRequest(String project) {
        super(project);
    }

    public JobRequest(String project, String name) {
        super(project);
        Args.notNullOrEmpty(name, "name");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return Consts.JOB_URI + "/" + name;
    }
}
