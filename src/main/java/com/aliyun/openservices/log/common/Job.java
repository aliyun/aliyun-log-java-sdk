package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Date;


/**
 * Will be removed in next release.
 */

public class Job implements Serializable {

    private static final long serialVersionUID = 6457720937101208563L;

    /**
     * The name of job.
     */
    @JSONField
    private String name;

    @JSONField
    private String displayName;

    /**
     * The type of job. See {@link JobType}
     */
    @JSONField
    private JobType type;

    /**
     * The description of job
     */
    @JSONField
    private String description;

    /**
     * The time of job create time.
     */
    private Date createTime;

    /**
     * The time of last modified.
     */
    private Date lastModifiedTime;

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
     * The configuration of job.
     */
    @JSONField
    private JobConfiguration configuration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public JobConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(JobConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    private static JobConfiguration createConfiguration(JobType type) {
        switch (type) {
            case ALERT:
                return new AlertConfiguration();
            case REPORT:
                return new ReportConfiguration();
            case ETL:
                return new ETLConfiguration();
            case EXPORT:
                return new ExportConfiguration();
            case AUDIT_JOB:
                return new AuditJobConfiguration();
            case INGESTION:
                return new IngestionConfiguration();
            case REBUILD_INDEX:
                return new RebuildIndexConfiguration();
            default:
                return null;
        }
    }

    public void deserialize(JSONObject value) {
        name = value.getString("name");
        displayName = JsonUtils.readOptionalString(value, "displayName");
        type = JobType.fromString(value.getString("type"));
        if (value.containsKey("state")) {
            state = JobState.fromString(value.getString("state"));
        }
        description = JsonUtils.readOptionalString(value, "description");
        if (value.containsKey("createTime")) {
            createTime = Utils.timestampToDate(value.getLong("createTime"));
        }
        if (value.containsKey("lastModifiedTime")) {
            lastModifiedTime = Utils.timestampToDate(value.getLong("lastModifiedTime"));
        }
        schedule = new JobSchedule();
        schedule.deserialize(value.getJSONObject("schedule"));
        configuration = createConfiguration(type);
        if (configuration != null) {
            configuration.deserialize(value.getJSONObject("configuration"));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        if (getName() != null ? !getName().equals(job.getName()) : job.getName() != null) return false;
        if (getDisplayName() != null ? !getDisplayName().equals(job.getDisplayName()) : job.getDisplayName() != null)
            return false;
        if (getType() != job.getType()) return false;
        if (getDescription() != null ? !getDescription().equals(job.getDescription()) : job.getDescription() != null)
            return false;
        if (getCreateTime() != null ? !getCreateTime().equals(job.getCreateTime()) : job.getCreateTime() != null)
            return false;
        if (getLastModifiedTime() != null ? !getLastModifiedTime().equals(job.getLastModifiedTime()) : job.getLastModifiedTime() != null)
            return false;
        if (getSchedule() != null ? !getSchedule().equals(job.getSchedule()) : job.getSchedule() != null) return false;
        if (getState() != job.getState()) return false;
        return getConfiguration() != null ? getConfiguration().equals(job.getConfiguration()) : job.getConfiguration() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDisplayName() != null ? getDisplayName().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getCreateTime() != null ? getCreateTime().hashCode() : 0);
        result = 31 * result + (getLastModifiedTime() != null ? getLastModifiedTime().hashCode() : 0);
        result = 31 * result + (getSchedule() != null ? getSchedule().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getConfiguration() != null ? getConfiguration().hashCode() : 0);
        return result;
    }
}
