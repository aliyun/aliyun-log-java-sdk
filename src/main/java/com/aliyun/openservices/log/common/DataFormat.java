package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

public abstract class DataFormat {

    private String type;
    private String timeFormat;
    private String timeZone;

    public DataFormat(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void deserialize(JSONObject jsonObject) {
        this.type = jsonObject.getString("type");
        this.timeFormat = JsonUtils.readOptionalString(jsonObject, "timeFormat");
        this.timeZone = JsonUtils.readOptionalString(jsonObject, "timeZone");
    }
}
