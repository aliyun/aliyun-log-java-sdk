package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.io.Serializable;

import static com.aliyun.openservices.log.util.Args.checkDuration;


/**
 * When and how often to repeat the job
 */
public class JobSchedule implements Serializable {

    private static final long serialVersionUID = 8400426178465652937L;

    @JSONField
    private JobScheduleType type;

    /**
     * Interval in duration format e,g "60s", "1h". Required for {@code JobScheduleType.FIXED_RATE} only.
     */
    @JSONField
    private String interval;

    /**
     * Cron expression for CRON type.
     */
    @JSONField
    private String cronExpression;

    /**
     * An optional delay to avoid missing data.
     */
    @JSONField
    private Integer delay;

    /**
     * sunday, monday, tuesday, wednesday, thursday, friday and saturday
     */
    @JSONField
    private Integer dayOfWeek;

    /**
     * hour at a day
     */
    @JSONField
    private Integer hour;


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
        checkDuration(interval);
        this.interval = interval;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public void deserialize(JSONObject value) {
        type = JobScheduleType.fromString(value.getString("type"));
        delay = JsonUtils.readOptionalInt(value, "delay");
        switch (type) {
            case CRON:
                cronExpression = value.getString("cronExpression");
                break;
            case FIXED_RATE:
                interval = value.getString("interval");
                break;
            case DAILY:
                hour = value.getInt("hour");
                break;
            case WEEKLY:
                dayOfWeek = value.getInt("dayOfWeek");
                hour = value.getInt("hour");
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSchedule schedule = (JobSchedule) o;

        if (getType() != schedule.getType()) return false;
        if (getInterval() != null ? !getInterval().equals(schedule.getInterval()) : schedule.getInterval() != null)
            return false;
        if (getCronExpression() != null ? !getCronExpression().equals(schedule.getCronExpression()) : schedule.getCronExpression() != null)
            return false;
        if (getDelay() != null ? !getDelay().equals(schedule.getDelay()) : schedule.getDelay() != null) return false;
        if (getDayOfWeek() != null ? !getDayOfWeek().equals(schedule.getDayOfWeek()) : schedule.getDayOfWeek() != null)
            return false;
        return getHour() != null ? getHour().equals(schedule.getHour()) : schedule.getHour() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getInterval() != null ? getInterval().hashCode() : 0);
        result = 31 * result + (getCronExpression() != null ? getCronExpression().hashCode() : 0);
        result = 31 * result + (getDelay() != null ? getDelay().hashCode() : 0);
        result = 31 * result + (getDayOfWeek() != null ? getDayOfWeek().hashCode() : 0);
        result = 31 * result + (getHour() != null ? getHour().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobSchedule{" +
                "type=" + type +
                ", interval='" + interval + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", delay=" + delay +
                ", dayOfWeek=" + dayOfWeek +
                ", hour=" + hour +
                '}';
    }
}
