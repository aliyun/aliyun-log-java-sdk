package com.aliyun.openservices.log.util;

import com.alibaba.fastjson.JSON;

public final class JsonUtils {

    private JsonUtils() {
    }

    public static String serialize(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T deserialize(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }
}
