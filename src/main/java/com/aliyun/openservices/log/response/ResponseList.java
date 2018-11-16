package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.Unmarshaller;
import net.sf.json.JSONObject;

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

    public abstract Unmarshaller<T> unmarshaller();

    public void deserialize(JSONObject value) {
        count = value.getInt(Consts.CONST_COUNT);
        total = value.getInt(Consts.CONST_TOTAL);
        results = JsonUtils.readList(value, "results", unmarshaller());
    }
}
