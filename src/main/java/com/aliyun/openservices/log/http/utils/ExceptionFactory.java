package com.aliyun.openservices.log.http.utils;

import com.aliyun.openservices.log.http.client.ClientErrorCode;
import com.aliyun.openservices.log.http.client.ClientException;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.HttpHostConnectException;

import javax.net.ssl.SSLException;
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
        } else if (ex instanceof HttpHostConnectException) {
            errorCode = ClientErrorCode.CONNECTION_REFUSED;
        } else if (ex instanceof SocketException) {
            errorCode = ClientErrorCode.SOCKET_EXCEPTION;
        } else if (ex instanceof ConnectTimeoutException) {
            errorCode = ClientErrorCode.CONNECTION_TIMEOUT;
        } else if (ex instanceof UnknownHostException) {
            errorCode = ClientErrorCode.UNKNOWN_HOST;
        } else if (ex instanceof NoHttpResponseException) {
            errorCode = ClientErrorCode.CONNECTION_TIMEOUT;
        } else if (ex instanceof SSLException) {
            errorCode = ClientErrorCode.SSL_EXCEPTION;
        } else if (ex instanceof ClientProtocolException) {
            errorCode = ClientErrorCode.CLIENT_PROTOCOL_EXCEPTION;
        }

        return new ClientException(ex.getMessage(), errorCode, requestId, ex);
    }
}
