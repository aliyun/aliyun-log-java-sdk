package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;


public class Report implements Serializable {

    private static final long serialVersionUID = 9211926785430833230L;

    /**
     * Report rule name.
     */
    private String name;

    /**
     * Report display name.
     */
    private String displayName;

    /**
     * Report description.
     */
    private String description;

    /**
     * Report rule state.
     */
    private JobState state;

    /**
     * Report configuration.
     */
    private ReportConfiguration configuration;

    /**
     * How to trigger report.
     */
    private JobSchedule schedule;

    /**
     * Report rule create time.
     */
    private Date createTime;

    /**
     * Report rule last modified time.
     */
    private Date lastModifiedTime;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ReportConfiguration configuration) {
        this.configuration = configuration;
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

    public void deserialize(JSONObject value) {
        name = value.getString("name");
        displayName = JsonUtils.readOptionalString(value, "displayName");
        description = JsonUtils.readOptionalString(value, "description");
        state = JobState.fromString(value.getString("state"));
        configuration = new ReportConfiguration();
        configuration.deserialize(value.getJSONObject("configuration"));
        createTime = Utils.timestampToDate(value.getLong("createTime"));
        lastModifiedTime = Utils.timestampToDate(value.getLong("lastModifiedTime"));
        schedule = new JobSchedule();
        schedule.deserialize(value.getJSONObject("schedule"));
    }

    public Job makeJob() {
        Job job = new Job();
        job.setType(JobType.REPORT);
        job.setName(getName());
        job.setDisplayName(getDisplayName());
        job.setDescription(getDescription());
        job.setState(getState());
        job.setSchedule(getSchedule());
        job.setConfiguration(getConfiguration());
        return job;
    }
}
