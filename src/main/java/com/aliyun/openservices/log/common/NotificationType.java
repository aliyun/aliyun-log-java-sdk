package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JsonEnumAdapter;
import com.aliyun.openservices.log.internal.json.JsonEnumCreator;
import com.aliyun.openservices.log.internal.json.JsonEnumValue;

@JsonEnumAdapter
public enum NotificationType {
    /**
     * Ding ding web hook.
     */
    DING_TALK("DingTalk"),
    /**
     * Send email.
     */
    EMAIL("Email"),
    /**
     * Send message to message center.
     */
    MESSAGE_CENTER("MessageCenter"),
    /**
     * Send SMS.
     */
    SMS("SMS"),
    /**
     * Send HTTP request to target uri.
     */
    WEBHOOK("Webhook"),
    /**
     * Send voice to phone number.
     */
    VOICE("Voice");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    @JsonEnumCreator
    public static NotificationType fromString(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    @JsonEnumValue
    @Override
    public String toString() {
        return value;
    }

}
