package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JSONObject;

public abstract class JobConfiguration {

    /**
     * Deserialize instance from JSON object.
     **/
    public abstract void deserialize(JSONObject value);
}
