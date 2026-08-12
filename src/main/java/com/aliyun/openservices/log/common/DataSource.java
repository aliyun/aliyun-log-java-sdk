package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.annotation.InternalApi;

public abstract class DataSource implements JsonSerializable, JsonDeserializable {

    protected DataSourceType type;

    public DataSource(DataSourceType type) {
        this.type = type;
    }

    public DataSourceType getType() {
        return type;
    }

    public void setType(DataSourceType type) {
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
