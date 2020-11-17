package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.JSONObject;

abstract class HttpNotification extends Notification {

    @JSONField
    private String serviceUri;

    HttpNotification(NotificationType type) {
        super(type);
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        serviceUri = value.getString(Consts.SERVICE_URI);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpNotification that = (HttpNotification) o;

        return getServiceUri() != null ? getServiceUri().equals(that.getServiceUri()) : that.getServiceUri() == null;
    }

    @Override
    public int hashCode() {
        return getServiceUri() != null ? getServiceUri().hashCode() : 0;
    }
}
