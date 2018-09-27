package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;


public class Job implements Serializable {

    /**
     * The name of job
     */
    @JSONField
    private String jobName;

    /**
     * The type of job. See {@link JobType}
     */
    @JSONField
    private JobType type;

    /**
     * When and how often to repeat the job.
     */
    @JSONField
    private JobSchedule schedule;

    /**
     * The state of job. See {@link JobState}
     */
    @JSONField
    private JobState state;

    /**
     * The arguments of job.
     */
    @JSONField
    private JobArguments arguments;

    // ---------- optional properties ----------------

    @JSONField
    private String description;

    @JSONField
    private LogSetting logSetting;

    @JSONField
    private long timeout;

    @JSONField
    private RetryPolicy retryPolicy;


    // ---------- read only properties ----------------

    @JSONField
    private JobStatus status;

    @JSONField
    private Date createTime;

    @JSONField
    private Date lastModifiedTime;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public JobSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(JobSchedule schedule) {
        this.schedule = schedule;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public JobArguments getArguments() {
        return arguments;
    }

    public void setArguments(JobArguments arguments) {
        this.arguments = arguments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LogSetting getLogSetting() {
        return logSetting;
    }

    public void setLogSetting(LogSetting logSetting) {
        this.logSetting = logSetting;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
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

    public enum JobState {
        Enabled,
        Disabled;

        public static JobState fromString(String value) {
            for (JobState state : JobState.values()) {
                if (state.name().equals(value)) {
                    return state;
                }
            }
            throw new IllegalArgumentException("Unknown job state: " + value);
        }
    }

    private static JobArguments createArgumentsFromType(JobType type) {
        switch (type) {
            case Alert:
                return new AlertArguments();
            default:
                throw new IllegalArgumentException("Unimplemented job type: " + type);
        }
    }

    public void deserialize(JSONObject value) {
        jobName = value.getString("jobName");
        type = JobType.fromString(value.getString("type"));
        state = JobState.fromString(value.getString("state"));
        timeout = value.getLong("timeout");
        description = value.getString("description");
        createTime = new Date(value.getLong("createTime"));
        lastModifiedTime = new Date(value.getLong("lastModifiedTime"));
        schedule = JsonUtils.deserialize(value.getString("schedule"), JobSchedule.class);
        if (value.containsKey("logSetting")) {
            logSetting = JsonUtils.deserialize(value.getString("logSetting"), LogSetting.class);
        }
        if (value.containsKey("retryPolicy")) {
            retryPolicy = JsonUtils.deserialize(value.getString("retryPolicy"), RetryPolicy.class);
        }
        arguments = createArgumentsFromType(type);
        arguments.deserialize(value.getJSONObject("arguments"));
        if (value.containsKey("status")) {
            status = JsonUtils.deserialize(value.getString("status"), JobStatus.class);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        if (getTimeout() != job.getTimeout()) return false;
        if (getJobName() != null ? !getJobName().equals(job.getJobName()) : job.getJobName() != null) return false;
        if (getType() != job.getType()) return false;
        if (getSchedule() != null ? !getSchedule().equals(job.getSchedule()) : job.getSchedule() != null) return false;
        if (getState() != job.getState()) return false;
        if (getArguments() != null ? !getArguments().equals(job.getArguments()) : job.getArguments() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(job.getDescription()) : job.getDescription() != null)
            return false;
        if (getLogSetting() != null ? !getLogSetting().equals(job.getLogSetting()) : job.getLogSetting() != null)
            return false;
        if (getRetryPolicy() != null ? !getRetryPolicy().equals(job.getRetryPolicy()) : job.getRetryPolicy() != null)
            return false;
        if (getStatus() != null ? !getStatus().equals(job.getStatus()) : job.getStatus() != null) return false;
        if (getCreateTime() != null ? !getCreateTime().equals(job.getCreateTime()) : job.getCreateTime() != null)
            return false;
        return getLastModifiedTime() != null ? getLastModifiedTime().equals(job.getLastModifiedTime()) : job.getLastModifiedTime() == null;
    }

    @Override
    public int hashCode() {
        int result = getJobName() != null ? getJobName().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getSchedule() != null ? getSchedule().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getArguments() != null ? getArguments().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getLogSetting() != null ? getLogSetting().hashCode() : 0);
        result = 31 * result + (int) (getTimeout() ^ (getTimeout() >>> 32));
        result = 31 * result + (getRetryPolicy() != null ? getRetryPolicy().hashCode() : 0);
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (getCreateTime() != null ? getCreateTime().hashCode() : 0);
        result = 31 * result + (getLastModifiedTime() != null ? getLastModifiedTime().hashCode() : 0);
        return result;
    }
}
