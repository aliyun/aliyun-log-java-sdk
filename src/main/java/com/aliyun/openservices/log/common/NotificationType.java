package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.lang.reflect.Type;

public enum NotificationType implements JSONSerializable {
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

    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Type type, int i) {
        jsonSerializer.write(toString());
    }
}
