package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class ETLV2 extends AbstractJob implements Serializable {

    private static final long serialVersionUID = 949447748635414993L;

    @JSONField
    private ETLConfiguration configuration;

    @JSONField
    private JobSchedule schedule;

    @JSONField
    private String status;

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

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        status = value.getString("status");
        schedule = new JobSchedule();
        schedule.deserialize(value.getJSONObject("schedule"));
        configuration = new ETLConfiguration();
        configuration.deserialize(value.getJSONObject("configuration"));
    }
}
