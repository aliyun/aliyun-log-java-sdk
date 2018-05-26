package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Logging;
import com.aliyun.openservices.log.util.Args;

import java.util.Map;

public class GetLoggingResponse extends Response {

    private Logging logging;

    /**
     * Construct a new {@link GetLoggingResponse} instance.
     *
     * @param headers The response headers.
     */
    public GetLoggingResponse(Map<String, String> headers, Logging logging) {
        super(headers);
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
