package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Logging;
import com.aliyun.openservices.log.util.Args;

public class UpdateLoggingRequest extends Request {

    private Logging logging;

    /**
     * Construct a new {@link UpdateLoggingRequest} instance.
     *
     * @param project project name
     * @param logging logging options
     */
    public UpdateLoggingRequest(String project, Logging logging) {
        super(project);
        setLogging(logging);
    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        Args.notNull(logging, "logging");
        this.logging = logging;
    }
}
