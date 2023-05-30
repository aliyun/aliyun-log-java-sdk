package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;

@JSONType(serializer = ToGeneralSerializer.class)
public class GeneralJobConfiguration extends JobConfiguration {

    private String detail;

    public GeneralJobConfiguration() {
    }

    public GeneralJobConfiguration(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public void deserialize(JSONObject value) {
        detail = value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneralJobConfiguration that = (GeneralJobConfiguration) o;
        return getDetail().equals(that.getDetail());
    }

    @Override
    public int hashCode() {
        int result = getDetail() != null ? getDetail().hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return this.detail;
    }

    public JSONObject toJsonObject() {
        return JSONObject.parseObject(this.detail);
    }
}