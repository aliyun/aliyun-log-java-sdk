package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.Args;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryContext implements Serializable {

    @JSONField
    private String dashboard;

    @JSONField
    private List<Query> queryList;

    public QueryContext() {
        // For JSON deserialization
    }

    public QueryContext(String dashboard, List<Query> queryList) {
        Args.notNullOrEmpty(dashboard, "dashboard");
        Args.notNullOrEmpty(queryList, "queryList");
        this.dashboard = dashboard;
        this.queryList = queryList;
    }

    public String getDashboard() {
        return dashboard;
    }

    public void setDashboard(String dashboard) {
        this.dashboard = dashboard;
    }

    public List<Query> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<Query> queryList) {
        this.queryList = queryList;
    }

    public void deserialize(JSONObject value) {
        setDashboard(value.getString("dashboard"));
        JSONArray queries = value.getJSONArray("queryList");
        queryList = new ArrayList<Query>(queries.size());
        for (int i = 0; i < queries.size(); i++) {
            Query query = new Query();
            query.deserialize(queries.getJSONObject(i));
            queryList.add(query);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryContext that = (QueryContext) o;

        if (getDashboard() != null ? !getDashboard().equals(that.getDashboard()) : that.getDashboard() != null)
            return false;
        return getQueryList() != null ? getQueryList().equals(that.getQueryList()) : that.getQueryList() == null;
    }

    @Override
    public int hashCode() {
        int result = getDashboard() != null ? getDashboard().hashCode() : 0;
        result = 31 * result + (getQueryList() != null ? getQueryList().hashCode() : 0);
        return result;
    }
}
