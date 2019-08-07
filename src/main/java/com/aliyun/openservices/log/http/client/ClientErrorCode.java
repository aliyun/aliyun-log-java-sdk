
package com.aliyun.openservices.log.http.client;

public interface ClientErrorCode {
    /**
     * Unknown error. This means the error is not expected.
     */
    String UNKNOWN = "Unknown";

    /**
     * Unknown host. This error is returned when a
     * {@link java.net.UnknownHostException} is thrown.
     */
    String UNKNOWN_HOST = "UnknownHost";

    /**
     * connection times out.
     */
    String CONNECTION_TIMEOUT = "ConnectionTimeout";

    /**
     * Socket times out
     */
    String SOCKET_TIMEOUT = "SocketTimeout";

    /**
     * Socket exception
     */
    String SOCKET_EXCEPTION = "SocketException";

    /**
     * Connection is refused by server side.
     */
    String CONNECTION_REFUSED = "ConnectionRefused";

    /**
     * The input stream is not repeatable for reading.
     */
    String NONREPEATABLE_REQUEST = "NonRepeatableRequest";
}
