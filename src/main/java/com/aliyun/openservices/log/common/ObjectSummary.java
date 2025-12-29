/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

import java.io.Serializable;

/**
 * Summary information about an object in the logstore.
 */
public class ObjectSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    private long size;
    private String lastModified;
    private String eTag;

    /**
     * Default constructor.
     */
    public ObjectSummary() {
    }

    /**
     * Construct an ObjectSummary with all fields.
     *
     * @param key          object key (name)
     * @param size         object size in bytes
     * @param lastModified last modified date as string
     * @param eTag         ETag value
     */
    public ObjectSummary(String key, long size, String lastModified, String eTag) {
        this.key = key;
        this.size = size;
        this.lastModified = lastModified;
        this.eTag = eTag;
    }

    /**
     * Get the object key (name).
     *
     * @return object key
     */
    public String getKey() {
        return key;
    }

    /**
     * Set the object key (name).
     *
     * @param key object key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Get the object size in bytes.
     *
     * @return object size
     */
    public long getSize() {
        return size;
    }

    /**
     * Set the object size in bytes.
     *
     * @param size object size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Get the last modified date as string.
     *
     * @return last modified date string
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * Set the last modified date as string.
     *
     * @param lastModified last modified date string
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Get the ETag value.
     *
     * @return ETag value
     */
    public String getETag() {
        return eTag;
    }

    /**
     * Set the ETag value.
     *
     * @param eTag ETag value
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}

