package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;

import java.util.HashMap;
import java.util.Map;

@JSONType(serializer = ToGeneralSerializer.class)
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
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        for (String field : jsonObject.keySet()) {
            put(field, jsonObject.get(field));
        }
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(fields);
    }

}
