package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;

public abstract class ExecutionResult {


    public abstract void deserialize(JSONObject value);
}
