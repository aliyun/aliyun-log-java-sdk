package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

public class EncryptConf {
    private boolean enable = false;
    private String encrypt_type = "default";
    private EncryptUserCmkConf user_cmk_info = null;

    public EncryptConf() {
    }

    public EncryptConf(boolean enable) {
        this.enable = enable;
    }

    public EncryptConf(boolean enable, String encrypt_type) {
        this.enable = enable;
        this.encrypt_type = encrypt_type;
    }

    public EncryptConf(boolean enable, String encrypt_type, EncryptUserCmkConf user_cmk_info) {
        this.enable = enable;
        this.encrypt_type = encrypt_type;
        this.user_cmk_info = user_cmk_info;
    }

    public boolean getEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getEncryptType() {
        return this.encrypt_type;
    }

    public void setEncryptType(String encrypt_type) {
        this.encrypt_type = encrypt_type;
    }

    public EncryptUserCmkConf getUserCmkConf() {
        return this.user_cmk_info;
    }

    public void setUserCmkConf(EncryptUserCmkConf user_cmk_info) {
        this.user_cmk_info = user_cmk_info;
    }

    public JSONObject ToJsonObject() {
        JSONObject dict = new JSONObject();
        dict.put("enable", this.enable);
        dict.put("encrypt_type", this.encrypt_type);
        if (user_cmk_info != null) {
            dict.put("user_cmk_info", user_cmk_info.ToJsonObject());
        }
        return dict;

    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject dict) throws LogException {
        try {
            setEnable(dict.getBooleanValue("enable"));
            setEncryptType(dict.getString("encrypt_type"));
            if (dict.containsKey("user_cmk_info")) {
                EncryptUserCmkConf user_cmk_info = new EncryptUserCmkConf();
                user_cmk_info.FromJsonObject(dict.getJSONObject("user_cmk_info"));
                setUserCmkConf(user_cmk_info);
            }
        } catch (JSONException e) {
            throw new LogException("The Encrypt User config is invalid", e.getMessage(), e, "");
        }
    }

    public void FromJsonString(String logStoreString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(logStoreString);
            FromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("The Encrypt User config is invalid", e.getMessage(), e, "");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncryptConf that = (EncryptConf) o;

        if (enable != that.enable) return false;
        if (encrypt_type != null ? !encrypt_type.equals(that.encrypt_type) : that.encrypt_type != null) return false;
        return user_cmk_info != null ? user_cmk_info.equals(that.user_cmk_info) : that.user_cmk_info == null;
    }

    @Override
    public int hashCode() {
        int result = (enable ? 1 : 0);
        result = 31 * result + (encrypt_type != null ? encrypt_type.hashCode() : 0);
        result = 31 * result + (user_cmk_info != null ? user_cmk_info.hashCode() : 0);
        return result;
    }
}
