package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;

/**
 * A model that can read its state from JSON.
 */
public interface JsonDeserializable {

    @InternalApi
    void fromJsonObject(JSONObject value) throws LogException;

    default void fromJsonString(String value) throws LogException {
        try {
            fromJsonObject(JSONObject.parseObject(value));
        } catch (JSONException e) {
            throw new LogException("FailToParseJson", e.getMessage(), e, "");
        }
    }
}
