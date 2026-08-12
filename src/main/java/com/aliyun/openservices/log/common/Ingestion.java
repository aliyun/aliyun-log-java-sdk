package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class Ingestion extends ScheduledJob implements Serializable {

    private static final long serialVersionUID = -6535073053545538036L;

    private IngestionConfiguration configuration;

    public Ingestion() {
        setType(JobType.INGESTION);
    }

    public void setConfiguration(IngestionConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public IngestionConfiguration getConfiguration() {
        return configuration;
    }

    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) {
        super.fromJsonObject(jsonObject);
        configuration = new IngestionConfiguration();
        configuration.fromJsonObject(jsonObject.getJSONObject("configuration"));
    }
}
