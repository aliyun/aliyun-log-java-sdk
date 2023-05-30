package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.MetricsConfig;

public class CreateMetricsConfigRequest extends Request {
    private String metricsName;
    private MetricsConfig metricsConfig;

    public CreateMetricsConfigRequest(String project, String metricsName, MetricsConfig metricsConfig) {
        super(project);
        this.metricsName = metricsName;
        this.metricsConfig = metricsConfig;
    }

    public String getMetricsName() {
        return metricsName;
    }

    public void setMetricsName(String metricsName) {
        this.metricsName = metricsName;
    }

    public MetricsConfig getMetricsConfig() {
        return metricsConfig;
    }

    public void setMetricsConfig(MetricsConfig metricsConfig) {
        this.metricsConfig = metricsConfig;
    }
}
