package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

public abstract class StructuredDataFormat extends DataFormat {
    private String timeField;
    private String timeFormat;

    public StructuredDataFormat(String type) {
        super(type);
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        timeField = JsonUtils.readOptionalString(jsonObject, "timeField");
        timeFormat = JsonUtils.readOptionalString(jsonObject, "timeFormat");
    }
}
