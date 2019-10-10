/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.http.client;

/**
 * The client configuration for accessing Aliyun Log Service.
 */
public class ClientConfiguration {
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 60 * 1000;
    public static final long DEFAULT_IDLE_CONNECTION_TIME = 60 * 1000;
    public static final int DEFAULT_VALIDATE_AFTER_INACTIVITY = 2 * 1000;
    public static final int DEFAULT_THREAD_POOL_WAIT_TIME = 60 * 1000;
    public static final int DEFAULT_REQUEST_TIMEOUT = 60 * 1000;

    private int maxConnections = 50;
    private int socketTimeout = 50 * 1000;
    private int connectionTimeout = 50 * 1000;
    protected int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;
    protected String proxyHost = null;
    protected int proxyPort = -1;
    protected String proxyUsername = null;
    protected String proxyPassword = null;
    protected String proxyDomain = null;
    protected String proxyWorkstation = null;

    private boolean requestTimeoutEnabled = false;
    protected long idleConnectionTime = DEFAULT_IDLE_CONNECTION_TIME;
    protected int requestTimeout = DEFAULT_REQUEST_TIMEOUT;


    public ClientConfiguration() {
    }

    /**
     * @return The maximum connection.
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * @param maxConnections The maximum connection.
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * @return The timeout for transferring data.
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Sets the timeout for transferring data. 0 for no timeout.
     *
     * @param socketTimeout The timeout for transferring data.
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    /**
     * @return The timeout for building connection.
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Set timeout for building connection.
     *
     * @param connectionTimeout Timeout for building connection.
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getProxyDomain() {
        return proxyDomain;
    }

    public String getProxyWorkstation() {
        return proxyWorkstation;
    }

    /**
     * Gets the connection's max idle time. If a connection has been idle for
     * more than this number, it would be closed.
     *
     * @return The connection's max idle time in millisecond.
     */
    public long getIdleConnectionTime() {
        return idleConnectionTime;
    }

    /**
     * Sets the connection's max idle time. If a connection has been idle for
     * more than this number, it would be closed.
     *
     * @param idleConnectionTime The connection's max idle time in millisecond.
     */
    public void setIdleConnectionTime(long idleConnectionTime) {
        this.idleConnectionTime = idleConnectionTime;
    }

    /**
     * The connection idle time threshold in millisecond that triggers the
     * validation. By default it's 2000.
     *
     * @return The connection idle time threshold.
     */
    public int getValidateAfterInactivity() {
        return DEFAULT_VALIDATE_AFTER_INACTIVITY;
    }

    /**
     * Gets the flag of enabling request timeout. By default it's disabled.
     *
     * @return true enabled; false disabled.
     */
    public boolean isRequestTimeoutEnabled() {
        return requestTimeoutEnabled;
    }

    /**
     * Sets the flag of enabling request timeout. By default it's disabled.
     *
     * @param requestTimeoutEnabled true to enable; false to disable.
     */
    public void setRequestTimeoutEnabled(boolean requestTimeoutEnabled) {
        this.requestTimeoutEnabled = requestTimeoutEnabled;
    }

    /**
     * Sets the timeout value in millisecond. By default it's 5 min.
     */
    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    /**
     * Gets the timeout value in millisecond.
     */
    public int getRequestTimeout() {
        return requestTimeout;
    }
}
