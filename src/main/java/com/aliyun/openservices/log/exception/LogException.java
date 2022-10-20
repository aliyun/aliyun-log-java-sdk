/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.exception;

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

    @Override
    public String toString() {
        return "LogException{" +
                "httpCode=" + httpCode +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + GetErrorMessage() + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
