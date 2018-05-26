package com.aliyun.openservices.log.request;

public class GetLoggingRequest extends Request {

    /**
     * Construct a new {@link GetLoggingRequest} instance.
     *
     * @param project project name
     */
    public GetLoggingRequest(String project) {
        super(project);
    }
}
