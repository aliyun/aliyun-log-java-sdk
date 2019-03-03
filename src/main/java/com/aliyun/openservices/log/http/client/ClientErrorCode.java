
package com.aliyun.openservices.log.http.client;

public interface ClientErrorCode {
    
    /**
     * Unknown error
     */
    String UNKNOWN = "Unknown";
    
    /**
     * 远程服务连接超时
     */
    String CONNECTION_TIMEOUT = "ConnectionTimeout";
    
    /**
     * 远程服务socket读写超时
     */
    String SOCKET_TIMEOUT = "SocketTimeout";
    
    /**
     * The response is unrecognizable.
     */
    String INVALID_RESPONSE = "InvalidResponse";
}
