package com.aliyun.openservices.log.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class JsonUtils {

    /**
     * Serialize Date to Unix timestamp and deserialize Unix timestamp to date.
     */
    private static final SerializeConfig SERIALIZE_CONFIG = new SerializeConfig();

    static {
        SERIALIZE_CONFIG.put(Date.class, new DateToUnixTimestampSerializer());
    }

    private JsonUtils() {
    }

    public static String serialize(Object object) {
        return JSON.toJSONString(object, SERIALIZE_CONFIG);
    }

    public static <T> List<T> readList(JSONObject value, String key, Unmarshaller<T> unmarshaller) {
        return readList(value.getJSONArray(key), unmarshaller);
    }

    public static <T> List<T> readList(JSONArray list, Unmarshaller<T> unmarshaller) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> values = new ArrayList<T>(list.size());
        for (int i = 0; i < list.size(); i++) {
            values.add(unmarshaller.unmarshal(list, i));
        }
        return values;
    }

    public static List<String> readOptionalStrings(JSONObject object, String key) {
        if (object == null || !object.containsKey(key)) {
            return Collections.emptyList();
        }
        try {
            return readStringList(object, key);
        } catch (JSONException ex) {
            return Collections.emptyList();
        }
    }

    public static List<String> readStringList(JSONObject object, String key) {
        return readList(object, key, new Unmarshaller<String>() {
            @Override
            public String unmarshal(JSONArray value, int index) {
                return value.getString(index);
            }
        });
    }

    public static String readOptionalString(JSONObject object, String key) {
        return object.containsKey(key) ? object.getString(key) : null;
    }

    public static String readOptionalString(JSONObject object, String key, String defaultValue) {
        if (object.containsKey(key)) {
            return object.getString(key);
        } else {
            return defaultValue;
        }
    }

    public static boolean readBool(JSONObject object, String key, boolean defaultValue) {
        return object.containsKey(key) ? object.getBoolean(key) : defaultValue;
    }

    public static Integer readOptionalInt(JSONObject object, String key) {
        return object.containsKey(key) ? object.getIntValue(key) : null;
    }

    public static Date readOptionalDate(JSONObject object, String key) {
        return object.containsKey(key) ? readDate(object, key) : null;
    }

    public static Date readDate(JSONObject object, String key) {
        return Utils.timestampToDate(object.getIntValue(key));
    }

    public static Map<String, String> readOptionalMap(JSONObject object, final String key) {
        if (!object.containsKey(key)) {
            return Collections.emptyMap();
        }
        JSONObject value = object.getJSONObject(key);
        if (value == null || value.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<String> keySet = value.keySet();
        Map<String, String> map = new HashMap<String, String>(keySet.size());
        for (String fieldName : keySet) {
            map.put(fieldName, value.getString(fieldName));
        }
        return map;
    }

    /**
     * Serialize date to unix timestamp.
     */
    private static class DateToUnixTimestampSerializer implements ObjectSerializer {

        @Override
        public void write(JSONSerializer serializer,
                          Object date,
                          Object fieldName,
                          Type fieldType,
                          int features) {
            if (date != null) {
                serializer.write(Utils.dateToTimestamp((Date) date));
            }
        }
    }

}
