package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.io.Serializable;

public class AlertV2 implements Serializable {

    /**
     * Alert rule name.
     */
    private String name;

    /**
     * Alert configuration.
     */
    private AlertConfiguration configuration;

    /**
     * How to trigger alert.
     */
    private JobSchedule schedule;

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

    public void deserialize(JSONObject value) {
        name = value.getString("name");
        configuration = new AlertConfiguration();
        configuration.deserialize(value.getJSONObject("configuration"));
        schedule = JsonUtils.deserialize(value.getString("schedule"), JobSchedule.class);
    }

    public Job convertToJob() {
        Args.notNullOrEmpty(name, "Alert name");
        Args.notNull(configuration, "configuration");
        Args.notNull(schedule, "schedule");
        Job job = new Job();
        job.setType(JobType.ALERT);
        job.setName(getName());
        job.setSchedule(getSchedule());
        job.setConfiguration(getConfiguration());
        return job;
    }
}
