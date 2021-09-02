package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.MetricAggRules;

public class CreateMetricAggRulesRequest extends Request {

    private static final long serialVersionUID = -4585612389837219188L;
    protected MetricAggRules metricAggRules;

    public CreateMetricAggRulesRequest(String project, MetricAggRules metricAggRules) {
        super(project);
        this.metricAggRules = metricAggRules;
    }

    public MetricAggRules getMetricAggRules() {
        return metricAggRules;
    }

    public void setMetricAggRules(MetricAggRules metricAggRules) {
        this.metricAggRules = metricAggRules;
    }
}
