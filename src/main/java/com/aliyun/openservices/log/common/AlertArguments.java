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
    private List<Notification> notifications;

    @JSONField
    private QueryContext queryContext;


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public QueryContext getQueryContext() {
        return queryContext;
    }

    public void setQueryContext(QueryContext queryContext) {
        this.queryContext = queryContext;
    }

    private static Notification createNotificationFromType(NotificationType type) {
        switch (type) {
            case Sms:
                return new SmsNotification();
            case Email:
                return new EmailNotification();
            case Webhook:
                return new WebhookNotification();
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void deserialize(final JSONObject value) {
        queryContext = new QueryContext();
        queryContext.deserialize(value.getJSONObject("queryContext"));
        condition = value.getString("condition");
        JSONArray notifications = value.getJSONArray("notifications");
        this.notifications = new ArrayList<Notification>(notifications.size());
        for (int i = 0; i < notifications.size(); i++) {
            JSONObject itemAsJson = notifications.getJSONObject(i);
            NotificationType notificationType = NotificationType.fromString(itemAsJson.getString("type"));
            Notification notification = createNotificationFromType(notificationType);
            notification.deserialize(itemAsJson);
            this.notifications.add(notification);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertArguments arguments = (AlertArguments) o;

        if (getCondition() != null ? !getCondition().equals(arguments.getCondition()) : arguments.getCondition() != null)
            return false;
        if (getNotifications() != null ? !getNotifications().equals(arguments.getNotifications()) : arguments.getNotifications() != null)
            return false;
        return getQueryContext() != null ? getQueryContext().equals(arguments.getQueryContext()) : arguments.getQueryContext() == null;
    }

    @Override
    public int hashCode() {
        int result = getCondition() != null ? getCondition().hashCode() : 0;
        result = 31 * result + (getNotifications() != null ? getNotifications().hashCode() : 0);
        result = 31 * result + (getQueryContext() != null ? getQueryContext().hashCode() : 0);
        return result;
    }
}
