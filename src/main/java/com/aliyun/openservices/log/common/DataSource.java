package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

public abstract class DataSource {

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

    public void deserialize(JSONObject jsonObject) {
        // No-op
    }
}
