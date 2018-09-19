package com.aliyun.openservices.log.util;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JsonUtils {

    private JsonUtils() {
    }

    public static String serialize(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T deserialize(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    public static List<String> readList(JSONObject object, String key) {
        JSONArray list = object.getJSONArray(key);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> values = new ArrayList<String>(list.size());
        for (int i = 0; i < list.size(); i++) {
            values.add(list.getString(i));
        }
        return values;
    }
}
