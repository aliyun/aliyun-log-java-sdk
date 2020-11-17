package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Date;

import static com.aliyun.openservices.log.util.Args.checkDuration;

public class JobSchedule implements Serializable {

    private static final long serialVersionUID = 8400426178465652937L;

    private String id;

    @JSONField
    private String displayName;

    @JSONField
    private String description;

    @JSONField
    private String jobName;

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
    private Integer fromTime;

    @JSONField
    private Integer toTime;

    private String status;

    private Date createTime;

    private Date lastModifiedTime;

    private Date startTime;

    private Date completeTime;

    private boolean runImmediately = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
    @Deprecated
    public Integer getFromTime() {
        return fromTime;
    }
    @Deprecated
    public void setFromTime(Integer fromTime) {
        this.fromTime = fromTime;
    }
    @Deprecated
    public Integer getToTime() {
        return toTime;
    }
    @Deprecated
    public void setToTime(Integer toTime) {
        this.toTime = toTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRunImmediately() {
        return runImmediately;
    }

    public void setRunImmediately(boolean runImmediately) {
        this.runImmediately = runImmediately;
    }

    public void deserialize(JSONObject value) {
        id = JsonUtils.readOptionalString(value, "id");
        displayName = JsonUtils.readOptionalString(value, "displayName");
        jobName = JsonUtils.readOptionalString(value, "jobName");
        type = JobScheduleType.fromString(value.getString("type"));
        delay = JsonUtils.readOptionalInt(value, "delay");
        fromTime = JsonUtils.readOptionalInt(value, "fromTime");
        toTime = JsonUtils.readOptionalInt(value, "toTime");
        switch (type) {
            case CRON:
                cronExpression = value.getString("cronExpression");
                break;
            case FIXED_RATE:
                interval = value.getString("interval");
                break;
            case DAILY:
                hour = value.getIntValue("hour");
                break;
            case WEEKLY:
                dayOfWeek = value.getIntValue("dayOfWeek");
                hour = value.getIntValue("hour");
                break;
        }
        status = JsonUtils.readOptionalString(value, "status");
        startTime = JsonUtils.readOptionalDate(value, "startTime");
        completeTime = JsonUtils.readOptionalDate(value, "completeTime");
        createTime = JsonUtils.readOptionalDate(value, "createTime");
        lastModifiedTime = JsonUtils.readOptionalDate(value, "lastModifiedTime");
        description = JsonUtils.readOptionalString(value, "description");
        runImmediately = JsonUtils.readBool(value, "runImmediately", false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSchedule schedule = (JobSchedule) o;

        if (getId() != null ? !getId().equals(schedule.getId()) : schedule.getId() != null) return false;
        if (getDisplayName() != null ? !getDisplayName().equals(schedule.getDisplayName()) : schedule.getDisplayName() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(schedule.getDescription()) : schedule.getDescription() != null)
            return false;
        if (getJobName() != null ? !getJobName().equals(schedule.getJobName()) : schedule.getJobName() != null)
            return false;
        if (getType() != schedule.getType()) return false;
        if (getInterval() != null ? !getInterval().equals(schedule.getInterval()) : schedule.getInterval() != null)
            return false;
        if (getCronExpression() != null ? !getCronExpression().equals(schedule.getCronExpression()) : schedule.getCronExpression() != null)
            return false;
        if (getDelay() != null ? !getDelay().equals(schedule.getDelay()) : schedule.getDelay() != null) return false;
        if (getDayOfWeek() != null ? !getDayOfWeek().equals(schedule.getDayOfWeek()) : schedule.getDayOfWeek() != null)
            return false;
        if (getHour() != null ? !getHour().equals(schedule.getHour()) : schedule.getHour() != null) return false;
        if (getFromTime() != null ? !getFromTime().equals(schedule.getFromTime()) : schedule.getFromTime() != null)
            return false;
        if (getToTime() != null ? !getToTime().equals(schedule.getToTime()) : schedule.getToTime() != null)
            return false;
        if (getStatus() != null ? !getStatus().equals(schedule.getStatus()) : schedule.getStatus() != null)
            return false;
        if (getCreateTime() != null ? !getCreateTime().equals(schedule.getCreateTime()) : schedule.getCreateTime() != null)
            return false;
        if (getLastModifiedTime() != null ? !getLastModifiedTime().equals(schedule.getLastModifiedTime()) : schedule.getLastModifiedTime() != null)
            return false;
        if (getStartTime() != null ? !getStartTime().equals(schedule.getStartTime()) : schedule.getStartTime() != null)
            return false;
        if (isRunImmediately() != schedule.isRunImmediately())
            return false;
        return getCompleteTime() != null ? getCompleteTime().equals(schedule.getCompleteTime()) : schedule.getCompleteTime() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getDisplayName() != null ? getDisplayName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getJobName() != null ? getJobName().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getInterval() != null ? getInterval().hashCode() : 0);
        result = 31 * result + (getCronExpression() != null ? getCronExpression().hashCode() : 0);
        result = 31 * result + (getDelay() != null ? getDelay().hashCode() : 0);
        result = 31 * result + (getDayOfWeek() != null ? getDayOfWeek().hashCode() : 0);
        result = 31 * result + (getHour() != null ? getHour().hashCode() : 0);
        result = 31 * result + (getFromTime() != null ? getFromTime().hashCode() : 0);
        result = 31 * result + (getToTime() != null ? getToTime().hashCode() : 0);
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (getCreateTime() != null ? getCreateTime().hashCode() : 0);
        result = 31 * result + (getLastModifiedTime() != null ? getLastModifiedTime().hashCode() : 0);
        result = 31 * result + (getStartTime() != null ? getStartTime().hashCode() : 0);
        result = 31 * result + (getCompleteTime() != null ? getCompleteTime().hashCode() : 0);
        result = 31 * result + (isRunImmediately() ? 1 : 0);
        return result;
    }
}
