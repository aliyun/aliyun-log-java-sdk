package com.aliyun.openservices.log.response;

import java.util.Map;

public class UpdateLogStoreInternalResponse extends Response {
    private static final long serialVersionUID = 6689859783789432780L;

    /**
     * Construct the base response body with http headers
     *
     * @param headers http headers
     */
    public UpdateLogStoreInternalResponse(Map<String, String> headers) {
        super(headers);
    }
}
