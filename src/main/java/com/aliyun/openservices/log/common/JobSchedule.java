package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;


/**
 * When and how often to repeat the job
 */
public class JobSchedule implements Serializable {

    public enum JobScheduleType {
        /**
         * Trigger in a fixed rate.
         */
        FixedRate
    }

    @JSONField
    private JobScheduleType type;

    @JSONField
    private Date doNotRunUntil;

    @JSONField
    private long interval;

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

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSchedule schedule = (JobSchedule) o;

        if (getInterval() != schedule.getInterval()) return false;
        if (getType() != schedule.getType()) return false;
        return getDoNotRunUntil() != null ? getDoNotRunUntil().equals(schedule.getDoNotRunUntil()) : schedule.getDoNotRunUntil() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getDoNotRunUntil() != null ? getDoNotRunUntil().hashCode() : 0);
        result = 31 * result + (int) (getInterval() ^ (getInterval() >>> 32));
        return result;
    }
}
