/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.http.client;

import com.aliyun.openservices.log.http.comm.Protocol;
import com.aliyun.openservices.log.http.comm.RetryStrategy;
import com.aliyun.openservices.log.http.signer.SignVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * The client configuration for accessing Aliyun Log Service.
 */
public class ClientConfiguration {
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 60 * 1000;
    public static final long DEFAULT_IDLE_CONNECTION_TIME = 60 * 1000;
    public static final int DEFAULT_VALIDATE_AFTER_INACTIVITY = 2 * 1000;
    public static final int DEFAULT_THREAD_POOL_WAIT_TIME = 60 * 1000;
    public static final int DEFAULT_REQUEST_TIMEOUT = 60 * 1000;
    public static final boolean DEFAULT_USE_REAPER = false;
    public static final SignVersion DEFAULT_SIGNATURE_VERSION = SignVersion.V1;

    private int maxConnections = 50;
    private int socketTimeout = 50 * 1000;
    private int connectionTimeout = 50 * 1000;
    protected int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;
    protected Protocol protocol = Protocol.HTTP;

    protected String proxyHost = null;
    protected int proxyPort = -1;
    protected String proxyUsername = null;
    protected String proxyPassword = null;
    protected String proxyDomain = null;
    protected String proxyWorkstation = null;

    private boolean requestTimeoutEnabled = false;
    protected long idleConnectionTime = DEFAULT_IDLE_CONNECTION_TIME;
    protected int requestTimeout = DEFAULT_REQUEST_TIMEOUT;
    protected boolean useReaper = DEFAULT_USE_REAPER;
    private boolean connManagerShared = false;
    private RetryStrategy retryStrategy;
    private int maxErrorRetry = 3;
    protected SignVersion signatureVersion = DEFAULT_SIGNATURE_VERSION;
    private String region;
    private boolean retryDisabled = false;

    private Map<String, String> defaultHeaders = new HashMap<String, String>();

    public ClientConfiguration() {
    }

    public RetryStrategy getRetryStrategy() {
        return retryStrategy;
    }

    public void setRetryStrategy(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    public int getMaxErrorRetry() {
        return maxErrorRetry;
    }

    public void setMaxErrorRetry(int maxErrorRetry) {
        this.maxErrorRetry = maxErrorRetry;
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

    /**
     * Gets the SLS's protocol (HTTP or HTTPS).
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Sets the SLS's protocol (HTTP or HTTPS).
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
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


    /**
     * Gets the flag of using {@link com.aliyun.openservices.log.http.comm.IdleConnectionReaper} to manage expired
     * connection.
     */
    public boolean isUseReaper() {
        return useReaper;
    }

    /**
     * Sets the flag of using {@link com.aliyun.openservices.log.http.comm.IdleConnectionReaper} to manage expired
     * connection.
     */
    public void setUseReaper(boolean useReaper) {
        this.useReaper = useReaper;
    }

    public boolean isConnManagerShared() {
        return connManagerShared;
    }

    public void setConnManagerShared(boolean connManagerShared) {
        this.connManagerShared = connManagerShared;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public void setProxyDomain(String proxyDomain) {
        this.proxyDomain = proxyDomain;
    }

    public void setProxyWorkstation(String proxyWorkstation) {
        this.proxyWorkstation = proxyWorkstation;
    }

    /**
     * Gets signature version
     *
     * @return signature version
     */
    public SignVersion getSignatureVersion() {
        return signatureVersion;
    }

    /**
     * Sets signature version for all request.
     *
     * @param signatureVersion
     */
    public void setSignatureVersion(SignVersion signatureVersion) {
        this.signatureVersion = signatureVersion;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    /**
     * Gets the default http headers. All these headers would be automatically
     * added in every request. And if a header is also specified in the request,
     * the default one will be overwritten.
     *
     * @return The default http headers.
     */
    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    /**
     * Sets the default http headers. All these headers would be automatically
     * added in every request. And if a header is also specified in the request,
     * the default one will be overwritten.
     *
     * @param defaultHeaders Default http headers.
     */
    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    /**
     * Add a default header into the default header list.
     *
     * @param key   The default header name.
     * @param value The default header value.
     */
    public void addDefaultHeader(String key, String value) {
        this.defaultHeaders.put(key, value);
    }

    /**
     * Returns true if retry is disabled. If retryStrategy is set, will not disable retry when network exception occurs.
     */
    public boolean getRetryDisabled() {
        return retryDisabled;
    }

    /**
     * Set disable retry for client. If retryStrategy is set, will not disable retry when network exception occurs.
     *
     * @param retryDisabled no retry will be performed if retryDisabled is true.
     */
    public void setRetryDisabled(boolean retryDisabled) {
        this.retryDisabled = retryDisabled;
    }

}
