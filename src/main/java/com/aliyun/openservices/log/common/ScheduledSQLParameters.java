package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JSONObject;

/**
 * @author cjh
 */
public interface ScheduledSQLParameters {
    /**
     * Deserialize parameters from JSON object.
     **/
    @InternalApi
    void deserialize(JSONObject value);
}
