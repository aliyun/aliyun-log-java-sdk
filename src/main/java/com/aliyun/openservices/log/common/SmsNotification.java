package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.Args;
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

    public SmsNotification(String content, String countryCode, List<String> mobileList) {
        super(NotificationType.SMS, content);
        Args.notNullOrEmpty(countryCode, "countryCode");
        Args.notNullOrEmpty(mobileList, Consts.MOBILE_LIST);
        this.mobileList = mobileList;
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
        mobileList = JsonUtils.readList(value, Consts.MOBILE_LIST);
    }
}
