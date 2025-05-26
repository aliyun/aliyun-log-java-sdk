package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BaseAsyncSqlResponse extends Response {
    private final static List<String> ACCEPTABLE_STATES = Arrays.asList("RUNNING", "FINISHED", "FAILED", "CANCELLED");

    private final String queryId;
    private final String state;
    private final String errorCode;
    private final String errorMessage;

    public BaseAsyncSqlResponse(Map<String, String> headers,
                                String queryId,
                                String state,
                                String errorCode,
                                String errorMessage) {
        super(headers);
        this.queryId = queryId;

        if (ACCEPTABLE_STATES.stream().noneMatch(s -> s.equalsIgnoreCase(state))) {
            this.state = "FAILED";
            this.errorCode = errorCode != null && !errorCode.isEmpty() ? errorCode : "InternalServerError";
            this.errorMessage = errorMessage != null && !errorMessage.isEmpty() ? errorMessage : "Internal Server Error";
        } else {
            this.state = state;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    public String getQueryId() {
        return queryId;
    }

    public String getState() {
        return state;
    }

    @JSONField(serialize = false)
    public boolean isRunning() {
        return "RUNNING".equalsIgnoreCase(state);
    }

    @JSONField(serialize = false)
    public boolean isSuccessful() {
        return "FINISHED".equalsIgnoreCase(state);
    }

    @JSONField(serialize = false)
    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(state);
    }

    @JSONField(serialize = false)
    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(state);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}