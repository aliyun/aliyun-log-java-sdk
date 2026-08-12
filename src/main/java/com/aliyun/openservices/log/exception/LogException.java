/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.exception;

import java.util.Map;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

/**
 * The exception is thrown if error happen.
 *
 * @author sls_dev
 */
public class LogException extends Exception {

    private static final long serialVersionUID = -4441995860203577032L;

    private int httpCode = -1;

    private String errorCode;

    private String requestId;

    private String rawResponseError;

    private Map<String, Object> accessDeniedDetail;

    /**
     * Construct LogException
     *
     * @param code      error code
     * @param message   error message
     * @param requestId request id from sls server, if the error is happened in the
     *                  client, the request id is empty
     */
    public LogException(String code, String message, String requestId) {
        super(message);
        this.errorCode = code;
        this.requestId = requestId;
    }

    /**
     * Construct LogException
     *
     * @param code      error code
     * @param message   error message
     * @param cause     inner exception, which cause the error
     * @param requestId request id from sls server, if the error is happened in the
     *                  client, the request id is empty
     */
    public LogException(String code, String message, Throwable cause,
                        String requestId) {
        super(message, cause);
        this.errorCode = code;
        this.requestId = requestId;
    }

    /**
     * Construct LogException
     *
     * @param httpCode  http code, -1 the error is happened in the client
     * @param code      error code
     * @param message   error message
     * @param requestId request id from sls server, if the error is happened in the
     *                  client, the request id is empty
     */
    public LogException(int httpCode, String code, String message, String requestId) {
        super(message);
        this.httpCode = httpCode;
        this.errorCode = code;
        this.requestId = requestId;
    }
    /**
     * Construct LogException
     *
     * @param httpCode  http code, -1 the error is happened in the client
     * @param code      error code
     * @param message   error message
     * @param requestId request id from sls server, if the error is happened in the
     *                  client, the request id is empty
     * @param rawResponseError body from http response
     */
    public LogException(int httpCode, String code, String message, String requestId, String rawResponseError) {
        super(message);
        this.httpCode = httpCode;
        this.errorCode = code;
        this.requestId = requestId;
        this.rawResponseError = rawResponseError;
        if (rawResponseError == null || rawResponseError.trim().isEmpty()) {
            return;
        }
        JSONObject object = JSONObject.parseObject(rawResponseError);
        this.accessDeniedDetail = JsonCodec.toMap(object.getJSONObject(Consts.ACCESS_DENIED_DETAIL));
    }

    /**
     * Get the error code
     *
     * @return error code
     */
    public String GetErrorCode() {
        return this.errorCode;
    }

    /**
     * Get the error message
     *
     * @return error message
     */
    public String GetErrorMessage() {
        return super.getMessage();
    }

    /**
     * Get the request id
     *
     * @return request id, if the error is happened in the client, the request
     * id is empty
     */
    public String GetRequestId() {
        return this.requestId;
    }

    /**
     * Get the http response code
     *
     * @return http code, -1 the error is happened in the client
     */
    public int GetHttpCode() {
        return httpCode;
    }

    /**
     * Set the http response code
     *
     * @param httpCode http code, -1 the error is happened in the client
     * @deprecated Use setHttpCode(int httpCode) instead.
     */
    @Deprecated
    public void SetHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRawResponseError() {
        return rawResponseError;
    }

    public void setRawResponseError(String rawResponseError) {
        this.rawResponseError = rawResponseError;
    }

    public Map<String, Object> getAccessDeniedDetail() {
        return accessDeniedDetail;
    }

    public void setAccessDeniedDetail(Map<String, Object> accessDeniedDetail) {
        this.accessDeniedDetail = accessDeniedDetail;
    }

    /**
     * @return the access denied detail as a raw JSON string, or null if absent.
     */
    public String getAccessDeniedDetailRaw() {
        return accessDeniedDetail == null ? null : JsonUtils.serialize(accessDeniedDetail);
    }

    @Override
    public String toString() {
        String accessDeniedDetailStr = "";
        if (accessDeniedDetail != null) {
            accessDeniedDetailStr = ", accessDeniedDetail='" + getAccessDeniedDetailRaw() + '\'';
        }
        return "LogException{" +
                "httpCode=" + httpCode +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + GetErrorMessage() + '\'' +
                accessDeniedDetailStr +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
