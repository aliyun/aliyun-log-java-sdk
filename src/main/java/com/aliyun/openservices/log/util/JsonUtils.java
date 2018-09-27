package com.aliyun.openservices.log.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.DateCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
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
        ParserConfig.getGlobalInstance().putDeserializer(Date.class, new UnixTimestampToDateDeserializer());
        SERIALIZE_CONFIG.put(Date.class, new DateToUnixTimestampSerializer());
    }

    private JsonUtils() {
    }

    public static String serialize(Object object) {
        return JSON.toJSONString(object, SERIALIZE_CONFIG);
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

    private static class UnixTimestampToDateDeserializer extends DateCodec {

        @Override
        public <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {
            if (val instanceof Number) {
                long value = ((Number) val).longValue() * 1000;
                return super.cast(parser, clazz, fieldName, value);
            }
            return null;
        }
    }
}
