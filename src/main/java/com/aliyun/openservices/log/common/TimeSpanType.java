package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.aliyun.openservices.log.util.Utils;

import java.lang.reflect.Type;

public enum TimeSpanType implements JSONSerializable {
    /**
     * Predefined relative timespan type. The end is fixed to now.
     */
    RELATIVE("Relative") {
        @Override
        public void validate(String start, String end, long max) {
            checkRange(parseStart(start), max);
        }
    },
    TRUNCATED("Truncated") {
        @Override
        public void validate(String start, String end, long max) {
            checkRange(parseStart(start), max);
        }
    },
    TODAY("Today") {
        @Override
        public void validate(String start, String end, long max) {
        }
    },
    CUSTOM("Custom") {
        private long parseEnd(String end) {
            long now = (System.currentTimeMillis() / 1000);
            if (end == null || end.isEmpty() || end.equals("now")) {
                return now;
            } else if (!end.startsWith("-")) {
                throw new IllegalArgumentException("Invalid timespan end: " + end);
            }
            return Utils.parseDuration(end.substring(1));
        }

        @Override
        public void validate(String start, String end, long max) {
            if ("absolute".equals(end)) {
                TRUNCATED.validate(start, end, max);
            } else {
                long startAt = parseStart(start);
                long endAt = parseEnd(end);
                checkRange(endAt - startAt, max);
            }
        }
    };

    private final String value;

    TimeSpanType(String value) {
        this.value = value;
    }

    protected void checkRange(long width, long max) {
        if (width > max) {
            throw new IllegalArgumentException("Timespan must be less than or equal to " + max);
        }
    }

    protected long parseStart(String start) {
        if (start == null || !start.startsWith("-")) {
            throw new IllegalArgumentException("Invalid timespan start: " + start);
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
