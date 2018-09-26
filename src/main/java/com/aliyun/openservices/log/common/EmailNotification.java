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

    @JSONField
    private String dashboard;

    public EmailNotification() {
        super(NotificationType.Email);
    }

    public EmailNotification(String content, List<String> emailList) {
        super(NotificationType.Email, content);
        this.emailList = emailList;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDashboard() {
        return dashboard;
    }

    public void setDashboard(String dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public void deserialize(final JSONObject value) {
        super.deserialize(value);
        dashboard = value.getString("dashboard");
        subject = value.getString("subject");
        emailList = JsonUtils.readList(value, "emailList");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailNotification that = (EmailNotification) o;

        if (getSubject() != null ? !getSubject().equals(that.getSubject()) : that.getSubject() != null) return false;
        if (getEmailList() != null ? !getEmailList().equals(that.getEmailList()) : that.getEmailList() != null)
            return false;
        return getDashboard() != null ? getDashboard().equals(that.getDashboard()) : that.getDashboard() == null;
    }

    @Override
    public int hashCode() {
        int result = getSubject() != null ? getSubject().hashCode() : 0;
        result = 31 * result + (getEmailList() != null ? getEmailList().hashCode() : 0);
        result = 31 * result + (getDashboard() != null ? getDashboard().hashCode() : 0);
        return result;
    }
}
