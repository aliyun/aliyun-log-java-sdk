package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ExportContentDetail implements Serializable, JsonDeserializable {

    public ExportContentDetail() {}

    @InternalApi
    public JSONObject toJsonObject() {
        return new JSONObject();
    }

    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) {
    }
}
