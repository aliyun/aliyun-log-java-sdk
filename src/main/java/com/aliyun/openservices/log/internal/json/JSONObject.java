package com.aliyun.openservices.log.internal.json;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Internal use only. Do not use this class in application code.
 * <p>
 * A plain-Java JSON object (insertion-ordered map) backed by gson for
 * parsing/serialization, providing the lenient accessor semantics the SDK
 * historically relied on: absent keys yield {@code null} (or the primitive
 * default for {@code getXxxValue}), and scalar values are coerced across
 * String/Number/Boolean.
 */
public class JSONObject extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    /** Original JSON text this object was parsed from, if captured. */
    private transient String rawText;

    public JSONObject() {
    }

    public JSONObject(Map<String, Object> map) {
        super(map);
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public static JSONObject parseObject(String text) {
        Object value = JsonTree.fromElement(JsonTree.parse(text));
        if (value == null) {
            return null;
        }
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        throw new JSONException("Not a JSON object: " + text);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        return GsonHolder.gson().fromJson(text, clazz);
    }

    public static Object parse(String text) {
        return JsonTree.fromElement(JsonTree.parse(text));
    }

    public static String toJSONString(Object object) {
        return GsonHolder.gson().toJson(object);
    }

    public String toJSONString() {
        return GsonHolder.gson().toJson(this);
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    public Map<String, Object> getInnerMap() {
        return this;
    }

    public JSONObject getJSONObject(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        if (value instanceof String) {
            return parseObject((String) value);
        }
        throw new JSONException("Value of '" + key + "' is not a JSON object");
    }

    public JSONArray getJSONArray(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }
        if (value instanceof String) {
            return JSONArray.parseArray((String) value);
        }
        throw new JSONException("Value of '" + key + "' is not a JSON array");
    }

    public String getString(String key) {
        return TypeCast.castToString(get(key));
    }

    public Integer getInteger(String key) {
        return TypeCast.castToInteger(get(key));
    }

    public int getIntValue(String key) {
        Integer value = getInteger(key);
        return value == null ? 0 : value;
    }

    public Long getLong(String key) {
        return TypeCast.castToLong(get(key));
    }

    public long getLongValue(String key) {
        Long value = getLong(key);
        return value == null ? 0L : value;
    }

    public Boolean getBoolean(String key) {
        return TypeCast.castToBoolean(get(key));
    }

    public boolean getBooleanValue(String key) {
        Boolean value = getBoolean(key);
        return value != null && value;
    }

    public Double getDouble(String key) {
        return TypeCast.castToDouble(get(key));
    }

    public double getDoubleValue(String key) {
        Double value = getDouble(key);
        return value == null ? 0D : value;
    }

    public Float getFloat(String key) {
        Double value = getDouble(key);
        return value == null ? null : value.floatValue();
    }

    public float getFloatValue(String key) {
        Float value = getFloat(key);
        return value == null ? 0F : value;
    }

    public BigDecimal getBigDecimal(String key) {
        return TypeCast.castToBigDecimal(get(key));
    }
}
