package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.lang.reflect.Type;

public enum JobScheduleType implements JSONSerializable {
    /**
     * Trigger in a fixed rate.
     */
    FIXED_RATE("FixedRate"),

    /**
     * Run each hour.
     */
    HOURLY("Hourly"),

    /**
     * Run each day
     */
    DAILY("Daily"),

    /**
     * Run each week.
     */
    WEEKLY("Weekly"),

    /**
     * Custom cron expression.
     */
    CRON("Cron"),

    /**
     * Only once.
     */
    DRY_RUN("DryRun"),

    /**
     * Not scheduled.
     */
    NONE("None"),

    /**
     * Long live.
     */
    RESIDENT("Resident"),
    ;

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
