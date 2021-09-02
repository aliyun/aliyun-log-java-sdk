package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


public class ResourceContentTemplate implements Serializable {
    @JSONField(name = "template_id")
    private String templateId;
    @JSONField(name = "template_name")
    private String templateName;
    @JSONField(name = "is_default")
    private boolean isDefault;
    @JSONField(name = "templates")
    private Templates templates;

    public static class Template {
        @JSONField(name = "content")
        private String content;
        @JSONField(name = "locale")
        private String locale;
        @JSONField(name = "title")
        private String title;
        @JSONField(name = "subject")
        private String subject;
        @JSONField(name = "send_type")
        private String sendType;
        @JSONField(name = "limit")
        private int limit;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getSendType() {
            return sendType;
        }

        public void setSendType(String sendType) {
            this.sendType = sendType;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }

    public static class Templates {
        @JSONField(name = "sms")
        private Template sms;
        @JSONField(name = "voice")
        private Template voice;
        @JSONField(name = "email")
        private Template email;
        @JSONField(name = "dingtalk")
        private Template dingtalk;
        @JSONField(name = "webhook")
        private Template webhook;
        @JSONField(name = "message_center")
        private Template messageCenter;
        @JSONField(name = "wechat")
        private Template wechat;
        @JSONField(name = "lark")
        private Template lark;
        @JSONField(name = "slack")
        private Template slack;

        public Template getSms() {
            return sms;
        }

        public void setSms(Template sms) {
            this.sms = sms;
        }

        public Template getVoice() {
            return voice;
        }

        public void setVoice(Template voice) {
            this.voice = voice;
        }

        public Template getEmail() {
            return email;
        }

        public void setEmail(Template email) {
            this.email = email;
        }

        public Template getDingtalk() {
            return dingtalk;
        }

        public void setDingtalk(Template dingtalk) {
            this.dingtalk = dingtalk;
        }

        public Template getWebhook() {
            return webhook;
        }

        public void setWebhook(Template webhook) {
            this.webhook = webhook;
        }

        public Template getMessageCenter() {
            return messageCenter;
        }

        public void setMessageCenter(Template messageCenter) {
            this.messageCenter = messageCenter;
        }

        public Template getWechat() {
            return wechat;
        }

        public void setWechat(Template wechat) {
            this.wechat = wechat;
        }

        public Template getLark() {
            return lark;
        }

        public void setLark(Template lark) {
            this.lark = lark;
        }

        public Template getSlack() {
            return slack;
        }

        public void setSlack(Template slack) {
            this.slack = slack;
        }


    }


}
