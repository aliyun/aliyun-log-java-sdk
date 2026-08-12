package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JsonEnumAdapter;
import com.aliyun.openservices.log.internal.json.JsonEnumCreator;
import com.aliyun.openservices.log.internal.json.JsonEnumValue;

@JsonEnumAdapter
public enum JobScheduleType {
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
     * Long live.
     */
    RESIDENT("Resident"),
    ;

    private final String value;

    JobScheduleType(String value) {
        this.value = value;
    }

    @JsonEnumValue
    @Override
    public String toString() {
        return value;
    }

    @JsonEnumCreator
    public static JobScheduleType fromString(String value) {
        for (JobScheduleType type : JobScheduleType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

}
