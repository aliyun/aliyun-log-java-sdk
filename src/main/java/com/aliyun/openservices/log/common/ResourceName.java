package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JsonEnumAdapter;
import com.aliyun.openservices.log.internal.json.JsonEnumCreator;
import com.aliyun.openservices.log.internal.json.JsonEnumValue;

@JsonEnumAdapter
public enum ResourceName {
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

    @JsonEnumCreator
    public static ResourceName fromString(String value) {
        for (ResourceName type : ResourceName.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    @JsonEnumValue
    @Override
    public String toString() {
        return value;
    }

}
