package com.aliyun.openservices.log.common;


import net.sf.json.JSONObject;

public abstract class JobArguments {


    /**
     * Deserialize arguments instance from JSON object.
     **/
    public abstract void deserialize(JSONObject value);
}
