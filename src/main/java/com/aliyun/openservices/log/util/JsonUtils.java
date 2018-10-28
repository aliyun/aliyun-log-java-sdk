package com.aliyun.openservices.log.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        return JSON.toJSONString(object, SERIALIZE_CONFIG, SerializerFeature.WriteEnumUsingToString);
    }

    public static <T> List<T> readList(JSONObject value, String key, Unmarshaller<T> unmarshaller) {
        JSONArray list = value.getJSONArray(key);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> values = new ArrayList<T>(list.size());
        for (int i = 0; i < list.size(); i++) {
            values.add(unmarshaller.unmarshal(list.getJSONObject(i)));
        }
        return values;
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

    private static class DateToUnixTimestampSerializer implements ObjectSerializer {

        @Override
        public void write(JSONSerializer serializer,
                          Object date,
                          Object fieldName,
                          Type fieldType,
                          int features) {
            if (date != null) {
                serializer.write(Utils.getTimestamp((Date) date));
            }
        }
    }
}
