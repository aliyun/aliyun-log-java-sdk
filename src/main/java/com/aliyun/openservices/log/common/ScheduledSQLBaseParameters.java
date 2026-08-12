package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.*;
import com.aliyun.openservices.log.annotation.InternalApi;


public class ScheduledSQLBaseParameters implements ScheduledSQLParameters {
    private Map<String, String> baseParams;

    private final Set<String> fields = new HashSet<String>();

    public ScheduledSQLBaseParameters() {
        baseParams = new HashMap<String, String>();
        fields.add("__task_type__");
    }

    public void withFields(String... fields) {
        this.fields.addAll(Arrays.asList(fields));
    }

    public Map<String, String> getBaseParams() {
        return baseParams;
    }

    public void setBaseParams(Map<String, String> params) {
        if (!fields.containsAll(params.keySet())) {
            return;
        }
        this.baseParams = params;
    }

    public void addBaseParams(String key, String value) {
        if (!fields.contains(key)) {
            return;
        }
        baseParams.put(key, value);
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = new JSONObject();
        for (Map.Entry<String, String> entry : baseParams.entrySet()) {
            value.put(entry.getKey(), entry.getValue());
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        for (String key : value.keySet()) {
            if (!fields.contains(key)) {
                continue;
            }
            baseParams.put(key, value.getString(key));
        }
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (String v : baseParams.values()) {
            result = result * 31 + v.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ScheduledSQLBaseParameters that = (ScheduledSQLBaseParameters) obj;
        if (baseParams.size() != that.getBaseParams().size()) {
            return false;
        }
        for (String key : baseParams.keySet()) {
            if (!that.getBaseParams().containsKey(key)) {
                return false;
            }
            if (!getBaseParams().get(key).equals(that.getBaseParams().get(key))) {
                return false;
            }
        }
        return true;
    }
}
