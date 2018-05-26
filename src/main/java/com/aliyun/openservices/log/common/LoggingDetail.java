package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.Args;
import net.sf.json.JSONObject;

import java.io.Serializable;


public class LoggingDetail implements Serializable {
    private String type;
    private String logstore;

    public LoggingDetail(String type, String logstore) {
        setLogstore(logstore);
        setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        Args.notNullOrEmpty(type, "type");
        this.type = type;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        Args.notNullOrEmpty(logstore, "logstore");
        this.logstore = logstore;
    }

    public JSONObject marshal() {
        JSONObject object = new JSONObject();
        object.put("type", type);
        object.put("logstore", logstore);
        return object;
    }

    public static LoggingDetail unmarshal(final JSONObject object) {
        Args.notNull(object, "object");
        final String type = object.getString("type");
        Args.notNullOrEmpty(type, "logstore");
        final String logstore = object.getString("logstore");
        Args.notNullOrEmpty(logstore, "logstore");
        return new LoggingDetail(type, logstore);
    }
}
