package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

public class Log2MetricParameters extends ScheduledSQLBaseParameters {
    private String timeKey;
    private String metricKeys;
    private String labelKeys;
    private String hashLabels;
    private String addLabels;

    public Log2MetricParameters() {
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        timeKey = value.getString("timeKey");
        metricKeys = value.getString("metricKeys");
        labelKeys = value.getString("labelKeys");
        hashLabels = value.getString("hashLabels");
        addLabels = value.getString("addLabels");
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getTimeKey() != null ? getTimeKey().hashCode() : 0);
        result = 31 * result + (getMetricKeys() != null ? getMetricKeys().hashCode() : 0);
        result = 31 * result + (getLabelKeys() != null ? getLabelKeys().hashCode() : 0);
        result = 31 * result + (getHashLabels() != null ? getHashLabels().hashCode() : 0);
        result = 31 * result + (getAddLabels() != null ? getAddLabels().hashCode() : 0);
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
        if (!super.equals(obj)) {
            return false;
        }
        Log2MetricParameters that = (Log2MetricParameters) obj;
        if (getMetricKeys() != null ? !getMetricKeys().equals(that.getMetricKeys()) : that.getMetricKeys() != null) {
            return false;
        }
        if (getLabelKeys() != null ? !getLabelKeys().equals(that.getLabelKeys()) : that.getLabelKeys() != null) {
            return false;
        }
        if (getHashLabels() != null ? !getHashLabels().equals(that.getHashLabels()) : that.getHashLabels() != null) {
            return false;
        }
        return getAddLabels() != null ? getAddLabels().equals(that.getAddLabels()) : that.getAddLabels() == null;
    }

    public String getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }

    public String getMetricKeys() {
        return metricKeys;
    }

    public void setMetricKeys(String metricKeys) {
        this.metricKeys = metricKeys;
    }

    public String getLabelKeys() {
        return labelKeys;
    }

    public void setLabelKeys(String labelKeys) {
        this.labelKeys = labelKeys;
    }

    public String getHashLabels() {
        return hashLabels;
    }

    public void setHashLabels(String hashLabels) {
        this.hashLabels = hashLabels;
    }

    public String getAddLabels() {
        return addLabels;
    }

    public void setAddLabels(String addLabels) {
        this.addLabels = addLabels;
    }
}
