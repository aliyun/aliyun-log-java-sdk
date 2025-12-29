/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The request used to put an object to logstore
 */
public class PutObjectRequest extends Request {
    private static final long serialVersionUID = 1L;
    private String logStore;
    private String objectName;
    private InputStream content;
    private Long contentLength;
    private Map<String, String> headers;

    /**
     * Construct a put object request
     *
     * @param project    project name
     * @param logStore   log store name
     * @param objectName object name (only allow a-z A-Z 0-9 _ -)
     * @param content    object content as InputStream (can be null for empty content)
     * @param contentLength content length in bytes (required if content is not null)
     */
    public PutObjectRequest(String project, String logStore, String objectName, InputStream content, long contentLength) {
        super(project);
        this.logStore = logStore;
        this.objectName = objectName;
        this.content = content;
        this.contentLength = contentLength;
        this.headers = new HashMap<String, String>();
    }

    /**
     * Construct a put object request with headers
     *
     * @param project    project name
     * @param logStore   log store name
     * @param objectName object name (only allow a-z A-Z 0-9 _ -)
     * @param content    object content as InputStream (can be null for empty content)
     * @param contentLength content length in bytes (required if content is not null)
     * @param headers    optional headers to send with the request
     */
    public PutObjectRequest(String project, String logStore, String objectName, InputStream content, long contentLength, Map<String, String> headers) {
        super(project);
        this.logStore = logStore;
        this.objectName = objectName;
        this.content = content;
        this.contentLength = contentLength;
        this.headers = headers != null ? new HashMap<String, String>(headers) : new HashMap<String, String>();
    }

    /**
     * Get log store
     *
     * @return log store
     */
    public String getLogStore() {
        return logStore;
    }

    /**
     * Set log store
     *
     * @param logStore log store name
     */
    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }

    /**
     * Get object name
     *
     * @return object name
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Set object name
     *
     * @param objectName object name
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Get object content
     *
     * @return object content as InputStream
     */
    public InputStream getContent() {
        return content;
    }

    /**
     * Set object content
     *
     * @param content object content as InputStream
     */
    public void setContent(InputStream content) {
        this.content = content;
    }

    /**
     * Get content length
     *
     * @return content length in bytes
     */
    public Long getContentLength() {
        return contentLength;
    }

    /**
     * Set content length
     *
     * @param contentLength content length in bytes
     */
    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Get headers
     *
     * @return headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Set headers
     *
     * @param headers headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers != null ? new HashMap<String, String>(headers) : new HashMap<String, String>();
    }

    /**
     * Add a header
     *
     * @param key   header key
     * @param value header value
     */
    public void addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<String, String>();
        }
        this.headers.put(key, value);
    }
}

