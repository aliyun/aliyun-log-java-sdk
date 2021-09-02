package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.MetricAggRules;

public class UpdateMetricAggRulesRequest extends Request {
    private static final long serialVersionUID = 3335785551791754002L;
    protected MetricAggRules metricAggRules;

    public UpdateMetricAggRulesRequest(String project, MetricAggRules metricAggRules) {
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
