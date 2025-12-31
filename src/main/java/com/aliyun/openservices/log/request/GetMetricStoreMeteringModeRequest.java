package com.aliyun.openservices.log.request;

public class GetMetricStoreMeteringModeRequest extends Request {

    private String metricStore;


    public GetMetricStoreMeteringModeRequest(String project, String metricStore) {
        super(project);
        this.metricStore = metricStore;
    }

    public String getMetricStore() {
        return metricStore;
    }

    public void setMetricStore(String metricStore) {
        this.metricStore = metricStore;
    }
}

