package com.aliyun.openservices.log.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ResourceContentTemplate implements Serializable {
    @SerializedName("template_id")
    private String templateId;
    @SerializedName("template_name")
    private String templateName;
    @SerializedName("is_default")
    private boolean isDefault;
    @SerializedName("templates")
    private Templates templates;

    public static class Template {
        @SerializedName("content")
        private String content;
        @SerializedName("locale")
        private String locale;
        @SerializedName("title")
        private String title;
        @SerializedName("subject")
        private String subject;
        @SerializedName("send_type")
        private String sendType;
        @SerializedName("limit")
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
        @SerializedName("sms")
        private Template sms;
        @SerializedName("voice")
        private Template voice;
        @SerializedName("email")
        private Template email;
        @SerializedName("dingtalk")
        private Template dingtalk;
        @SerializedName("webhook")
        private Template webhook;
        @SerializedName("message_center")
        private Template messageCenter;
        @SerializedName("wechat")
        private Template wechat;
        @SerializedName("lark")
        private Template lark;
        @SerializedName("slack")
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
