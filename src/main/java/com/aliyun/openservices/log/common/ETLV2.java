package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ETLV2 extends AbstractJob implements Serializable {

    private static final long serialVersionUID = 949447748635414993L;

    private ETLConfiguration configuration;

    private JobSchedule schedule;

    private String status;

    private String scheduleId;

    public ETLV2() {
        setType(JobType.ETL);
    }

    @Override
    public ETLConfiguration getConfiguration() {
        return configuration;
    }

    public JobSchedule getSchedule(){
        return schedule;
    }

    public void setSchedule(JobSchedule schedule) {
        this.schedule = schedule;
    }

    public void setConfiguration(ETLConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getStatus() {
        return status;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        put(value, "status", status);
        put(value, "scheduleId", scheduleId);
        if (schedule != null) {
            value.put("schedule", schedule.toJsonObject());
        }
        return value;
    }

    public void fromJsonString(String etlString) throws LogException {
        try {
            fromJsonObject(JSONObject.parseObject(etlString));
        } catch (JSONException e) {
            throw new LogException("FailToGenerateETLV2", e.getMessage(), e, "");
        }
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
        status = value.getString("status");
        scheduleId = JsonUtils.readOptionalString(value,"scheduleId","");
        schedule = new JobSchedule();
        schedule.fromJsonObject(value.getJSONObject("schedule"));
        configuration = new ETLConfiguration();
        configuration.fromJsonObject(value.getJSONObject("configuration"));
    }
}
