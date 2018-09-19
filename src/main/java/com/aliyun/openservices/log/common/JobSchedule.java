package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;


/**
 * When and how often to repeat the job
 */
public class JobSchedule implements Serializable {

    public enum JobScheduleType {
        TimePeriod,
       // Hourly,
       // Daily
    }

    @JSONField
    private JobScheduleType type;

    @JSONField
    private Date doNotRunUntil;

    @JSONField
    private Long interval;

    public JobScheduleType getType() {
        return type;
    }

    public void setType(JobScheduleType type) {
        this.type = type;
    }

    public Date getDoNotRunUntil() {
        return doNotRunUntil;
    }

    public void setDoNotRunUntil(Date doNotRunUntil) {
        this.doNotRunUntil = doNotRunUntil;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSchedule that = (JobSchedule) o;

        if (getType() != that.getType()) return false;
        if (getDoNotRunUntil() != null ? !getDoNotRunUntil().equals(that.getDoNotRunUntil()) : that.getDoNotRunUntil() != null)
            return false;
        return getInterval() != null ? getInterval().equals(that.getInterval()) : that.getInterval() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getDoNotRunUntil() != null ? getDoNotRunUntil().hashCode() : 0);
        result = 31 * result + (getInterval() != null ? getInterval().hashCode() : 0);
        return result;
    }
}
