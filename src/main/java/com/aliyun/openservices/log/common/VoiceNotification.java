package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

public class VoiceNotification extends Notification {

    @JSONField
    private String mobile;

    public VoiceNotification() {
        super(NotificationType.VOICE);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        mobile = value.getString("mobile");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoiceNotification that = (VoiceNotification) o;

        return getMobile() != null ? getMobile().equals(that.getMobile()) : that.getMobile() == null;
    }

    @Override
    public int hashCode() {
        return getMobile() != null ? getMobile().hashCode() : 0;
    }
}
