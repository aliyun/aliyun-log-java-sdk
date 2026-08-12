package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JsonEnumAdapter;
import com.aliyun.openservices.log.internal.json.JsonEnumCreator;
import com.aliyun.openservices.log.internal.json.JsonEnumValue;

@JsonEnumAdapter
public enum TimeSpanType {
    /**
     * Relative time span, e,g -1m-now. now is trigger time.
     */
    RELATIVE("Relative"),
    /**
     * Truncated time span. e,g if current time is 2019-02-01:02:32:15:
     * -1h - (2019-02-01:01:00:00 - 2019-02-01:02:00:00)
     * -1m - (2019-02-01:01:31:00 - 2019-02-01:02:32:00)
     * -1d - (2019-02-01:00:00:00 - 2019-02-02:00:00:00)
     */
    TRUNCATED("Truncated"),
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    CUSTOM("Custom"),
    FIXED("Fixed"),
    THEDAYBEFOREYESTERDAY("TheDayBeforeYesterday"),
    THISWEEK("ThisWeek"),
    LASTWEEK("LastWeek"),
    THISMONTH("ThisMonth"),
    THISQUARTER("ThisQuarter");

    private final String value;

    TimeSpanType(String value) {
        this.value = value;
    }

    @JsonEnumValue
    @Override
    public String toString() {
        return value;
    }

    @JsonEnumCreator
    public static TimeSpanType fromString(String value) {
        for (TimeSpanType timeSpanType : TimeSpanType.values()) {
            if (timeSpanType.value.equals(value)) {
                return timeSpanType;
            }
        }
        return null;
    }

}
