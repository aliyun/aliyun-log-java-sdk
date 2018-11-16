package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.http.client.HttpMethod;

/**
 * Send DingTalk notification.
 */
public class DingTalkNotification extends WebhookNotification {

    public DingTalkNotification() {
        super(NotificationType.DING_TALK);
        setMethod(HttpMethod.POST);
    }

}
