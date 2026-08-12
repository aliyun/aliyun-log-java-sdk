package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JSONObject;

/**
 * @author cjh
 */
public interface ScheduledSQLParameters extends JsonDeserializable {
    @Override
    @InternalApi
    void fromJsonObject(JSONObject value);

    @InternalApi
    JSONObject toJsonObject();
}
