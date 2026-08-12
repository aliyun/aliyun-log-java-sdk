package com.aliyun.openservices.log.internal.json;

import java.util.Collections;
import java.util.Set;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Internal mutable JSON object whose values are restricted to the JSON data
 * model. Gson implementation types are deliberately not exposed by this API.
 */
@InternalApi
public final class JSONObject {

    private final JsonObject value;

    public JSONObject() {
        this(new JsonObject());
    }

    JSONObject(JsonObject value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    JsonObject element() {
        return value;
    }

    public static JSONObject parseObject(String text) {
        JsonElement parsed = JsonTree.parse(text);
        if (parsed.isJsonNull()) {
            return null;
        }
        if (!parsed.isJsonObject()) {
            throw new JSONException("Expected JSON object but was " + JsonTree.typeName(parsed));
        }
        return new JSONObject(parsed.getAsJsonObject());
    }

    public void put(String key, String item) {
        value.addProperty(key, item);
    }

    public void put(String key, Boolean item) {
        value.addProperty(key, item);
    }

    public void put(String key, Number item) {
        if (item != null) {
            JsonTree.requireFinite(item);
        }
        value.addProperty(key, item);
    }

    public void put(String key, JSONObject item) {
        value.add(key, item == null ? com.google.gson.JsonNull.INSTANCE : item.element());
    }

    public void put(String key, JSONArray item) {
        value.add(key, item == null ? com.google.gson.JsonNull.INSTANCE : item.element());
    }

    public boolean containsKey(String key) {
        return value.has(key);
    }

    public boolean isNull(String key) {
        JsonElement item = value.get(key);
        return item == null || item.isJsonNull();
    }

    public boolean isString(String key) {
        JsonElement item = nullableValue(key);
        return item != null && item.isJsonPrimitive()
                && item.getAsJsonPrimitive().isString();
    }

    public boolean isNumber(String key) {
        JsonElement item = nullableValue(key);
        return item != null && item.isJsonPrimitive()
                && item.getAsJsonPrimitive().isNumber();
    }

    public boolean isBoolean(String key) {
        JsonElement item = nullableValue(key);
        return item != null && item.isJsonPrimitive()
                && item.getAsJsonPrimitive().isBoolean();
    }

    public boolean isObject(String key) {
        JsonElement item = nullableValue(key);
        return item != null && item.isJsonObject();
    }

    public boolean isArray(String key) {
        JsonElement item = nullableValue(key);
        return item != null && item.isJsonArray();
    }

    public boolean isEmpty() {
        return value.size() == 0;
    }

    public int size() {
        return value.size();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(value.keySet());
    }

    public void putAll(JSONObject other) {
        if (other == null) {
            return;
        }
        for (String key : other.element().keySet()) {
            value.add(key, other.element().get(key));
        }
    }

    public void remove(String key) {
        value.remove(key);
    }

    public JSONObject getJSONObject(String key) {
        JsonElement item = nullableValue(key);
        if (item == null) {
            return null;
        }
        if (!item.isJsonObject()) {
            throw typeError(key, "object", item);
        }
        return new JSONObject(item.getAsJsonObject());
    }

    public JSONArray getJSONArray(String key) {
        JsonElement item = nullableValue(key);
        if (item == null) {
            return null;
        }
        if (!item.isJsonArray()) {
            throw typeError(key, "array", item);
        }
        return new JSONArray(item.getAsJsonArray());
    }

    public String getString(String key) {
        JsonPrimitive item = primitive(key, "string");
        if (item == null) {
            return null;
        }
        if (!item.isString()) {
            throw typeError(key, "string", item);
        }
        return item.getAsString();
    }

    public Integer getInteger(String key) {
        JsonPrimitive item = number(key);
        return item == null ? null : item.getAsInt();
    }

    public int getIntValue(String key) {
        Integer item = getInteger(key);
        return item == null ? 0 : item;
    }

    public Long getLong(String key) {
        JsonPrimitive item = number(key);
        return item == null ? null : item.getAsLong();
    }

    public long getLongValue(String key) {
        Long item = getLong(key);
        return item == null ? 0L : item;
    }

    public Boolean getBoolean(String key) {
        JsonPrimitive item = primitive(key, "boolean");
        if (item == null) {
            return null;
        }
        if (!item.isBoolean()) {
            throw typeError(key, "boolean", item);
        }
        return item.getAsBoolean();
    }

    public boolean getBooleanValue(String key) {
        Boolean item = getBoolean(key);
        return item != null && item;
    }

    public double getDoubleValue(String key) {
        JsonPrimitive item = number(key);
        return item == null ? 0D : item.getAsDouble();
    }

    private JsonPrimitive number(String key) {
        JsonPrimitive item = primitive(key, "number");
        if (item == null) {
            return null;
        }
        if (!item.isNumber()) {
            throw typeError(key, "number", item);
        }
        // TODO: Gson narrowing conversions can overflow silently. Preserve Gson
        // behavior for compatibility for now, but consider range checks later.
        return item;
    }

    private JsonPrimitive primitive(String key, String expected) {
        JsonElement item = nullableValue(key);
        if (item == null) {
            return null;
        }
        if (!item.isJsonPrimitive()) {
            throw typeError(key, expected, item);
        }
        return item.getAsJsonPrimitive();
    }

    private JsonElement nullableValue(String key) {
        JsonElement item = value.get(key);
        return item == null || item.isJsonNull() ? null : item;
    }

    private static JSONException typeError(String key, String expected, JsonElement actual) {
        return new JSONException("Value of '" + key + "' must be JSON " + expected
                + " but was " + JsonTree.typeName(actual));
    }

    @Override
    public String toString() {
        return JsonTree.write(value);
    }
}
