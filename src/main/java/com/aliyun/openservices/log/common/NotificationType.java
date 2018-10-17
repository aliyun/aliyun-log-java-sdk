package com.aliyun.openservices.log.common;

public enum NotificationType {
    /**
     * Ding ding web hook.
     */
    DING_TALK("DingTalk"),
    /**
     * Send email
     */
    EMAIL("Email"),
    /**
     * Private message
     */
    MESSAGE("Message"),
    /**
     * Send SMS
     */
    SMS("SMS"),
    /**
     * Send HTTP request to target uri
     */
    WEBHOOK("Webhook");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public static NotificationType fromString(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
