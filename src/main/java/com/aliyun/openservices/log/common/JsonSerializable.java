package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.json.JSONObject;

/**
 * A model that can be converted to JSON.
 */
public interface JsonSerializable {

    @InternalApi
    JSONObject toJsonObject() throws LogException;

    default String toJsonString() throws LogException {
        return toJsonObject().toString();
    }
}
