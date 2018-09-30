package com.aliyun.openservices.log.common;

public enum NotificationType {
    /**
     * Ding ding web hook.
     */
    DingTalk,
    /**
     * Send email
     */
    Email,
    /**
     * Private message
     */
    Message,
    /**
     * Send SMS
     */
    SMS,
    /**
     * Send HTTP request to target uri
     */
    Webhook;

    public static NotificationType fromString(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + value);
    }
}
