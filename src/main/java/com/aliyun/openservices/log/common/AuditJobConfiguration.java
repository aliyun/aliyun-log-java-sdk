package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class AuditJobConfiguration extends JobConfiguration {

    private String detail;

    public AuditJobConfiguration() {
    }

    public AuditJobConfiguration(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
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
        AuditJobConfiguration that = (AuditJobConfiguration) o;
        return getDetail().equals(that.getDetail());
    }

    @Override
    public int hashCode() {
        int result = getDetail() != null ? getDetail().hashCode() : 0;
        return result;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        return this.detail == null ? null : JSONObject.parseObject(this.detail);
    }
}
