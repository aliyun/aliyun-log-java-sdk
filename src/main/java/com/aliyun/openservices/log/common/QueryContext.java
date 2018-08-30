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
    private List<Query> queries;

    public QueryContext() {
        // For JSON deserialization
    }

    public QueryContext(String dashboard, List<Query> queries) {
        Args.notNullOrEmpty(dashboard, "dashboard");
        Args.notNullOrEmpty(queries, "queries");
        this.dashboard = dashboard;
        this.queries = queries;
    }

    public String getDashboard() {
        return dashboard;
    }

    public void setDashboard(String dashboard) {
        this.dashboard = dashboard;
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }

    public void deserialize(JSONObject value) {
        setDashboard(value.getString("dashboard"));
        JSONArray queries = value.getJSONArray("queries");
        List<Query> queryList = new ArrayList<Query>(queries.size());
        for (int i = 0; i < queries.size(); i++) {
            Query query = new Query();
            query.deserialize(queries.getJSONObject(i));
            queryList.add(query);
        }
        setQueries(queryList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryContext that = (QueryContext) o;

        if (getDashboard() != null ? !getDashboard().equals(that.getDashboard()) : that.getDashboard() != null)
            return false;
        return getQueries() != null ? getQueries().equals(that.getQueries()) : that.getQueries() == null;
    }

    @Override
    public int hashCode() {
        int result = getDashboard() != null ? getDashboard().hashCode() : 0;
        result = 31 * result + (getQueries() != null ? getQueries().hashCode() : 0);
        return result;
    }
}
