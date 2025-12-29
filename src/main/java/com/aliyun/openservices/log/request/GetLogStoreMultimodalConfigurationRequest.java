/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

/**
 * The request used to get logstore multimodal configuration.
 */
public class GetLogStoreMultimodalConfigurationRequest extends Request {
    private static final long serialVersionUID = 1L;

    private String logStore;

    /**
     * Construct a get logstore multimodal configuration request.
     *
     * @param project  project name
     * @param logStore logstore name
     */
    public GetLogStoreMultimodalConfigurationRequest(String project, String logStore) {
        super(project);
        this.logStore = logStore;
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
}

