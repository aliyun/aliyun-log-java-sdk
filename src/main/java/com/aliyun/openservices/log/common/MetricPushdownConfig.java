package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

public class MetricPushdownConfig {

    @JSONField(name = "enable")
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
