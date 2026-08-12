package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JsonEnumAdapter;
import com.aliyun.openservices.log.internal.json.JsonEnumCreator;
import com.aliyun.openservices.log.internal.json.JsonEnumValue;

@JsonEnumAdapter
public enum JobType {
    ALERT("Alert"),
    REPORT("Report"),
    ETL("ETL"),
    INGESTION("Ingestion"),
    REBUILD_INDEX("RebuildIndex"),
    AUDIT_JOB("AuditJob"),
    EXPORT("Export"),
    SCHEDULED_SQL("ScheduledSQL"),

    DOWN_SAMPLING("DownSampling");

    private final String value;

    JobType(String value) {
        this.value = value;
    }

    @JsonEnumValue
    @Override
    public String toString() {
        return value;
    }

    @JsonEnumCreator
    public static JobType fromString(String value) {
        for (JobType type : JobType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

}
