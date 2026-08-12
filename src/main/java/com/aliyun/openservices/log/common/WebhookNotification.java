package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;


public class WebhookNotification extends HttpNotification {

    /**
     * Optional headers for http request.
     */
    private Map<String, String> headers;

    /**
     * Optional method, default to POST.
     */
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
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (headers != null) {
            value.put(Consts.HEADERS, JsonCodec.toJsonObject(headers));
        }
        if (method != null) {
            value.put(Consts.METHOD, method.toString());
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
        String method = JsonUtils.readOptionalString(value, Consts.METHOD);
        if (method != null) {
            setMethod(HttpMethod.fromString(method));
        }
        setHeaders(JsonUtils.readOptionalMap(value, Consts.HEADERS));
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
