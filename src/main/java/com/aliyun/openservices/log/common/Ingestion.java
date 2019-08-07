package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;

import java.io.Serializable;

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

    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        configuration = new IngestionConfiguration();
        configuration.deserialize(jsonObject.getJSONObject("configuration"));
    }
}
