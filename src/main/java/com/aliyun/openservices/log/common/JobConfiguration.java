package com.aliyun.openservices.log.common;


import net.sf.json.JSONObject;

public abstract class JobConfiguration {

    /**
     * Deserialize instance from JSON object.
     **/
    public abstract void deserialize(JSONObject value);
}
