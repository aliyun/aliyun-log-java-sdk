package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
    private Integer notifyThreshold = 1;

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
        Args.checkDuration(throttling);
        this.throttling = throttling;
    }

    @Override
    public void deserialize(JSONObject value) {
        condition = value.getString("condition");
        dashboard = value.getString("dashboard");
        queryList = JsonUtils.readList(value, "queryList", new Unmarshaller<Query>() {
            @Override
            public Query unmarshal(JSONArray value, int index) {
                Query query = new Query();
                query.deserialize(value.getJSONObject(index));
                return query;
            }
        });
        notificationList = JsonUtils.readList(value, "notificationList", new Unmarshaller<Notification>() {
            @Override
            public Notification unmarshal(JSONArray value, int index) {
                JSONObject item = value.getJSONObject(index);
                NotificationType notificationType = NotificationType.fromString(item.getString("type"));
                Notification notification = createNotification(notificationType);
                notification.deserialize(item);
                return notification;
            }
        });
        if (value.has("muteUntil")) {
            muteUntil = Utils.timestampToDate(value.getLong("muteUntil"));
        }
        if (value.has("notifyThreshold")) {
            notifyThreshold = value.getInt("notifyThreshold");
        }
        if (value.has("throttling")) {
            throttling = value.getString("throttling");
        }
    }

    private static Notification createNotification(NotificationType type) {
        switch (type) {
            case DING_TALK:
                return new DingTalkNotification();
            case EMAIL:
                return new EmailNotification();
            case MESSAGE_CENTER:
                return new MessageCenterNotification();
            case SMS:
                return new SmsNotification();
            case WEBHOOK:
                return new WebhookNotification();
            default:
                throw new IllegalArgumentException("Unimplemented notification type: " + type);
        }
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
}
