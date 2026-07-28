/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.util.Args;

/**
 * The request used to enable Store Modify for an existing logstore.
 * This is a one-way operation; the request body always contains
 * {@code enabled=true}.
 */
public class EnableLogStoreModifyRequest extends Request {
    private static final long serialVersionUID = 1L;

    private String logStore;

    /**
     * Construct an enable logstore modification request.
     *
     * @param project  project name
     * @param logStore logstore name
     */
    public EnableLogStoreModifyRequest(String project, String logStore) {
        super(project);
        Args.notNullOrEmpty(logStore, "logStore");
        this.logStore = logStore;
    }

    /**
     * Get the logstore name.
     *
     * @return logstore name
     */
    public String getLogStore() {
        return logStore;
    }

    /**
     * Set the logstore name.
     *
     * @param logStore logstore name
     */
    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }

    /**
     * Get the request body as a JSON string.
     *
     * @return the JSON request body
     */
    public String getRequestBody() {
        JSONObject body = new JSONObject();
        body.put("enabled", true);
        return body.toString();
    }
}
