package com.aliyun.openservices.log.internal.json;

import java.util.ArrayList;
import java.util.Collection;
import com.aliyun.openservices.log.annotation.InternalApi;

/**
 * Internal use only. Do not use this class in application code.
 * <p>
 * A plain-Java JSON array backed by gson for parsing/serialization. See
 * {@link JSONObject} for the accessor semantics.
 */
@InternalApi
public class JSONArray extends ArrayList<Object> {

    private static final long serialVersionUID = 1L;

    public JSONArray() {
    }

    public JSONArray(int initialCapacity) {
        super(initialCapacity);
    }

    public JSONArray(Collection<?> collection) {
        super(collection);
    }

    public static JSONArray parseArray(String text) {
        Object value = JsonTree.fromElement(JsonTree.parse(text));
        if (value == null) {
            return null;
        }
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }
        throw new JSONException("Not a JSON array: " + text);
    }

    public String toJSONString() {
        return GsonHolder.gson().toJson(this);
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    public JSONObject getJSONObject(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        if (value instanceof String) {
            return JSONObject.parseObject((String) value);
        }
        throw new JSONException("Value at index " + index + " is not a JSON object");
    }

    public JSONArray getJSONArray(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }
        if (value instanceof String) {
            return parseArray((String) value);
        }
        throw new JSONException("Value at index " + index + " is not a JSON array");
    }

    public String getString(int index) {
        return TypeCast.castToString(get(index));
    }

    public Integer getInteger(int index) {
        return TypeCast.castToInteger(get(index));
    }

    public int getIntValue(int index) {
        Integer value = getInteger(index);
        return value == null ? 0 : value;
    }

    public Long getLong(int index) {
        return TypeCast.castToLong(get(index));
    }

    public long getLongValue(int index) {
        Long value = getLong(index);
        return value == null ? 0L : value;
    }

    public Boolean getBoolean(int index) {
        return TypeCast.castToBoolean(get(index));
    }

    public boolean getBooleanValue(int index) {
        Boolean value = getBoolean(index);
        return value != null && value;
    }

    public Double getDouble(int index) {
        return TypeCast.castToDouble(get(index));
    }

    public double getDoubleValue(int index) {
        Double value = getDouble(index);
        return value == null ? 0D : value;
    }
}
