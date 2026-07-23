package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public abstract class JobConfiguration {

    /**
     * Deserialize instance from JSON object.
     **/
    @InternalApi
    public abstract void deserialize(JSONObject value);
}
