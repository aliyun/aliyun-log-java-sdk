package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.http.client.HttpMethod;
import net.sf.json.JSONObject;

// TODO support authentication, custom headers
public class WebhookNotification extends Notification {

    @JSONField
    private HttpMethod method;

    @JSONField
    private String serviceUri;

    public WebhookNotification() {
        super(NotificationType.WEBHOOK);
    }

    WebhookNotification(NotificationType type) {
        super(type);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
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
        method = HttpMethod.fromString(value.getString(Consts.METHOD));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebhookNotification that = (WebhookNotification) o;

        if (getMethod() != that.getMethod()) return false;
        return getServiceUri() != null ? getServiceUri().equals(that.getServiceUri()) : that.getServiceUri() == null;
    }

    @Override
    public int hashCode() {
        int result = getMethod() != null ? getMethod().hashCode() : 0;
        result = 31 * result + (getServiceUri() != null ? getServiceUri().hashCode() : 0);
        return result;
    }
}
