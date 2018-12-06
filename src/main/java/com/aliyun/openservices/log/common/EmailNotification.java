package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.util.List;

public class EmailNotification extends Notification {

    @JSONField
    private List<String> emailList;

    public EmailNotification() {
        super(NotificationType.EMAIL);
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
        emailList = JsonUtils.readStringList(value, Consts.EMAIL_LIST);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailNotification that = (EmailNotification) o;

        return getEmailList() != null ? getEmailList().equals(that.getEmailList()) : that.getEmailList() == null;
    }

    @Override
    public int hashCode() {
        return getEmailList() != null ? getEmailList().hashCode() : 0;
    }
}
