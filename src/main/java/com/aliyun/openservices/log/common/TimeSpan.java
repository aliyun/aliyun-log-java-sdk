package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class TimeSpan implements Serializable, JsonDeserializable {
    private int queryTimeType;
    private String start;
    private String end;
    private Integer startTime;
    private Integer endTime;

    public int getQueryTimeType() {
        return queryTimeType;
    }

    public void setQueryTimeType(int queryTimeType) {
        this.queryTimeType = queryTimeType;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    @InternalApi
    public void fromJsonObject(JSONObject timeSpan) {
        queryTimeType = timeSpan.getIntValue("queryTimeType");
        start = timeSpan.getString("start");
        end = timeSpan.getString("end");
        startTime = timeSpan.getIntValue("startTime");
        endTime = timeSpan.getIntValue("endTime");
    }
}
