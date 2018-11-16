package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.Utils;

import java.lang.reflect.Type;

public enum TimeSpanType implements JSONSerializable {
    RELATIVE("Relative") {
        @Override
        public void validate(String start, String end, long max) {
            long d = parseStart(start);
            if (d > max) {
                throw new IllegalArgumentException("Timespan must be less than or equal to " + max);
            }
        }
    },
    TRUNCATED("Truncated") {
        @Override
        public void validate(String start, String end, long max) {
            long d = parseStart(start);
            if (d > max) {
                throw new IllegalArgumentException("Timespan must be less than or equal to " + max);
            }
        }
    },
    TODAY("Today") {
        @Override
        public void validate(String start, String end, long max) {
        }
    },
    CUSTOM("Custom") {
        @Override
        public void validate(String start, String end, long max) {
            if (!"now".equals(end) && !"absolute".equals(end)) {
                throw new IllegalArgumentException("Invalid timespan end for custom type: " + end);
            }
            long d = parseStart(start);
            if (d > max) {
                throw new IllegalArgumentException("Timespan must be less than or equal to " + max);
            }
        }
    };

    private final String value;

    TimeSpanType(String value) {
        this.value = value;
    }

    protected long parseStart(String start) {
        Args.notNullOrEmpty(start, "start");
        if (!start.startsWith("-")) {
            throw new IllegalArgumentException("Timespan start must be start with -");
        }
        return Utils.parseDuration(start.substring(1));
    }

    public abstract void validate(String start, String end, long max);

    @Override
    public String toString() {
        return value;
    }

    public static TimeSpanType fromString(String value) {
        for (TimeSpanType timeSpanType : TimeSpanType.values()) {
            if (timeSpanType.value.equals(value)) {
                return timeSpanType;
            }
        }
        throw new IllegalArgumentException("Invalid timespan type: " + value);
    }

    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
        serializer.write(toString());
    }
}
