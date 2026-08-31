/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.common.MultimodalStatus;
import com.aliyun.openservices.log.common.AnonymousWriteStatus;
import com.aliyun.openservices.log.util.Args;

/**
 * The request used to put logstore multimodal configuration.
 */
public class PutLogStoreMultimodalConfigurationRequest extends Request {
    private static final long serialVersionUID = 1L;

    private String logStore;
    private MultimodalStatus status;
    private AnonymousWriteStatus anonymousWrite;
    private String ossBucket;
    private String roleArn;

    /**
     * Construct a put logstore multimodal configuration request.
     *
     * @param project  project name
     * @param logStore logstore name
     * @param status   multimodal status: Enabled or Disabled
     */
    public PutLogStoreMultimodalConfigurationRequest(String project, String logStore, MultimodalStatus status) {
        super(project);
        Args.notNullOrEmpty(logStore, "logStore");
        Args.notNull(status, "status");
        this.logStore = logStore;
        this.status = status;
        this.anonymousWrite = null;
    }

    /**
     * Construct a put logstore multimodal configuration request with anonymous write status.
     *
     * @param project       project name
     * @param logStore      logstore name
     * @param status        multimodal status: Enabled or Disabled
     * @param anonymousWrite anonymous write status: Enabled or Disabled (optional)
     */
    public PutLogStoreMultimodalConfigurationRequest(String project, String logStore, MultimodalStatus status, AnonymousWriteStatus anonymousWrite) {
        super(project);
        Args.notNullOrEmpty(logStore, "logStore");
        Args.notNull(status, "status");
        this.logStore = logStore;
        this.status = status;
        this.anonymousWrite = anonymousWrite;
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
     * Get OSS bucket name.
     *
     * @return OSS bucket name
     */
    public String getOssBucket() {
        return ossBucket;
    }

    /**
     * Set OSS bucket name.
     *
     * @param ossBucket OSS bucket name
     */
    public void setOssBucket(String ossBucket) {
        this.ossBucket = ossBucket;
    }

    /**
     * Get role ARN.
     *
     * @return role ARN
     */
    public String getRoleArn() {
        return roleArn;
    }

    /**
     * Set role ARN.
     *
     * @param roleArn role ARN
     */
    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    /**
     * Get request body as JSON string.
     *
     * @return JSON string representation of the request body
     */
    public String getRequestBody() {
        JSONObject body = new JSONObject();
        body.put("status", status != null ? status.getValue() : null);
        if (anonymousWrite != null) {
            body.put("anonymousWrite", anonymousWrite.getValue());
        }
        if (ossBucket != null) {
            body.put("ossBucket", ossBucket);
        }
        if (roleArn != null) {
            body.put("roleArn", roleArn);
        }
        return body.toString();
    }
}

