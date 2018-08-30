package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;


/**
 * When and how often to repeat the job
 */
public class JobSchedule implements Serializable {

    public enum ScheduleType {
        TimeWindow
    }

    @JSONField
    private ScheduleType type;

    @JSONField
    private Integer interval;

    // Optional
    @JSONField
    private Integer count;

    @JSONField
    private Date startTime;

    // Optional
    @JSONField
    private Date endTime;

    public ScheduleType getType() {
        return type;
    }

    public void setType(ScheduleType type) {
        this.type = type;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSchedule that = (JobSchedule) o;

        if (getType() != that.getType()) return false;
        if (getInterval() != null ? !getInterval().equals(that.getInterval()) : that.getInterval() != null)
            return false;
        if (getCount() != null ? !getCount().equals(that.getCount()) : that.getCount() != null) return false;
        if (getStartTime() != null ? !getStartTime().equals(that.getStartTime()) : that.getStartTime() != null)
            return false;
        return getEndTime() != null ? getEndTime().equals(that.getEndTime()) : that.getEndTime() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getInterval() != null ? getInterval().hashCode() : 0);
        result = 31 * result + (getCount() != null ? getCount().hashCode() : 0);
        result = 31 * result + (getStartTime() != null ? getStartTime().hashCode() : 0);
        result = 31 * result + (getEndTime() != null ? getEndTime().hashCode() : 0);
        return result;
    }
}
