package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

public class LineFormat extends DataFormat {

    private String timePattern;
    private String timeFormat;

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

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        timePattern = JsonUtils.readOptionalString(jsonObject, "timePattern");
        timeFormat = JsonUtils.readOptionalString(jsonObject, "timeFormat");
    }
}
