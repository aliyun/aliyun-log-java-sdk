package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.List;


abstract class DashboardBasedJobConfiguration extends JobConfiguration {

    private String dashboard;

    private List<Notification> notificationList;

    public String getDashboard() {
        return dashboard;
    }

    public void setDashboard(String dashboard) {
        this.dashboard = dashboard;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    abstract Notification makeQualifiedNotification(NotificationType type);

    @Override
    public void deserialize(JSONObject value) {
        dashboard = value.getString("dashboard");
        notificationList = JsonUtils.readList(value, "notificationList", new Unmarshaller<Notification>() {
            @Override
            public Notification unmarshal(JSONArray value, int index) {
                JSONObject item = value.getJSONObject(index);
                NotificationType notificationType = NotificationType.fromString(item.getString("type"));
                if (notificationType == null) {
                    // For bwc
                    return null;
                }
                Notification notification = makeQualifiedNotification(notificationType);
                notification.deserialize(item);
                return notification;
            }
        });
    }
}
