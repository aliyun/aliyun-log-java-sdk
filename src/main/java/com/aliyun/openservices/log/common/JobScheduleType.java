package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.lang.reflect.Type;

public enum JobScheduleType implements JSONSerializable {
    /**
     * Trigger in a fixed rate.
     */
    FIXED_RATE("FixedRate");

    private final String value;

    JobScheduleType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static JobScheduleType fromString(String value) {
        for (JobScheduleType type : JobScheduleType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Illegal schedule type: " + value);
    }

    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
        serializer.write(toString());
    }
}
