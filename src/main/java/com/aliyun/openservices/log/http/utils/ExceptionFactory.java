package com.aliyun.openservices.log.http.utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.apache.http.conn.ConnectTimeoutException;
import com.aliyun.openservices.log.http.client.ClientErrorCode;
import com.aliyun.openservices.log.http.client.ClientException;

public class ExceptionFactory {
    public static ClientException createNetworkException(IOException ex) {
        String errorCode = ClientErrorCode.UNKNOWN;
        
        if (ex instanceof SocketTimeoutException) {
            errorCode = ClientErrorCode.SOCKET_TIMEOUT;
        } else if (ex instanceof ConnectTimeoutException) {
            errorCode = ClientErrorCode.CONNECTION_TIMEOUT;
        }
        
        return new ClientException(errorCode, ex.getMessage(), ex);
    }
}
