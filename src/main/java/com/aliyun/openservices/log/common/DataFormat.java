package com.aliyun.openservices.log.common;


import net.sf.json.JSONObject;

public abstract class DataFormat {

    private String type;

    public DataFormat(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void deserialize(JSONObject jsonObject) {
        this.type = jsonObject.getString("type");
    }
}
