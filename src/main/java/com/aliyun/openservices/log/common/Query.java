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
    private long period;

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

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public void deserialize(JSONObject value) {
        setChart(value.getString("chart"));
        setPeriod(value.getLong("period"));
        setLogStore(value.getString("logStore"));
        setQuery(value.getString("query"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query1 = (Query) o;

        if (getPeriod() != query1.getPeriod())
            return false;
        if (getChart() != null ? !getChart().equals(query1.getChart()) : query1.getChart() != null) return false;
        if (getQuery() != null ? !getQuery().equals(query1.getQuery()) : query1.getQuery() != null) return false;
        if (getLogStore() != null ? !getLogStore().equals(query1.getLogStore()) : query1.getLogStore() != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = getChart() != null ? getChart().hashCode() : 0;
        result = 31 * result + (getQuery() != null ? getQuery().hashCode() : 0);
        result = 31 * result + (getLogStore() != null ? getLogStore().hashCode() : 0);
        result = 31 * result + (int) (getPeriod() ^ (getPeriod() >>> 32));
        return result;
    }
}