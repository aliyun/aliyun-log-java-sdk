package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import com.alibaba.fastjson.JSONObject;


public class ReportConfiguration extends DashboardBasedJobConfiguration {

    /**
     * Whether add watermark on image, default to false.
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

    @JSONField
    private boolean customizePeriod;

    /**
     * Must be specified if customizePeriod is true.
     */
    @JSONField
    private TimeSpan period;

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

    public boolean getCustomizePeriod() {
        return customizePeriod;
    }

    public void setCustomizePeriod(boolean customizePeriod) {
        this.customizePeriod = customizePeriod;
    }

    public TimeSpan getPeriod() {
        return period;
    }

    public void setPeriod(TimeSpan period) {
        this.period = period;
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
        customizePeriod = JsonUtils.readBool(value, "customizePeriod", false);
        if (customizePeriod) {
            period = new TimeSpan();
            period.deserialize(value.getJSONObject("period"));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportConfiguration that = (ReportConfiguration) o;

        if (getEnableWatermark() != that.getEnableWatermark()) return false;
        if (getAllowAnonymousAccess() != that.getAllowAnonymousAccess()) return false;
        if (getCustomizePeriod() != that.getCustomizePeriod()) return false;
        if (getLanguage() != null ? !getLanguage().equals(that.getLanguage()) : that.getLanguage() != null)
            return false;
        return getPeriod() != null ? getPeriod().equals(that.getPeriod()) : that.getPeriod() == null;
    }

    @Override
    public int hashCode() {
        int result = (getEnableWatermark() ? 1 : 0);
        result = 31 * result + (getAllowAnonymousAccess() ? 1 : 0);
        result = 31 * result + (getLanguage() != null ? getLanguage().hashCode() : 0);
        result = 31 * result + (getCustomizePeriod() ? 1 : 0);
        result = 31 * result + (getPeriod() != null ? getPeriod().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReportConfiguration{" +
                "enableWatermark=" + enableWatermark +
                ", allowAnonymousAccess=" + allowAnonymousAccess +
                ", language='" + language + '\'' +
                ", customizePeriod=" + customizePeriod +
                ", period=" + period +
                '}';
    }
}
