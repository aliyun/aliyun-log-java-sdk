/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.util.Args;

/**
 * The request used to generate a presigned url for an object in the logstore.
 */
public class GeneratePresignedUrlRequest extends Request {
    private static final long serialVersionUID = 1L;

    private String logStore;
    private String key;
    private String method;
    private Long expires;

    /**
     * Construct a generate presigned url request.
     *
     * @param project  project name
     * @param logStore logstore name
     * @param key      object key (name)
     * @param method   http method the presigned url is used for, e.g. GET, PUT
     */
    public GeneratePresignedUrlRequest(String project, String logStore, String key, String method) {
        this(project, logStore, key, method, null);
    }

    /**
     * Construct a generate presigned url request.
     *
     * @param project  project name
     * @param logStore logstore name
     * @param key      object key (name)
     * @param method   http method the presigned url is used for, e.g. GET, PUT
     * @param expires  expiration time of the presigned url in seconds; if null,
     *                 the server side default is used
     */
    public GeneratePresignedUrlRequest(String project, String logStore, String key, String method, Long expires) {
        super(project);
        Args.notNullOrEmpty(logStore, "logStore");
        Args.notNullOrEmpty(key, "key");
        Args.notNullOrEmpty(method, "method");
        this.logStore = logStore;
        this.key = key;
        this.method = method;
        this.expires = expires;
    }

    /**
     * Get logstore name.
     *
     * @return logstore name
     */
    public String getLogStore() {
        return logStore;
    }

    /**
     * Set logstore name.
     *
     * @param logStore logstore name
     */
    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }

    /**
     * Get object key.
     *
     * @return object key
     */
    public String getKey() {
        return key;
    }

    /**
     * Set object key.
     *
     * @param key object key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Get http method the presigned url is used for.
     *
     * @return http method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Set http method the presigned url is used for.
     *
     * @param method http method, e.g. GET, PUT
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Get the expiration time of the presigned url in seconds.
     *
     * @return expiration time in seconds
     */
    public Long getExpires() {
        return expires;
    }

    /**
     * Set the expiration time of the presigned url in seconds.
     *
     * @param expires expiration time in seconds
     */
    public void setExpires(Long expires) {
        this.expires = expires;
    }

    /**
     * Get request body as JSON string.
     *
     * @return JSON string representation of the request body
     */
    public String getRequestBody() {
        JSONObject body = new JSONObject();
        body.put("key", key);
        body.put("method", method);
        if (expires != null) {
            body.put("expires", expires);
        }
        return body.toString();
    }
}
