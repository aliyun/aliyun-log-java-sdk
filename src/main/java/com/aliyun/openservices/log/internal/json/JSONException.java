package com.aliyun.openservices.log.internal.json;

/**
 * Internal use only. Do not use this class in application code.
 */
public class JSONException extends RuntimeException {

    public JSONException(String message) {
        super(message);
    }

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }
}
