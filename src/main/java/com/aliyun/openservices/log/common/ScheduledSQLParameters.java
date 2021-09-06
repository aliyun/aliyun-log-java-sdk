package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

/**
 * @author cjh
 */
public interface ScheduledSQLParameters {
    /**
     * Deserialize parameters from JSON object.
     **/
    void deserialize(JSONObject value);
}
