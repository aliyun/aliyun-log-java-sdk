package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Consts;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class ResponseList<T> extends Response {

    private Integer total;
    private Integer count;
    private List<T> results;

    public ResponseList(Map<String, String> headers) {
        super(headers);
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public abstract T unmarshal(JSONObject value);


    public void deserialize(JSONObject value) {
        count = value.getInt(Consts.CONST_COUNT);
        total = value.getInt(Consts.CONST_TOTAL);
        final JSONArray resultsAsJson = value.getJSONArray("results");
        results = new ArrayList<T>(resultsAsJson.size());
        for (int i = 0; i < resultsAsJson.size(); i++) {
            results.add(unmarshal(resultsAsJson.getJSONObject(i)));
        }
    }
}
