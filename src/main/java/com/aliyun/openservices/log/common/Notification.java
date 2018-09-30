package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

/**
 * The base class of notifications.
 */
public abstract class Notification {

    @JSONField
    private NotificationType type;

    @JSONField
    private String content;

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

    public void deserialize(final JSONObject value) {
        content = value.getString("content");
    }
}