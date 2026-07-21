package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

/**
 * @author cjh
 */
public interface ScheduledSQLParameters {
    /**
     * Deserialize parameters from JSON object.
     **/
    void deserialize(JSONObject value);
}
