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
    private LogSetting logging;

    @JSONField
    private long timeout;

    @JSONField
    private RetryPolicy retryPolicy;


    // ---------- read only properties ----------------

    // execution status
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

    public LogSetting getLogging() {
        return logging;
    }

    public void setLogging(LogSetting logging) {
        this.logging = logging;
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

    public enum JobType {
        Alert,
        Report,
        ScheduleSearch;
        // etc

        @Override
        public String toString() {
            return name();
        }

        public static JobType fromString(String value) {
            for (JobType type : JobType.values()) {
                if (type.name().equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown job type: " + value);
        }
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
        // FIXME We have to deserialize Job manually because fastjson
        // cannot deserialize abstract class based on type.
        jobName = value.getString("jobName");
        type = JobType.fromString(value.getString("type"));
        state = JobState.fromString(value.getString("state"));
        timeout = value.getLong("timeout");
        description = value.getString("description");
        createTime = new Date(value.getLong("createTime"));
        lastModifiedTime = new Date(value.getLong("lastModifiedTime"));
        schedule = JsonUtils.deserialize(value.getString("schedule"), JobSchedule.class);
        if (value.containsKey("logging")) {
            logging = JsonUtils.deserialize(value.getString("logging"), LogSetting.class);
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
}
