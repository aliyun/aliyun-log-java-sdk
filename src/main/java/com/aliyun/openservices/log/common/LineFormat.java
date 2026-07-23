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
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        timePattern = JsonUtils.readOptionalString(jsonObject, "timePattern");
    }
}
