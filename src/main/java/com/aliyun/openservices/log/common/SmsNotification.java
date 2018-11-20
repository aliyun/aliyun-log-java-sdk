package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.util.List;

public class SmsNotification extends Notification {

    @JSONField
    private List<String> mobileList;

    public SmsNotification() {
        super(NotificationType.SMS);
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

        return getMobileList() != null ? getMobileList().equals(that.getMobileList()) : that.getMobileList() == null;
    }

    @Override
    public int hashCode() {
        return getMobileList() != null ? getMobileList().hashCode() : 0;
    }
}
