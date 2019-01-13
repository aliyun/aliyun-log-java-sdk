package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

/**
 * Configuration for report job.
 */
public class ReportConfiguration extends DashboardBasedJobConfiguration {

    @JSONField
    private boolean enableWatermark;

    @JSONField
    private boolean createPublicAccessUrl;

    public boolean getEnableWatermark() {
        return enableWatermark;
    }

    public void setEnableWatermark(boolean enableWatermark) {
        this.enableWatermark = enableWatermark;
    }

    public boolean getCreatePublicAccessUrl() {
        return createPublicAccessUrl;
    }

    public void setCreatePublicAccessUrl(boolean createPublicAccessUrl) {
        this.createPublicAccessUrl = createPublicAccessUrl;
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
        createPublicAccessUrl = JsonUtils.readBool(value, "createPublicAccessUrl", false);
    }
}
