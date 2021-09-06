package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.lang.reflect.Type;

public enum ResourceName implements JSONSerializable {
    ALERT_POLICY("sls.alert.alert_policy"),
    ACTION_POLICY("sls.alert.action_policy"),
    USER("sls.common.user"),
    USER_GROUP("sls.common.user_group"),
    CONTENT_TEMPLATE("sls.alert.content_template"),
    GLOBAL_CONFIG("sls.alert.global_config"),
    WEBHOOK_APPLICATION("sls.alert.webhook_application");

    private final String value;

    ResourceName(String value) {
        this.value = value;
    }

    public static ResourceName fromString(String value) {
        for (ResourceName type : ResourceName.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
        serializer.write(toString());
    }
}
