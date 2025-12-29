/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

/**
 * Enum for anonymous write status.
 */
public enum AnonymousWriteStatus {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private final String value;

    AnonymousWriteStatus(String value) {
        this.value = value;
    }

    /**
     * Get the string value.
     *
     * @return string value ("Enabled" or "Disabled")
     */
    public String getValue() {
        return value;
    }

    /**
     * Get AnonymousWriteStatus from string value.
     *
     * @param value string value ("Enabled" or "Disabled")
     * @return AnonymousWriteStatus enum value
     * @throws IllegalArgumentException if value is not "Enabled" or "Disabled"
     */
    public static AnonymousWriteStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (AnonymousWriteStatus status : AnonymousWriteStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown AnonymousWriteStatus value: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}

