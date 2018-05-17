package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Logging;

public class CreateLoggingRequest extends Request {

    private static final long serialVersionUID = -8733254344842470177L;
    private Logging logging;

    public CreateLoggingRequest(String project, Logging logging) {
        super(project);
        this.logging = logging;
    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }
}
