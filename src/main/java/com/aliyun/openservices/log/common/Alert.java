package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.Utils;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class Alert implements Serializable {

    private static final long serialVersionUID = 9211926785430833230L;

    /**
     * Alert rule name.
     */
    private String name;

    /**
     * Alert display name.
     */
    private String displayName;

    /**
     * Alert description.
     */
    private String description;

    /**
     * Alert rule state.
     */
    private JobState state;

    /**
     * Alert configuration.
     */
    private AlertConfiguration configuration;

    /**
     * How to trigger alert.
     */
    private JobSchedule schedule;

    /**
     * Alert rule create time.
     */
    private Date createTime;

    /**
     * Alert rule last modified time.
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

    public AlertConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AlertConfiguration configuration) {
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
        if (value.has("displayName")) {
            displayName = value.getString("displayName");
        }
        if (value.has("description")) {
            description = value.getString("description");
        }
        state = JobState.fromString(value.getString("state"));
        configuration = new AlertConfiguration();
        configuration.deserialize(value.getJSONObject("configuration"));
        createTime = Utils.timestampToDate(value.getLong("createTime"));
        lastModifiedTime = Utils.timestampToDate(value.getLong("lastModifiedTime"));
        schedule = new JobSchedule();
        schedule.deserialize(value.getJSONObject("schedule"));
    }

    public void validate() {
        Args.notNullOrEmpty(name, "name");
        Args.notNullOrEmpty(displayName, "displayName");
        Args.notNull(configuration, "configuration");
        List<Query> queries = configuration.getQueryList();
        Args.notNullOrEmpty(queries, "Query list");
        for (Query query : queries) {
            Args.notNull(query, "query");
        }
        Args.notNull(schedule, "schedule");
    }

    public Job makeJob() {
        Job job = new Job();
        job.setType(JobType.ALERT);
        job.setName(getName());
        job.setDisplayName(getDisplayName());
        job.setDescription(getDescription());
        job.setState(getState());
        job.setSchedule(getSchedule());
        job.setConfiguration(getConfiguration());
        return job;
    }
}
