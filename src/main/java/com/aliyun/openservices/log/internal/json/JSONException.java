package com.aliyun.openservices.log.internal.json;

import com.aliyun.openservices.log.annotation.InternalApi;

/**
 * Internal use only. Do not use this class in application code.
 */
@InternalApi
public class JSONException extends RuntimeException {

    public JSONException(String message) {
        super(message);
    }

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }
}
