package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class LineFormat extends DataFormat {

    private String timePattern;

    public LineFormat() {
        super("Line");
    }

    protected LineFormat(String type) {
        super(type);
    }

    public String getTimePattern() {
        return timePattern;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (timePattern != null) {
            value.put("timePattern", timePattern);
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) {
        super.fromJsonObject(jsonObject);
        timePattern = JsonUtils.readOptionalString(jsonObject, "timePattern");
    }
}
