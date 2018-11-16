package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.lang.reflect.Type;

public enum JobState implements JSONSerializable {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private final String value;

    JobState(String value) {
        this.value = value;
    }

    public static JobState fromString(String value) {
        for (JobState state : JobState.values()) {
            if (state.value.equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown job state: " + value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
        serializer.write(toString());
    }
}
