package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.lang.reflect.Type;

public enum DataSourceType implements JSONSerializable {
    ALIYUN_OSS("AliyunOSS"),
    ALIYUN_BSS("AliyunBSS"),
    ALIYUN_MAX_COMPUTE("AliyunMaxCompute"),
    JDBC("JDBC"),
    KAFKA("Kafka"),
    ALIYUN_CLOUD_MONITOR("AliyunCloudMonitor"),
    GENERAL("General");

    private final String name;

    DataSourceType(String name) {
        this.name = name;
    }

    public static DataSourceType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (DataSourceType type : DataSourceType.values()) {
            if (type.name.equals(value)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
        serializer.write(name);
    }
}
