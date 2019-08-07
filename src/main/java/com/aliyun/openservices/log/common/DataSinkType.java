package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.lang.reflect.Type;

public enum DataSinkType implements JSONSerializable {
    ALIYUN_LOG("AliyunLOG"),
    ALIYUN_ADB("AliyunADB");

    private final String name;

    DataSinkType(String name) {
        this.name = name;
    }

    public static DataSinkType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (DataSinkType type : DataSinkType.values()) {
            if (type.name.equals(value)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
        serializer.write(name);
    }
}