/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.MultimodalStatus;
import com.aliyun.openservices.log.common.AnonymousWriteStatus;

import java.util.Map;

/**
 * The response of the get logstore multimodal configuration API from log service.
 */
public class GetLogStoreMultimodalConfigurationResponse extends Response {
    private static final long serialVersionUID = 1L;

    private MultimodalStatus status;
    private AnonymousWriteStatus anonymousWrite;

    /**
     * Construct the response with http headers.
     *
     * @param headers http headers
     */
    public GetLogStoreMultimodalConfigurationResponse(Map<String, String> headers) {
        super(headers);
    }

    /**
     * Get multimodal status.
     *
     * @return multimodal status: Enabled or Disabled
     */
    public MultimodalStatus getStatus() {
        return status;
    }

    /**
     * Set multimodal status.
     *
     * @param status multimodal status: Enabled or Disabled
     */
    public void setStatus(MultimodalStatus status) {
        this.status = status;
    }

    /**
     * Get anonymous write status.
     *
     * @return anonymous write status: Enabled or Disabled
     */
    public AnonymousWriteStatus getAnonymousWrite() {
        return anonymousWrite;
    }

    /**
     * Set anonymous write status.
     *
     * @param anonymousWrite anonymous write status: Enabled or Disabled
     */
    public void setAnonymousWrite(AnonymousWriteStatus anonymousWrite) {
        this.anonymousWrite = anonymousWrite;
    }

    /**
     * Deserialize from JSON object.
     *
     * @param asJson JSON object
     */
    public void deserializeFrom(JSONObject asJson) {
        String statusStr = asJson.getString("status");
        status = statusStr != null ? MultimodalStatus.fromValue(statusStr) : null;
        String anonymousWriteStr = asJson.getString("anonymousWrite");
        anonymousWrite = anonymousWriteStr != null ? AnonymousWriteStatus.fromValue(anonymousWriteStr) : null;
    }
}

