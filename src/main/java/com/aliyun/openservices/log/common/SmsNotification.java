package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.Args;
import net.sf.json.JSONObject;

public class SmsNotification extends Notification {

    @JSONField
    private String phoneNumber;

    public SmsNotification() {
        super(NotificationType.Sms);
    }

    public SmsNotification(String content, String phoneNumber) {
        super(NotificationType.Sms, content);
        Args.notNullOrEmpty(phoneNumber, "phoneNumber");
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void deserialize(final JSONObject value) {
        super.deserialize(value);
        phoneNumber = value.getString("phoneNumber");
    }
}
