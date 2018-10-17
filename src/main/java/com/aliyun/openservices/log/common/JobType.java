package com.aliyun.openservices.log.common;

/**
 * Job type.
 */
public enum JobType {
    ALERT("Alert");

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
        throw new IllegalArgumentException("Illegal job type: " + value);
    }
}