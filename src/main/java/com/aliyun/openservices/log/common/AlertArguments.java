package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.common.Notification.NotificationType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AlertArguments extends JobArguments {

    /**
     * The trigger condition expression e.g $0.xx > 100 and $1.yy < 100.
     * Which depends on the order of queries in query context.
     */
    @JSONField
    private String condition;

    @JSONField
    private AlertResource resource;

    @JSONField
    private List<Notification> notificationList;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public AlertResource getResource() {
        return resource;
    }

    public void setResource(AlertResource resource) {
        this.resource = resource;
    }

    private static Notification createNotificationFromType(NotificationType type) {
        switch (type) {
            case Sms:
                return new SmsNotification();
            case Email:
                return new EmailNotification();
            case Webhook:
                return new WebhookNotification();
            case DingTalk:
                return new DingTalkNotification();
            case Message:
                return new MessageNotification();
            default:
                throw new IllegalArgumentException("Illegal notification type: " + type);
        }
    }

    @Override
    public void deserialize(final JSONObject value) {
        resource = new AlertResource();
        resource.deserialize(value.getJSONObject("resource"));
        condition = value.getString("condition");
        JSONArray notifications = value.getJSONArray("notificationList");
        notificationList = new ArrayList<Notification>(notifications.size());
        for (int i = 0; i < notifications.size(); i++) {
            JSONObject itemAsJson = notifications.getJSONObject(i);
            NotificationType notificationType = NotificationType.fromString(itemAsJson.getString("type"));
            Notification notification = createNotificationFromType(notificationType);
            notification.deserialize(itemAsJson);
            notificationList.add(notification);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertArguments arguments = (AlertArguments) o;

        if (getCondition() != null ? !getCondition().equals(arguments.getCondition()) : arguments.getCondition() != null)
            return false;
        if (getNotificationList() != null ? !getNotificationList().equals(arguments.getNotificationList()) : arguments.getNotificationList() != null)
            return false;
        return getResource() != null ? getResource().equals(arguments.getResource()) : arguments.getResource() == null;
    }

    @Override
    public int hashCode() {
        int result = getCondition() != null ? getCondition().hashCode() : 0;
        result = 31 * result + (getNotificationList() != null ? getNotificationList().hashCode() : 0);
        result = 31 * result + (getResource() != null ? getResource().hashCode() : 0);
        return result;
    }
}
