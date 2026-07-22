package com.aliyun.openservices.log.internal.json;

import com.aliyun.openservices.log.annotation.InternalApi;

/**
 * Internal use only. Do not use this class in application code.
 */
@InternalApi
public final class JSON {

    private JSON() {
    }

    public static String toJSONString(Object object) {
        return GsonHolder.gson().toJson(object);
    }

    public static Object parse(String text) {
        return JsonTree.fromElement(JsonTree.parse(text));
    }

    public static JSONObject parseObject(String text) {
        return JSONObject.parseObject(text);
    }

    public static JSONArray parseArray(String text) {
        return JSONArray.parseArray(text);
    }
}
