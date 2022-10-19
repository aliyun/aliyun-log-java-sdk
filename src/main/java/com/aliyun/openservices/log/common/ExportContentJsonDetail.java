package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

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
    public void deserialize(JSONObject value) {
        enableTag = value.getBoolean("enableTag");
    }
}
