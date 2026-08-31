/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Map;

/**
 * The response of the generate presigned url API from log service.
 */
public class GeneratePresignedUrlResponse extends Response {
    private static final long serialVersionUID = 1L;

    private String url;

    /**
     * Construct the response with http headers.
     *
     * @param headers http headers
     */
    public GeneratePresignedUrlResponse(Map<String, String> headers) {
        super(headers);
    }

    /**
     * Get the presigned url.
     *
     * @return presigned url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the presigned url.
     *
     * @param url presigned url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Read fields from a JSON object.
     *
     * @param asJson JSON object
     */
    @InternalApi
    public void fromJsonObject(JSONObject asJson) {
        url = asJson.getString("url");
    }
}
