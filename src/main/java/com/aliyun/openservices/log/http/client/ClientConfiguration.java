/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.http.client;

/**
 * The client configuration for accessing Aliyun Log Service.
 */
public class ClientConfiguration {

    private int maxConnections = 50;
    private int socketTimeout = 50 * 1000;
    private int connectionTimeout = 50 * 1000;

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
}
