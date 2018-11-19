package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.Args;
import net.sf.json.JSONObject;

import java.io.Serializable;


public class LoggingDetail implements Serializable {

    @JSONField
    private String type;

    @JSONField
    private String logstore;

    public LoggingDetail() {
    }

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

    public void deserialize(final JSONObject object) {
        Args.notNull(object, "object");
        type = object.getString("type");
        logstore = object.getString("logstore");
    }
}
