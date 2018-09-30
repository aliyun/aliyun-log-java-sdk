package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlertConfiguration extends JobConfiguration {

    /**
     * The trigger condition expression e.g $0.xx > 100 and $1.yy < 100.
     * Which depends on the order of queries in query context.
     */
    @JSONField
    private String condition;

    @JSONField
    private String dashboard;

    @JSONField
    private List<Query> queryList;

    @JSONField
    private List<Notification> notificationList;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDashboard() {
        return dashboard;
    }

    public void setDashboard(String dashboard) {
        this.dashboard = dashboard;
    }

    public List<Query> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<Query> queryList) {
        this.queryList = queryList;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertConfiguration that = (AlertConfiguration) o;

        if (getCondition() != null ? !getCondition().equals(that.getCondition()) : that.getCondition() != null)
            return false;
        if (getDashboard() != null ? !getDashboard().equals(that.getDashboard()) : that.getDashboard() != null)
            return false;
        if (getQueryList() != null ? !getQueryList().equals(that.getQueryList()) : that.getQueryList() != null)
            return false;
        return getNotificationList() != null ? getNotificationList().equals(that.getNotificationList()) : that.getNotificationList() == null;
    }

    @Override
    public int hashCode() {
        int result = getCondition() != null ? getCondition().hashCode() : 0;
        result = 31 * result + (getDashboard() != null ? getDashboard().hashCode() : 0);
        result = 31 * result + (getQueryList() != null ? getQueryList().hashCode() : 0);
        result = 31 * result + (getNotificationList() != null ? getNotificationList().hashCode() : 0);
        return result;
    }

    @Override
    public void deserialize(JSONObject value) {
        condition = value.getString("condition");
        dashboard = value.getString("dashboard");
        JSONArray queries = value.getJSONArray("queryList");
        queryList = new ArrayList<Query>(queries.size());
        for (int i = 0; i < queries.size(); i++) {
            Query query = new Query();
            query.deserialize(queries.getJSONObject(i));
            queryList.add(query);
        }
        JSONArray notifications = value.getJSONArray("notificationList");
        notificationList = new ArrayList<Notification>(notifications.size());
        for (int i = 0; i < notifications.size(); i++) {
            JSONObject itemAsJson = notifications.getJSONObject(i);
            NotificationType notificationType = NotificationType.fromString(itemAsJson.getString("type"));
            Notification notification = createNotification(notificationType);
            notification.deserialize(itemAsJson);
            notificationList.add(notification);
        }
    }

    private static Notification createNotification(NotificationType type) {
        switch (type) {
            case DingTalk:
                return new DingTalkNotification();
            case Email:
                return new EmailNotification();
            case Message:
                return new MessageNotification();
            case SMS:
                return new SmsNotification();
            case Webhook:
                return new WebhookNotification();
            default:
                throw new IllegalArgumentException("Unimplemented notification type: " + type);
        }
    }
}
