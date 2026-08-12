package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ExportGeneralSink extends DataSink {

    private Map<String, Object> fields = new HashMap<String, Object>();

    public ExportGeneralSink() {
        super(DataSinkType.GENERAL);
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Object get(String key) {
        return fields.get(key);
    }

    public void put(String key, Object value) {
        fields.put(key, value);
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) {
        super.fromJsonObject(jsonObject);
        fields = JsonCodec.toMap(jsonObject);
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        return JsonCodec.toJsonObject(fields);
    }

}
