/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

/**
 * Enum for multimodal configuration status.
 */
public enum MultimodalStatus {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private final String value;

    MultimodalStatus(String value) {
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
     * Get MultimodalStatus from string value.
     *
     * @param value string value ("Enabled" or "Disabled")
     * @return MultimodalStatus enum value
     * @throws IllegalArgumentException if value is not "Enabled" or "Disabled"
     */
    public static MultimodalStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (MultimodalStatus status : MultimodalStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown MultimodalStatus value: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}

