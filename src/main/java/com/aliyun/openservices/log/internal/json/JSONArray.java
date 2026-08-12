package com.aliyun.openservices.log.internal.json;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.google.gson.JsonElement;

/** Internal mutable JSON array restricted to standard JSON values. */
@InternalApi
public final class JSONArray {

    private final com.google.gson.JsonArray value;

    public JSONArray() {
        this(new com.google.gson.JsonArray());
    }

    JSONArray(com.google.gson.JsonArray value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    com.google.gson.JsonArray element() {
        return value;
    }

    public static JSONArray parseArray(String text) {
        JsonElement parsed = JsonTree.parse(text);
        if (parsed.isJsonNull()) {
            return null;
        }
        if (!parsed.isJsonArray()) {
            throw new JSONException("Expected JSON array but was " + JsonTree.typeName(parsed));
        }
        return new JSONArray(parsed.getAsJsonArray());
    }

    public void add(String item) {
        if (item == null) {
            value.add(com.google.gson.JsonNull.INSTANCE);
        } else {
            value.add(item);
        }
    }

    public void add(Number item) {
        if (item == null) {
            value.add(com.google.gson.JsonNull.INSTANCE);
        } else {
            JsonTree.requireFinite(item);
            value.add(item);
        }
    }

    public void add(JSONObject item) {
        value.add(item == null ? com.google.gson.JsonNull.INSTANCE : item.element());
    }

    public void add(JSONArray item) {
        value.add(item == null ? com.google.gson.JsonNull.INSTANCE : item.element());
    }

    public int size() {
        return value.size();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public JSONObject getJSONObject(int index) {
        JsonElement item = nullableValue(index);
        if (item == null) {
            return null;
        }
        if (!item.isJsonObject()) {
            throw typeError(index, "object", item);
        }
        return new JSONObject(item.getAsJsonObject());
    }

    public JSONArray getJSONArray(int index) {
        JsonElement item = nullableValue(index);
        if (item == null) {
            return null;
        }
        if (!item.isJsonArray()) {
            throw typeError(index, "array", item);
        }
        return new JSONArray(item.getAsJsonArray());
    }

    public String getString(int index) {
        JsonElement item = nullableValue(index);
        if (item == null) {
            return null;
        }
        if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
            throw typeError(index, "string", item);
        }
        return item.getAsString();
    }

    public int getIntValue(int index) {
        JsonElement item = nullableValue(index);
        if (item == null) {
            return 0;
        }
        if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isNumber()) {
            throw typeError(index, "number", item);
        }
        // TODO: Gson narrowing conversions can overflow silently. Preserve Gson
        // behavior for compatibility for now, but consider range checks later.
        return item.getAsInt();
    }

    private JsonElement nullableValue(int index) {
        JsonElement item = value.get(index);
        return item.isJsonNull() ? null : item;
    }

    private static JSONException typeError(int index, String expected, JsonElement actual) {
        return new JSONException("Value at index " + index + " must be JSON " + expected
                + " but was " + JsonTree.typeName(actual));
    }

    @Override
    public String toString() {
        return JsonTree.write(value);
    }
}
