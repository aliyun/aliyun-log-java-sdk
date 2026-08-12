package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;


public class Report extends ScheduledJob implements Serializable {

    private static final long serialVersionUID = 9211926785430833230L;

    private ReportConfiguration configuration;

    public Report() {
        setType(JobType.REPORT);
    }

    @Override
    public ReportConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ReportConfiguration configuration) {
        this.configuration = configuration;
    }

    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
        configuration = new ReportConfiguration();
        configuration.fromJsonObject(value.getJSONObject("configuration"));
    }
}
