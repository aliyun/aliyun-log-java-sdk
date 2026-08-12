package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.List;
import com.aliyun.openservices.log.annotation.InternalApi;

public class EmailNotification extends Notification {

    private String subject;

    private String countryCode;

    private List<String> emailList;

    public EmailNotification() {
        super(NotificationType.EMAIL);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (subject != null) {
            value.put(Consts.SUBJECT, subject);
        }
        if (countryCode != null) {
            value.put("countryCode", countryCode);
        }
        if (emailList != null) {
            value.put(Consts.EMAIL_LIST, JsonCodec.toJsonArray(emailList));
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(final JSONObject value) {
        super.fromJsonObject(value);
        subject = JsonUtils.readOptionalString(value, Consts.SUBJECT);
        emailList = JsonUtils.readStringList(value, Consts.EMAIL_LIST);
        countryCode = JsonUtils.readOptionalString(value, "countryCode");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EmailNotification that = (EmailNotification) o;

        if (getSubject() != null ? !getSubject().equals(that.getSubject()) : that.getSubject() != null) {
            return false;
        }
        if (getCountryCode() != null ? !getCountryCode().equals(that.getCountryCode()) : that.getCountryCode() != null) {
            return false;
        }
        return getEmailList() != null ? getEmailList().equals(that.getEmailList()) : that.getEmailList() == null;
    }

    @Override
    public int hashCode() {
        int result = getSubject() != null ? getSubject().hashCode() : 0;
        result = 31 * result + (getCountryCode() != null ? getCountryCode().hashCode() : 0);
        result = 31 * result + (getEmailList() != null ? getEmailList().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EmailNotification{" +
                "subject='" + subject + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", emailList=" + emailList +
                '}';
    }
}
