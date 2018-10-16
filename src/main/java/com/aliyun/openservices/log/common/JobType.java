package com.aliyun.openservices.log.common;

public enum JobType {
    Alert;

    @Override
    public String toString() {
        return name();
    }

    public static JobType fromString(String value) {
        for (JobType type : JobType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown job type: " + value);
    }
}