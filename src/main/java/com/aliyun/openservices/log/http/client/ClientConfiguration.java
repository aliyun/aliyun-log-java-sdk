/**
 * Copyright (C) Alibaba Cloud Computing
 * All rights reserved.
 * 
 * 版权所有 （C）阿里云计算有限公司
 */

package com.aliyun.openservices.log.http.client;

/**
 * 访问阿里云服务的客户端配置。
 * 
 * @author xiaoming.yin
 *
 */
public class ClientConfiguration {

    private int maxConnections = 50;
    private int socketTimeout = 50 * 1000;
    private int connectionTimeout = 50 * 1000;

    /**
     * 构造新实例。
     */
    public ClientConfiguration(){}

    /**
     * 返回允许打开的最大HTTP连接数。
     * @return 最大HTTP连接数。
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * 设置允许打开的最大HTTP连接数。
     * @param maxConnections
     *          最大HTTP连接数。
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 返回通过打开的连接传输数据的超时时间（单位：毫秒）。
     * 0表示无限等待（但不推荐使用）。
     * @return 通过打开的连接传输数据的超时时间（单位：毫秒）。
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * 设置通过打开的连接传输数据的超时时间（单位：毫秒）。
     * 0表示无限等待（但不推荐使用）。
     * @param socketTimeout
     *          通过打开的连接传输数据的超时时间（单位：毫秒）。
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    /**
     * 返回建立连接的超时时间（单位：毫秒）。
     * @return 建立连接的超时时间（单位：毫秒）。
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置建立连接的超时时间（单位：毫秒）。
     * @param connectionTimeout
     *          建立连接的超时时间（单位：毫秒）。
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
