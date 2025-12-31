package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.util.Args;

public class UpdateMetricStoreMeteringModeRequest extends Request {

    private String metricStore;
    private String meteringMode;

    public UpdateMetricStoreMeteringModeRequest(String project, String metricStore, String meteringMode) {
        super(project);
        Args.notNullOrEmpty(metricStore, "metricStore");
        Args.notNullOrEmpty(meteringMode, "meteringMode");
        this.metricStore = metricStore;
        this.meteringMode = meteringMode;
    }

    public String getMetricStore() {
        return metricStore;
    }

    public void setMetricStore(String metricStore) {
        this.metricStore = metricStore;
    }

    public String getMeteringMode() {
        return meteringMode;
    }

    public void setMeteringMode(String meteringMode) {
        this.meteringMode = meteringMode;
    }

    public String getRequestBody() {
        JSONObject body = new JSONObject();
        body.put("meteringMode", meteringMode);
        return body.toString();
    }
}

