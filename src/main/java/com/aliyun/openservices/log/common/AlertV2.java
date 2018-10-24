package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;


public class AlertV2 implements Serializable {

    /**
     * Alert rule name.
     */
    private String name;

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
        state = JobState.fromString(value.getString("state"));
        configuration = new AlertConfiguration();
        configuration.deserialize(value.getJSONObject("configuration"));
        createTime = new Date(value.getLong("createTime") * 1000);
        lastModifiedTime = new Date(value.getLong("lastModifiedTime") * 1000);
        schedule = new JobSchedule();
        schedule.deserialize(value.getJSONObject("schedule"));
    }

    public Job convertToJob() {
        Args.notNullOrEmpty(name, "Alert name");
        Args.notNull(configuration, "configuration");
        Args.notNull(schedule, "schedule");
        Job job = new Job();
        job.setType(JobType.ALERT);
        job.setName(getName());
        job.setState(getState());
        job.setSchedule(getSchedule());
        job.setConfiguration(getConfiguration());
        return job;
    }
}
