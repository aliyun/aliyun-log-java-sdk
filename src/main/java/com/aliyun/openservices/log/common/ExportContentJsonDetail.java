package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ExportContentJsonDetail extends ExportContentDetail {

    private boolean enableTag;

    public boolean isEnableTag() {
        return enableTag;
    }

    public void setEnableTag(boolean enableTag) {
        this.enableTag = enableTag;
    }

    public ExportContentJsonDetail() {}

    public ExportContentJsonDetail(boolean enableTag) {
        this.enableTag = enableTag;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        value.put("enableTag", enableTag);
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        enableTag = value.getBooleanValue("enableTag");
    }
}
