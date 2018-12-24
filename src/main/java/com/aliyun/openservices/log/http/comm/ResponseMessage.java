package com.aliyun.openservices.log.http.comm;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

import java.io.UnsupportedEncodingException;

/**
 * Represents the response of a http request.
 */
public class ResponseMessage extends HttpMessage {
    private String uri;
    private String requestId;
    private int statusCode;
    private byte[] body = null;

    public ResponseMessage() {
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
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
        if (requestId == null) {
            requestId = Utils.getOrEmpty(getHeaders(), Consts.CONST_X_SLS_REQUESTID);
        }
        return requestId;
    }
}
