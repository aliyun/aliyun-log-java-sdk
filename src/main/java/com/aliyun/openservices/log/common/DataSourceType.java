package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JsonEnumAdapter;
import com.aliyun.openservices.log.internal.json.JsonEnumCreator;
import com.aliyun.openservices.log.internal.json.JsonEnumValue;

@JsonEnumAdapter
public enum DataSourceType {
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

    @JsonEnumCreator
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

    @JsonEnumValue
    @Override
    public String toString() {
        return name;
    }

}
