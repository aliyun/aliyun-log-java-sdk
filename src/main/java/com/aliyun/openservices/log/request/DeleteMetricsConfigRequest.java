package com.aliyun.openservices.log.request;

public class DeleteMetricsConfigRequest extends Request {
    private String metricsName;

    public DeleteMetricsConfigRequest(String project, String metricsName) {
        super(project);
        this.metricsName = metricsName;
    }

    public String getMetricsName() {
        return metricsName;
    }

    public void setMetricsName(String metricsName) {
        this.metricsName = metricsName;
    }
}
