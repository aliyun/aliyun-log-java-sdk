package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.http.client.HttpMethod;

/**
 * Send DingTalk notification.
 */
public class DingTalkNotification extends WebhookNotification {

    public DingTalkNotification() {
        this(null, null);
    }

    public DingTalkNotification(String content, String serviceUri) {
        super(NotificationType.DING_TALK, content, HttpMethod.POST, serviceUri);
    }
}
