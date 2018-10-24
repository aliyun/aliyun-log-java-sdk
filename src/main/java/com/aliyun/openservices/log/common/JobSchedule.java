package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

import java.io.Serializable;


/**
 * When and how often to repeat the job
 */
public class JobSchedule implements Serializable {

    public enum JobScheduleType {
        /**
         * Trigger in a fixed rate.
         */
        FIXED_RATE("FixedRate");

        private final String value;

        JobScheduleType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static JobScheduleType fromString(String value) {
            for (JobScheduleType type : JobScheduleType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Illegal schedule type: " + value);
        }
    }

    @JSONField
    private JobScheduleType type;

    @JSONField
    private long interval;

    public JobScheduleType getType() {
        return type;
    }

    public void setType(JobScheduleType type) {
        this.type = type;
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
        return true;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (int) (getInterval() ^ (getInterval() >>> 32));
        return result;
    }

    public void deserialize(JSONObject value) {
        type = JobScheduleType.fromString(value.getString("type"));
        interval = value.getLong("interval");
    }
}
