package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.ProtectedApi;

public class DataSink implements Serializable {

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

    @ProtectedApi
    public void deserialize(JSONObject jsonObject) {
        // No-op
    }
}
