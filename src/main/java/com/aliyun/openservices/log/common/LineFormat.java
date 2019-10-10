package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

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
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        timePattern = JsonUtils.readOptionalString(jsonObject, "timePattern");
    }
}
