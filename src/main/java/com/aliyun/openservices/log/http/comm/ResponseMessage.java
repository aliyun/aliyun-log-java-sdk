package com.aliyun.openservices.log.http.comm;

import com.aliyun.openservices.log.common.Consts;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Represents the response of a http request.
 */
public class ResponseMessage extends HttpMessage {
    private String uri;
    private int statusCode;
    private static final int HTTP_SUCCESS_STATUS_CODE = 200;
    private byte[] body = null;

    public ResponseMessage() {
    }

    public String getUri() {
        return uri;
    }

    public void setUrl(String uri) {
        this.uri = uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccessful() {
        return statusCode / 100 == HTTP_SUCCESS_STATUS_CODE / 100;
    }

    public void SetBody(byte[] body) {
        this.body = body;
    }

    public byte[] GetRawBody() {
        return this.body;
    }

    public String GetStringBody() {
        try {
            return new String(this.body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * @return The request id returned in headers.
     */
    public String getRequestId() {
        final Map<String, String> headers = getHeaders();
        final String requestId = headers.get(Consts.CONST_X_SLS_REQUESTID);
        return requestId == null ? "" : requestId;
    }
}
