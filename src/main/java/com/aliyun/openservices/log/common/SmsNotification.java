package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.util.List;

public class SmsNotification extends Notification {

    @JSONField
    private String countryCode;

    @JSONField
    private List<String> mobileList;

    public SmsNotification() {
        super(NotificationType.SMS);
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<String> getMobileList() {
        return mobileList;
    }

    public void setMobileList(List<String> mobileList) {
        this.mobileList = mobileList;
    }

    @Override
    public void deserialize(final JSONObject value) {
        super.deserialize(value);
        mobileList = JsonUtils.readStringList(value, Consts.MOBILE_LIST);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsNotification that = (SmsNotification) o;

        if (getCountryCode() != null ? !getCountryCode().equals(that.getCountryCode()) : that.getCountryCode() != null)
            return false;
        return getMobileList() != null ? getMobileList().equals(that.getMobileList()) : that.getMobileList() == null;
    }

    @Override
    public int hashCode() {
        int result = getCountryCode() != null ? getCountryCode().hashCode() : 0;
        result = 31 * result + (getMobileList() != null ? getMobileList().hashCode() : 0);
        return result;
    }
}
