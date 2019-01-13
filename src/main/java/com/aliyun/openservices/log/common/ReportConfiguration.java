package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Configuration for report job.
 */
public class ReportConfiguration extends DashboardBasedJobConfiguration {

    @JSONField
    private String dashboard;

    @JSONField
    private List<Notification> notificationList;

    @JSONField
    private boolean enableWatermark;

    @JSONField
    private boolean generatePublicAccessUrl;

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

    public boolean getEnableWatermark() {
        return enableWatermark;
    }

    public void setEnableWatermark(boolean enableWatermark) {
        this.enableWatermark = enableWatermark;
    }

    public boolean getGeneratePublicAccessUrl() {
        return generatePublicAccessUrl;
    }

    public void setGeneratePublicAccessUrl(boolean generatePublicAccessUrl) {
        this.generatePublicAccessUrl = generatePublicAccessUrl;
    }

    @Override
    Notification makeQualifiedNotification(NotificationType type) {
        switch (type) {
            case DING_TALK:
                return new DingTalkNotification();
            case EMAIL:
                return new EmailNotification();
            default:
                throw new IllegalArgumentException("Unimplemented report notification type: " + type);
        }
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        enableWatermark = JsonUtils.readBool(value, "enableWatermark", false);
        generatePublicAccessUrl = JsonUtils.readBool(value, "generatePublicAccessUrl", false);
    }
}
