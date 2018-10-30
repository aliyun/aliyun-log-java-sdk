package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.Args;
import net.sf.json.JSONObject;

import java.io.Serializable;


/**
 * When and how often to repeat the job
 */
public class JobSchedule implements Serializable {

    @JSONField
    private JobScheduleType type;

    /**
     * Interval in duration format e,g "60s", "1h".
     */
    @JSONField
    private String interval;

    public JobScheduleType getType() {
        return type;
    }

    public void setType(JobScheduleType type) {
        this.type = type;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        Args.checkDuration(interval);
        this.interval = interval;
    }

    public void deserialize(JSONObject value) {
        type = JobScheduleType.fromString(value.getString("type"));
        interval = value.getString("interval");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSchedule schedule = (JobSchedule) o;

        if (getType() != schedule.getType()) return false;
        return getInterval() != null ? getInterval().equals(schedule.getInterval()) : schedule.getInterval() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getInterval() != null ? getInterval().hashCode() : 0);
        return result;
    }
}
