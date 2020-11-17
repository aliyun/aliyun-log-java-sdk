package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class TimeSpan implements Serializable {
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

    public void deserialize(JSONObject timeSpan) {
        queryTimeType = timeSpan.getIntValue("queryTimeType");
        start = timeSpan.getString("start");
        end = timeSpan.getString("end");
        startTime = timeSpan.getIntValue("startTime");
        endTime = timeSpan.getIntValue("endTime");
    }
}
