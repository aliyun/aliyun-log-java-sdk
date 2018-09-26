package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.http.client.HttpMethod;

/**
 * Send DingTalk notification.
 */
public class DingTalkNotification extends WebhookNotification {

    public DingTalkNotification() {
        super(NotificationType.DingTalk, null, HttpMethod.POST, null);
    }

    public DingTalkNotification(String content, String serviceUri) {
        super(NotificationType.DingTalk, content, HttpMethod.POST, serviceUri);
    }
}
