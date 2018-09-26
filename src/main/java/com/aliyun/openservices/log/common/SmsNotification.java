package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONObject;

import java.util.List;

public class SmsNotification extends Notification {

    @JSONField
    private List<String> mobileList;

    public SmsNotification() {
        super(NotificationType.Sms);
    }

    public SmsNotification(String content, List<String> mobileList) {
        super(NotificationType.Sms, content);
        Args.notNullOrEmpty(mobileList, "mobileList");
        this.mobileList = mobileList;
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
        mobileList = JsonUtils.readList(value, "mobileList");
    }
}
