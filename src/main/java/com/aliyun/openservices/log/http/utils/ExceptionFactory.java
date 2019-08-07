package com.aliyun.openservices.log.http.utils;

import com.aliyun.openservices.log.http.client.ClientErrorCode;
import com.aliyun.openservices.log.http.client.ClientException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ExceptionFactory {
    public static ClientException createNetworkException(IOException ex) {
        String requestId = "Unknown";
        String errorCode = ClientErrorCode.UNKNOWN;

        if (ex instanceof SocketTimeoutException) {
            errorCode = ClientErrorCode.SOCKET_TIMEOUT;
        } else if (ex instanceof SocketException) {
            errorCode = ClientErrorCode.SOCKET_EXCEPTION;
        } else if (ex instanceof ConnectTimeoutException) {
            errorCode = ClientErrorCode.CONNECTION_TIMEOUT;
        } else if (ex instanceof UnknownHostException) {
            errorCode = ClientErrorCode.UNKNOWN_HOST;
        } else if (ex instanceof NoHttpResponseException) {
            errorCode = ClientErrorCode.CONNECTION_TIMEOUT;
        } else if (ex instanceof ClientProtocolException) {
            Throwable cause = ex.getCause();
            if (cause instanceof NonRepeatableRequestException) {
                errorCode = ClientErrorCode.NONREPEATABLE_REQUEST;
                return new ClientException(cause.getMessage(), errorCode, requestId, cause);
            }
        }

        return new ClientException(ex.getMessage(), errorCode, requestId, ex);
    }
}
