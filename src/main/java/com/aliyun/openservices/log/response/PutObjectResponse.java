/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import java.util.Map;

/**
 * The response of the put_object API from log.
 */
public class PutObjectResponse extends Response {
    private static final long serialVersionUID = 1L;

    /**
     * Construct the response with http headers
     *
     * @param headers http headers
     */
    public PutObjectResponse(Map<String, String> headers) {
        super(headers);
    }

    /**
     * Get the ETag of the uploaded object.
     *
     * @return ETag value, may be null if not set
     */
    public String getETag() {
        return GetHeader("etag");
    }
}

