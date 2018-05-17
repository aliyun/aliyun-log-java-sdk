package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Logging;

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
        this.logging = logging;
    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }
}
