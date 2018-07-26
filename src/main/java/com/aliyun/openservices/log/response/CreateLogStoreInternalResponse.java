package com.aliyun.openservices.log.response;

import java.util.Map;

public class CreateLogStoreInternalResponse extends Response {

    private static final long serialVersionUID = 6555283264551847171L;

    /**
     * Construct the base response body with http headers
     *
     * @param headers http headers
     */
    public CreateLogStoreInternalResponse(Map<String, String> headers) {
        super(headers);
    }
}
