package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

public abstract class Notification {

    @JSONField
    private NotificationType type;

    @JSONField
    private String content;

    public Notification() {
    }

    public Notification(NotificationType type) {
        this.type = type;
    }

    public Notification(NotificationType type, String content) {
        this.type = type;
        this.content = content;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public enum NotificationType {
        DingTalk,
        Email,
        /**
         * Private message
         */
        Message,
        Sms,
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

    public void deserialize(final JSONObject value) {
        content = value.getString("content");
    }
}