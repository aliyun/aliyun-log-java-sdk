/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ObjectSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The response of the list objects API from log service.
 */
public class ListObjectsResponse extends Response {
    private static final long serialVersionUID = 1L;

    private List<ObjectSummary> objects = new ArrayList<ObjectSummary>();
    private String nextToken;
    private Integer maxResults;
    private String prefix;
    private Boolean isTruncated;

    /**
     * Construct the response with http headers.
     *
     * @param headers http headers
     */
    public ListObjectsResponse(Map<String, String> headers) {
        super(headers);
    }

    /**
     * Get the list of objects.
     *
     * @return list of objects
     */
    public List<ObjectSummary> getObjects() {
        return objects;
    }

    /**
     * Set the list of objects.
     *
     * @param objects list of objects
     */
    public void setObjects(List<ObjectSummary> objects) {
        this.objects = objects != null ? new ArrayList<ObjectSummary>(objects) : new ArrayList<ObjectSummary>();
    }

    /**
     * Get the nextToken value (for pagination).
     *
     * @return next token value, or null if not set
     */
    public String getNextToken() {
        return nextToken;
    }

    /**
     * Set the nextToken value.
     *
     * @param nextToken next token value
     */
    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    /**
     * Get the maxResults value.
     *
     * @return maximum number of results, or null if not set
     */
    public Integer getMaxResults() {
        return maxResults;
    }

    /**
     * Set the maxResults value.
     *
     * @param maxResults maximum number of results
     */
    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * Get the prefix value.
     *
     * @return prefix value, or null if not set
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set the prefix value.
     *
     * @param prefix prefix value
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Check if the result is truncated (more results available).
     *
     * @return true if truncated, false otherwise
     */
    public Boolean getIsTruncated() {
        return isTruncated;
    }

    /**
     * Set the isTruncated flag.
     *
     * @param isTruncated true if truncated, false otherwise
     */
    public void setIsTruncated(Boolean isTruncated) {
        this.isTruncated = isTruncated;
    }
}

