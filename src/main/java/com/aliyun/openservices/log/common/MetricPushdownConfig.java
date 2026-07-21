package com.aliyun.openservices.log.common;

import com.google.gson.annotations.SerializedName;

public class MetricPushdownConfig {

    @SerializedName("enable")
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
