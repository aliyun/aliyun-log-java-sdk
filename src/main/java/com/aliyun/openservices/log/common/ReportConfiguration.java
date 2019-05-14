package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

/**
 * Configuration for report.
 */
public class ReportConfiguration extends DashboardBasedJobConfiguration {

    /**
     * Whether add watermark on image, default to true.
     */
    @JSONField
    private boolean enableWatermark;

    /**
     * Whether create a public access url for dashboard, default to false.
     */
    @JSONField
    private boolean allowAnonymousAccess;

    /**
     * Optional language for internationalization. Defaults as zh.
     */
    @JSONField
    private String language;

    public boolean getEnableWatermark() {
        return enableWatermark;
    }

    public void setEnableWatermark(boolean enableWatermark) {
        this.enableWatermark = enableWatermark;
    }

    public boolean getAllowAnonymousAccess() {
        return allowAnonymousAccess;
    }

    public void setAllowAnonymousAccess(boolean allowAnonymousAccess) {
        this.allowAnonymousAccess = allowAnonymousAccess;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
        allowAnonymousAccess = JsonUtils.readBool(value, "allowAnonymousAccess", false);
        language = JsonUtils.readOptionalString(value, "language");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportConfiguration that = (ReportConfiguration) o;

        if (getEnableWatermark() != that.getEnableWatermark()) return false;
        if (getAllowAnonymousAccess() != that.getAllowAnonymousAccess()) return false;
        return getLanguage() != null ? getLanguage().equals(that.getLanguage()) : that.getLanguage() == null;
    }

    @Override
    public int hashCode() {
        int result = (getEnableWatermark() ? 1 : 0);
        result = 31 * result + (getAllowAnonymousAccess() ? 1 : 0);
        result = 31 * result + (getLanguage() != null ? getLanguage().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReportConfiguration{" +
                "enableWatermark=" + enableWatermark +
                ", allowAnonymousAccess=" + allowAnonymousAccess +
                ", language='" + language + '\'' +
                '}';
    }
}
