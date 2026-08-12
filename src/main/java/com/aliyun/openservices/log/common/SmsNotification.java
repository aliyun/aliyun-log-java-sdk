package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.List;
import com.aliyun.openservices.log.annotation.InternalApi;

public class SmsNotification extends Notification {

    private List<String> mobileList;

    SmsNotification(NotificationType type) {
        super(type);
    }

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
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (mobileList != null) {
            value.put(Consts.MOBILE_LIST, JsonCodec.toJsonArray(mobileList));
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(final JSONObject value) {
        super.fromJsonObject(value);
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
