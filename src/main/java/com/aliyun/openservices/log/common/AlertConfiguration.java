package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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

    @JSONField
    private Date muteUntil;

    @JSONField
    private int notifyThreshold = 1;

    /**
     * Duration with format '1h', '2s'
     */
    @JSONField
    private String throttling;

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

    public Date getMuteUntil() {
        return muteUntil;
    }

    public void setMuteUntil(Date muteUntil) {
        this.muteUntil = muteUntil;
    }

    public int getNotifyThreshold() {
        return notifyThreshold;
    }

    public void setNotifyThreshold(int notifyThreshold) {
        this.notifyThreshold = notifyThreshold;
    }

    public String getThrottling() {
        return throttling;
    }

    public void setThrottling(String throttling) {
        this.throttling = throttling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertConfiguration that = (AlertConfiguration) o;

        if (getNotifyThreshold() != that.getNotifyThreshold()) return false;
        if (getCondition() != null ? !getCondition().equals(that.getCondition()) : that.getCondition() != null)
            return false;
        if (getDashboard() != null ? !getDashboard().equals(that.getDashboard()) : that.getDashboard() != null)
            return false;
        if (getQueryList() != null ? !getQueryList().equals(that.getQueryList()) : that.getQueryList() != null)
            return false;
        if (getNotificationList() != null ? !getNotificationList().equals(that.getNotificationList()) : that.getNotificationList() != null)
            return false;
        if (getMuteUntil() != null ? !getMuteUntil().equals(that.getMuteUntil()) : that.getMuteUntil() != null)
            return false;
        return getThrottling() != null ? getThrottling().equals(that.getThrottling()) : that.getThrottling() == null;
    }

    @Override
    public int hashCode() {
        int result = getCondition() != null ? getCondition().hashCode() : 0;
        result = 31 * result + (getDashboard() != null ? getDashboard().hashCode() : 0);
        result = 31 * result + (getQueryList() != null ? getQueryList().hashCode() : 0);
        result = 31 * result + (getNotificationList() != null ? getNotificationList().hashCode() : 0);
        result = 31 * result + (getMuteUntil() != null ? getMuteUntil().hashCode() : 0);
        result = 31 * result + getNotifyThreshold();
        result = 31 * result + (getThrottling() != null ? getThrottling().hashCode() : 0);
        return result;
    }

    @Override
    public void deserialize(JSONObject value) {
        condition = value.getString("condition");
        dashboard = value.getString("dashboard");
        final JSONArray queries = value.getJSONArray("queryList");
        queryList = new ArrayList<Query>(queries.size());
        for (int i = 0; i < queries.size(); i++) {
            Query query = new Query();
            query.deserialize(queries.getJSONObject(i));
            queryList.add(query);
        }
        final JSONArray notifications = value.getJSONArray("notificationList");
        notificationList = new ArrayList<Notification>(notifications.size());
        for (int i = 0; i < notifications.size(); i++) {
            JSONObject itemAsJson = notifications.getJSONObject(i);
            NotificationType notificationType = NotificationType.fromString(itemAsJson.getString("type"));
            Notification notification = createNotification(notificationType);
            notification.deserialize(itemAsJson);
            notificationList.add(notification);
        }
        if (value.containsKey("muteUntil")) {
            muteUntil = new Date(value.getLong("muteUntil"));
        }
        if (value.containsKey("notifyThreshold")) {
            notifyThreshold = value.getInt("notifyThreshold");
        }
        if (value.containsKey("throttling")) {
            throttling = value.getString("throttling");
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
