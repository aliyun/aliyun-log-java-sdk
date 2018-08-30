package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

public class EmailNotification extends Notification {

    @JSONField
    private String emailAddress;

    @JSONField
    private String subject;

    @JSONField
    private String dashboard;

    public EmailNotification() {
        // For JSON deserialization
        super(NotificationType.Email);
    }

    public EmailNotification(String content, String emailAddress) {
        super(NotificationType.Email, content);
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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
        emailAddress = value.getString("emailAddress");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailNotification that = (EmailNotification) o;

        if (getEmailAddress() != null ? !getEmailAddress().equals(that.getEmailAddress()) : that.getEmailAddress() != null)
            return false;
        if (getSubject() != null ? !getSubject().equals(that.getSubject()) : that.getSubject() != null) return false;
        return getDashboard() != null ? getDashboard().equals(that.getDashboard()) : that.getDashboard() == null;
    }

    @Override
    public int hashCode() {
        int result = getEmailAddress() != null ? getEmailAddress().hashCode() : 0;
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        result = 31 * result + (getDashboard() != null ? getDashboard().hashCode() : 0);
        return result;
    }
}
