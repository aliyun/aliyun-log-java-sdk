package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JsonEnumAdapter;
import com.aliyun.openservices.log.internal.json.JsonEnumCreator;
import com.aliyun.openservices.log.internal.json.JsonEnumValue;

@JsonEnumAdapter
public enum DataSinkType {
    ALIYUN_LOG("AliyunLOG"),
    ALIYUN_ADB("AliyunADB"),
    ALIYUN_TSDB("AliyunTSDB"),
    ALIYUN_OSS("AliyunOSS"),
    ALIYUN_OSSHDFS("AliyunOSSHDFS"),
    ALIYUN_ODPS("AliyunODPS"),
    GENERAL("General");

    private final String name;

    DataSinkType(String name) {
        this.name = name;
    }

    @JsonEnumCreator
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

    @JsonEnumValue
    @Override
    public String toString() {
        return name;
    }

}
