package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import static com.aliyun.openservices.log.util.Args.checkDuration;


/**
 * When and how often to repeat the job
 */
public class JobSchedule implements Serializable {

    private static final long serialVersionUID = 8400426178465652937L;

    /**
     * The unique id of this schedule.
     */
    private String id;

    /**
     * The name of job to schedule.
     */
    @JSONField
    private String jobName;

    /**
     * The schedule type.
     */
    @JSONField
    private JobScheduleType type;

    // TODO Move schedule info to a separate class

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

    @JSONField
    private Map<String, String> parameters;

    private String status;

    private Date createTime;

    private Date lastModifiedTime;

    private Date startTime;

    private Date completeTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

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

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public void deserialize(JSONObject value) {
        id = value.getString("id");
        jobName = value.getString("jobName");
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
        status = value.getString("status");
        parameters = JsonUtils.readOptionalMap(value, "parameters");
        startTime = JsonUtils.readOptionalDate(value, "startTime");
        completeTime = JsonUtils.readOptionalDate(value, "completeTime");
        createTime = JsonUtils.readDate(value, "startTime");
        lastModifiedTime = JsonUtils.readDate(value, "lastModifiedTime");
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
