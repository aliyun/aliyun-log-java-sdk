package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import com.alibaba.fastjson.JSONObject;

public class RebuildIndexConfiguration extends JobConfiguration {

    @JSONField
    private String logstore;

    @JSONField
    private Integer fromTime;

    @JSONField
    private Integer toTime;

    @Override
    public void deserialize(JSONObject value) {
        logstore = value.getString("logstore");
        fromTime = JsonUtils.readOptionalInt(value, "fromTime");
        toTime = JsonUtils.readOptionalInt(value, "toTime");
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public Integer getFromTime() {
        return fromTime;
    }

    public void setFromTime(Integer fromTime) {
        this.fromTime = fromTime;
    }

    public Integer getToTime() {
        return toTime;
    }

    public void setToTime(Integer toTime) {
        this.toTime = toTime;
    }

    @Override
    public String toString() {
        return "RebuildIndexConfiguration{" +
                "logstore='" + logstore + '\'' +
                ", fromTime=" + fromTime +
                ", toTime=" + toTime +
                '}';
    }
}
