package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.util.List;

public class EmailNotification extends Notification {

    @JSONField
    private String subject;

    @JSONField
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

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    @Override
    public void deserialize(final JSONObject value) {
        super.deserialize(value);
        subject = JsonUtils.readOptionalString(value, Consts.SUBJECT);
        emailList = JsonUtils.readStringList(value, Consts.EMAIL_LIST);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailNotification that = (EmailNotification) o;

        if (getSubject() != null ? !getSubject().equals(that.getSubject()) : that.getSubject() != null) return false;
        return getEmailList() != null ? getEmailList().equals(that.getEmailList()) : that.getEmailList() == null;
    }

    @Override
    public int hashCode() {
        int result = getSubject() != null ? getSubject().hashCode() : 0;
        result = 31 * result + (getEmailList() != null ? getEmailList().hashCode() : 0);
        return result;
    }
}
