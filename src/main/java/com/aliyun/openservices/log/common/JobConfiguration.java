package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.annotation.InternalApi;

public abstract class JobConfiguration implements JsonSerializable, JsonDeserializable {

    @InternalApi
    public JSONObject toJsonObject() {
        return JsonCodec.toJsonObject(this);
    }

    /**
     * Read fields from a JSON object.
     **/
    @InternalApi
    public abstract void fromJsonObject(JSONObject value);
}
