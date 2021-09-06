package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

public class MetricAggRuleItem implements Serializable {

    private static final long serialVersionUID = -6302157336524923574L;
    private String name;
    private String queryType;
    private String query;
    private String timeName;
    private String[] metricNames;
    private Map<String, String> labelNames;

    private int beginUnixTime;
    private int endUnixTime;
    private int interval;
    private int delaySeconds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTimeName() {
        return timeName;
    }

    public void setTimeName(String timeName) {
        this.timeName = timeName;
    }

    public String[] getMetricNames() {
        return metricNames;
    }

    public void setMetricNames(String[] metricNames) {
        this.metricNames = metricNames;
    }

    public Map<String, String> getLabelNames() {
        return labelNames;
    }

    public void setLabelNames(Map<String, String> labelNames) {
        this.labelNames = labelNames;
    }

    public int getBeginUnixTime() {
        return beginUnixTime;
    }

    public void setBeginUnixTime(int beginUnixTime) {
        this.beginUnixTime = beginUnixTime;
    }

    public int getEndUnixTime() {
        return endUnixTime;
    }

    public void setEndUnixTime(int endUnixTime) {
        this.endUnixTime = endUnixTime;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricAggRuleItem that = (MetricAggRuleItem) o;

        if (beginUnixTime != that.beginUnixTime) return false;
        if (endUnixTime != that.endUnixTime) return false;
        if (interval != that.interval) return false;
        if (delaySeconds != that.delaySeconds) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (queryType != null ? !queryType.equals(that.queryType) : that.queryType != null) return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (timeName != null ? !timeName.equals(that.timeName) : that.timeName != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(metricNames, that.metricNames)) return false;
        return labelNames != null ? labelNames.equals(that.labelNames) : that.labelNames == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (queryType != null ? queryType.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (timeName != null ? timeName.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(metricNames);
        result = 31 * result + (labelNames != null ? labelNames.hashCode() : 0);
        result = 31 * result + beginUnixTime;
        result = 31 * result + endUnixTime;
        result = 31 * result + interval;
        result = 31 * result + delaySeconds;
        return result;
    }
}
