package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.Args;
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

    /**
     * Duration in range [60, 86400] seconds.
     * Duration format e,g:
     * "60s" => 60 seconds
     * "1h" => 1 hour
     * "2m" => 2 minutes
     * "1d" => 1 day
     */
    @JSONField
    private String duration;

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        Args.checkDuration(duration);
        this.duration = duration;
    }

    public void deserialize(JSONObject value) {
        setChartTitle(value.getString("chartTitle"));
        setDuration(value.getString("duration"));
        setLogStore(value.getString("logStore"));
        setQuery(value.getString("query"));
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
        return getDuration() != null ? getDuration().equals(query1.getDuration()) : query1.getDuration() == null;
    }

    @Override
    public int hashCode() {
        int result = getChartTitle() != null ? getChartTitle().hashCode() : 0;
        result = 31 * result + (getQuery() != null ? getQuery().hashCode() : 0);
        result = 31 * result + (getLogStore() != null ? getLogStore().hashCode() : 0);
        result = 31 * result + (getDuration() != null ? getDuration().hashCode() : 0);
        return result;
    }
}