package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;

import java.io.Serializable;


public class LoggingDetail implements Serializable {
    private String type;
    private String logstore;

    public LoggingDetail(String type, String logstore) {
        this.type = type;
        this.logstore = logstore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public JSONObject marshal() {
        JSONObject object = new JSONObject();
        object.put("type", type);
        object.put("logstore", logstore);
        return object;
    }
}
