package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.JSONObject;

public abstract class JobConfiguration {

    /**
     * Deserialize instance from JSON object.
     **/
    public abstract void deserialize(JSONObject value);
}
