package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

import java.io.Serializable;

public class Query implements Serializable {

    @JSONField
    private String chart;

    @JSONField
    private String query;

    @JSONField
    private String logStore;

    @JSONField
    private Long startTime;

    @JSONField
    private Long endTime;

    public String getChart() {
        return chart;
    }

    public void setChart(String chart) {
        this.chart = chart;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLogStore() {
        return logStore;
    }

    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public void deserialize(JSONObject value) {
        setChart(value.getString("chart"));
        setEndTime(value.getLong("endTime"));
        setStartTime(value.getLong("startTime"));
        setLogStore(value.getString("logStore"));
        setQuery(value.getString("query"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query1 = (Query) o;

        if (getChart() != null ? !getChart().equals(query1.getChart()) : query1.getChart() != null) return false;
        if (getQuery() != null ? !getQuery().equals(query1.getQuery()) : query1.getQuery() != null) return false;
        if (getLogStore() != null ? !getLogStore().equals(query1.getLogStore()) : query1.getLogStore() != null)
            return false;
        if (getStartTime() != null ? !getStartTime().equals(query1.getStartTime()) : query1.getStartTime() != null)
            return false;
        return getEndTime() != null ? getEndTime().equals(query1.getEndTime()) : query1.getEndTime() == null;
    }

    @Override
    public int hashCode() {
        int result = getChart() != null ? getChart().hashCode() : 0;
        result = 31 * result + (getQuery() != null ? getQuery().hashCode() : 0);
        result = 31 * result + (getLogStore() != null ? getLogStore().hashCode() : 0);
        result = 31 * result + (getStartTime() != null ? getStartTime().hashCode() : 0);
        result = 31 * result + (getEndTime() != null ? getEndTime().hashCode() : 0);
        return result;
    }
}