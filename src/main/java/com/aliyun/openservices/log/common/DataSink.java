package com.aliyun.openservices.log.common;


import net.sf.json.JSONObject;

import java.io.Serializable;

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

    public void deserialize(JSONObject jsonObject) {
        // No-op
    }
}
