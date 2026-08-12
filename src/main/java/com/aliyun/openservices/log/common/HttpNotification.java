package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

abstract class HttpNotification extends Notification {

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
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (serviceUri != null) {
            value.put(Consts.SERVICE_URI, serviceUri);
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
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
