package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.JSONObject;

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
    private TimeSpanType timeSpanType;

    @JSONField
    private String start;

    @JSONField
    private String end;

    @JSONField
    private String storeType;
    @JSONField
    private String project;
    @JSONField
    private String store;
    @JSONField
    private String ui;
    @JSONField
    private String region;
    @JSONField
    private String roleArn;
    @JSONField
    private String dashboardId;

    public String getRoleArn() {
        return roleArn;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

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

    public TimeSpanType getTimeSpanType() {
        return timeSpanType;
    }

    public void setTimeSpanType(TimeSpanType timeSpanType) {
        this.timeSpanType = timeSpanType;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    public void deserialize(JSONObject value) {
        setChartTitle(value.getString("chartTitle"));
        setLogStore(value.getString("logStore"));
        setQuery(value.getString("query"));
        setTimeSpanType(TimeSpanType.fromString(value.getString("timeSpanType")));
        if (value.containsKey("start")) {
            setStart(value.getString("start"));
        }
        if (value.containsKey("end")) {
            setEnd(value.getString("end"));
        }
        if (value.containsKey("storeType")) {
            setStoreType(value.getString("storeType"));
        }
        if (value.containsKey("store")) {
            setStore(value.getString("store"));
        }
        if (value.containsKey("ui")) {
            setUi(value.getString("ui"));
        }
        if (value.containsKey("region")) {
            setRegion(value.getString("region"));
        }
        if (value.containsKey("project")) {
            setProject(value.getString("project"));
        }
        if (value.containsKey("roleArn")) {
            setRoleArn(value.getString("roleArn"));
        }
        if (value.containsKey("dashboardId")) {
            setDashboardId(value.getString("dashboardId"));
        }
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
        if (getTimeSpanType() != query1.getTimeSpanType()) return false;
        if (getStart() != null ? !getStart().equals(query1.getStart()) : query1.getStart() != null) return false;
        return getEnd() != null ? getEnd().equals(query1.getEnd()) : query1.getEnd() == null;
    }

    @Override
    public int hashCode() {
        int result = getChartTitle() != null ? getChartTitle().hashCode() : 0;
        result = 31 * result + (getQuery() != null ? getQuery().hashCode() : 0);
        result = 31 * result + (getLogStore() != null ? getLogStore().hashCode() : 0);
        result = 31 * result + (getTimeSpanType() != null ? getTimeSpanType().hashCode() : 0);
        result = 31 * result + (getStart() != null ? getStart().hashCode() : 0);
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        return result;
    }
}