/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

/**
 * The request used to list objects in a logstore.
 */
public class ListObjectsRequest extends Request {
    private static final long serialVersionUID = 1L;
    private String logStore;

    /**
     * Construct a list objects request.
     *
     * @param project  project name
     * @param logStore logstore name
     */
    public ListObjectsRequest(String project, String logStore) {
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

    /**
     * Set the nextToken parameter (next page start position).
     *
     * @param nextToken next token value
     */
    public void setNextToken(String nextToken) {
        if (nextToken != null && !nextToken.isEmpty()) {
            SetParam("nextToken", nextToken);
        }
    }

    /**
     * Get the nextToken parameter.
     *
     * @return next token value, or null if not set
     */
    public String getNextToken() {
        return GetParam("nextToken");
    }

    /**
     * Set the maxResults parameter (number of rows to return).
     *
     * @param maxResults maximum number of results
     */
    public void setMaxResults(Integer maxResults) {
        if (maxResults != null && maxResults > 0) {
            SetParam("maxResults", String.valueOf(maxResults));
        }
    }

    /**
     * Get the maxResults parameter.
     *
     * @return maximum number of results, or null if not set
     */
    public Integer getMaxResults() {
        String value = GetParam("maxResults");
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Set the prefix parameter (filter objects by prefix).
     *
     * @param prefix prefix to filter by
     */
    public void setPrefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            SetParam("prefix", prefix);
        }
    }

    /**
     * Get the prefix parameter.
     *
     * @return prefix value, or null if not set
     */
    public String getPrefix() {
        return GetParam("prefix");
    }
}

