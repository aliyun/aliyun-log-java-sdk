/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

/**
 * The request used to get an object from logstore
 */
public class GetObjectRequest extends Request {
    private static final long serialVersionUID = 1L;
    private String logStore;
    private String objectName;

    /**
     * Construct a get object request
     *
     * @param project    project name
     * @param logStore   log store name
     * @param objectName object name
     */
    public GetObjectRequest(String project, String logStore, String objectName) {
        super(project);
        this.logStore = logStore;
        this.objectName = objectName;
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
}

