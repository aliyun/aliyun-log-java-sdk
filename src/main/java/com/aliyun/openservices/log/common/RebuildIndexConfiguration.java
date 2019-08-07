package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

public class RebuildIndexConfiguration extends JobConfiguration {

    @JSONField
    private String logstore;

    @JSONField
    private Integer startTime;

    @JSONField
    private Integer endTime;

    @Override
    public void deserialize(JSONObject value) {
        logstore = value.getString("logstore");
        startTime = JsonUtils.readOptionalInt(value, "startTime");
        endTime = JsonUtils.readOptionalInt(value, "endTime");
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "RebuildIndexConfiguration{" +
                "logstore='" + logstore + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
