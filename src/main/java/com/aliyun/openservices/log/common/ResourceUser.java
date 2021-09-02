package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

public class ResourceUser implements Serializable {
    @JSONField(name = "user_id")
    private String userId;
    @JSONField(name = "user_name")
    private String userName;
    @JSONField(name = "enabled")
    private boolean enabled;
    @JSONField(name = "country_code")
    private String countryCode;
    @JSONField(name = "phone")
    private String phone;
    @JSONField(name = "email")
    private List<String> email;
    @JSONField(name = "sms_enabled")
    private boolean smsEnabled;
    @JSONField(name = "voice_enabled")
    private boolean voiceEnabled;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public boolean isVoiceEnabled() {
        return voiceEnabled;
    }

    public void setVoiceEnabled(boolean voiceEnabled) {
        this.voiceEnabled = voiceEnabled;
    }
}
