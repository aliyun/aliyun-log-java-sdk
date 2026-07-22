package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class StructuredDataFormat extends DataFormat {
    private String timeField;

    public StructuredDataFormat(String type) {
        super(type);
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }

    @Override
    @InternalApi
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        timeField = JsonUtils.readOptionalString(jsonObject, "timeField");
    }
}
