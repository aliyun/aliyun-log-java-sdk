package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.http.client.HttpMethod;
import net.sf.json.JSONObject;

import java.util.Map;

public class WebhookNotification extends HttpNotification {

    @JSONField
    private Map<String, String> headers;

    @JSONField
    private HttpMethod method;

    public WebhookNotification() {
        super(NotificationType.WEBHOOK);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        method = HttpMethod.fromString(value.getString(Consts.METHOD));
        if (value.has("headers")) {
            JSONObject headers = value.getJSONObject("headers");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WebhookNotification that = (WebhookNotification) o;

        if (getMethod() != that.getMethod()) return false;
        return getHeaders() != null ? getHeaders().equals(that.getHeaders()) : that.getHeaders() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getMethod() != null ? getMethod().hashCode() : 0);
        result = 31 * result + (getHeaders() != null ? getHeaders().hashCode() : 0);
        return result;
    }
}
