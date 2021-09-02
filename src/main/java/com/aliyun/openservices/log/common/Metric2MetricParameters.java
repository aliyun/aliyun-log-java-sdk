package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

public class Metric2MetricParameters implements ScheduledSQLParameters {
    private String metricName;
    private String addLabels;
    private String hashLabels;

    @Override
    public void deserialize(JSONObject value) {
        metricName = value.getString("metricName");
        addLabels = value.getString("addLabels");
        hashLabels = value.getString("hashLabels");
    }

    @Override
    public int hashCode() {
        int result = getMetricName() != null ? getMetricName().hashCode() : 0;
        result = 31 * result + (getAddLabels() != null ? getAddLabels().hashCode() : 0);
        result = 31 * result + (getHashLabels() != null ? getHashLabels().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Metric2MetricParameters that = (Metric2MetricParameters) obj;
        if (getMetricName() != null ? !getMetricName().equals(that.getMetricName()) : that.getMetricName() != null) {
            return false;
        }
        if (getHashLabels() != null ? !getHashLabels().equals(that.getHashLabels()) : that.getHashLabels() != null) {
            return false;
        }
        return getAddLabels() != null ? getAddLabels().equals(that.getAddLabels()) : that.getAddLabels() == null;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getAddLabels() {
        return addLabels;
    }

    public void setAddLabels(String addLabels) {
        this.addLabels = addLabels;
    }

    public String getHashLabels() {
        return hashLabels;
    }

    public void setHashLabels(String hashLabels) {
        this.hashLabels = hashLabels;
    }
}
