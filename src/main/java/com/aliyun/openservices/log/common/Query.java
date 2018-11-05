package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

import java.io.Serializable;


public class Query implements Serializable {

    /**
     * The unique title for chart in a dashboard.
     */
    @JSONField
    private String chartTitle;

    @JSONField
    private String query;

    @JSONField
    private String logStore;

    @JSONField
    private TimeSpan timeSpan;

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
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

    public TimeSpan getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(TimeSpan timeSpan) {
        this.timeSpan = timeSpan;
    }

    public void deserialize(JSONObject value) {
        setChartTitle(value.getString("chartTitle"));
        setLogStore(value.getString("logStore"));
        setQuery(value.getString("query"));
        timeSpan = new TimeSpan();
        timeSpan.deserialize(value.getJSONObject("timeSpan"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query1 = (Query) o;

        if (getChartTitle() != null ? !getChartTitle().equals(query1.getChartTitle()) : query1.getChartTitle() != null)
            return false;
        if (getQuery() != null ? !getQuery().equals(query1.getQuery()) : query1.getQuery() != null) return false;
        if (getLogStore() != null ? !getLogStore().equals(query1.getLogStore()) : query1.getLogStore() != null)
            return false;
        return getTimeSpan() != null ? getTimeSpan().equals(query1.getTimeSpan()) : query1.getTimeSpan() == null;
    }

    @Override
    public int hashCode() {
        int result = getChartTitle() != null ? getChartTitle().hashCode() : 0;
        result = 31 * result + (getQuery() != null ? getQuery().hashCode() : 0);
        result = 31 * result + (getLogStore() != null ? getLogStore().hashCode() : 0);
        result = 31 * result + (getTimeSpan() != null ? getTimeSpan().hashCode() : 0);
        return result;
    }
}