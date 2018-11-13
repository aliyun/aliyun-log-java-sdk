package com.aliyun.openservices.log.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.aliyun.openservices.log.internal.Unmarshaller;
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

    public static List<String> readStringList(JSONObject object, String key) {
        return readList(object, key, new Unmarshaller<String>() {
            @Override
            public String unmarshal(JSONArray value, int index) {
                return value.getString(index);
            }
        });
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
