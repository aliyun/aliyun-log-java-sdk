package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.internal.json.JsonCodec;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class DataSink implements Serializable, JsonSerializable, JsonDeserializable {

    private DataSinkType type;

    public DataSinkType getType() {
        return type;
    }

    protected void setType(DataSinkType type) {
        this.type = type;
    }

    public DataSink(DataSinkType type) {
        this.type = type;
    }

    @InternalApi
    public JSONObject toJsonObject() {
        return JsonCodec.toJsonObject(this);
    }

    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) {
        // No-op
    }
}
