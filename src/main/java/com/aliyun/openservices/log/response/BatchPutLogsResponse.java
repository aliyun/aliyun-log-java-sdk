package com.aliyun.openservices.log.response;

import java.util.Map;

/**
 * The response of the BatchPutLogs API from sls server
 *
 * @author sls_dev
 */
public class BatchPutLogsResponse extends Response {
    private static final long serialVersionUID = -3662644214038972169L;

    /**
     * Construct the response with http headers
     *
     * @param headers http headers
     */
    public BatchPutLogsResponse(Map<String, String> headers) {
        super(headers);
    }
}