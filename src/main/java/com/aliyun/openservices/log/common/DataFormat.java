package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public abstract class DataFormat implements JsonDeserializable {

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

    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = new JSONObject();
        if (type != null) {
            value.put("type", type);
        }
        if (timeFormat != null) {
            value.put("timeFormat", timeFormat);
        }
        if (timeZone != null) {
            value.put("timeZone", timeZone);
        }
        return value;
    }

    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) {
        this.type = jsonObject.getString("type");
        this.timeFormat = JsonUtils.readOptionalString(jsonObject, "timeFormat");
        this.timeZone = JsonUtils.readOptionalString(jsonObject, "timeZone");
    }
}
