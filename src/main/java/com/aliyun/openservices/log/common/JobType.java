package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.lang.reflect.Type;

public enum JobType implements JSONSerializable {
    ALERT("Alert"),
    REPORT("Report"),
    ETL("ETL"),
    INGESTION("Ingestion"),
    REBUILD_INDEX("RebuildIndex"),
    EXPORT("Export");

    private final String value;

    JobType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static JobType fromString(String value) {
        for (JobType type : JobType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
        serializer.write(toString());
    }
}