package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

/**
 * The base class of notifications.
 */
public abstract class Notification implements JsonDeserializable {

    private NotificationType type;

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

    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = new JSONObject();
        if (type != null) {
            value.put("type", type.toString());
        }
        if (content != null) {
            value.put("content", content);
        }
        return value;
    }

    @InternalApi
    public void fromJsonObject(final JSONObject value) {
        NotificationType parsedType = NotificationType.fromString(value.getString("type"));
        if (parsedType != null) {
            type = parsedType;
        }
        content = value.getString("content");
    }
}
