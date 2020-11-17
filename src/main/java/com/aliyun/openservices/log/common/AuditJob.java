package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class AuditJob extends ScheduledJob implements Serializable {

    private static final long serialVersionUID = 5950790729563144144L;

    private AuditJobConfiguration configuration;

    public AuditJob() {
        setType(JobType.AUDIT_JOB);
        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.RESIDENT);
        setSchedule(schedule);
    }

    public void setConfiguration(AuditJobConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public JobConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        configuration = new AuditJobConfiguration();
        configuration.deserialize(value.getJSONObject("configuration"));
    }

    public String toString(){
        JSONObject value = new JSONObject();
        value.put("name", getName());
        value.put("type", getType().toString());
        value.put("displayName", getDisplayName());
        value.put("description", getDescription());
        JSONObject scheduleJson = new JSONObject();
        scheduleJson.put("type", getSchedule().getType().toString());
        value.put("schedule", scheduleJson);
        value.put("configuration", this.configuration.toJsonObject());
        return value.toString();
    }
}
