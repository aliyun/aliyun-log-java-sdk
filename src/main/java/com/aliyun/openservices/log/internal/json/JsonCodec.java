package com.aliyun.openservices.log.internal.json;

import java.util.Map;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.google.gson.JsonElement;

/** Internal model/tree codec. Gson types never cross this boundary. */
@InternalApi
public final class JsonCodec {

    private JsonCodec() {
    }

    public static String toJson(Object value) {
        try {
            return JsonTree.write(toElement(value));
        } catch (RuntimeException e) {
            throw wrap("serialize", e);
        }
    }

    public static <T> T fromJson(String text, Class<T> type) {
        return fromElement(JsonTree.parse(text), type);
    }

    public static <T> T fromJson(JSONObject value, Class<T> type) {
        if (value == null) {
            return null;
        }
        return fromElement(value.element(), type);
    }

    public static JSONObject toJsonObject(Object value) {
        JsonElement element = toElement(value);
        if (element.isJsonNull()) {
            return null;
        }
        if (!element.isJsonObject()) {
            throw new JSONException("Expected serialized model to be JSON object but was "
                    + JsonTree.typeName(element));
        }
        return new JSONObject(element.getAsJsonObject());
    }

    public static JSONArray toJsonArray(Object value) {
        JsonElement element = toElement(value);
        if (element.isJsonNull()) {
            return null;
        }
        if (!element.isJsonArray()) {
            throw new JSONException("Expected serialized model to be JSON array but was "
                    + JsonTree.typeName(element));
        }
        return new JSONArray(element.getAsJsonArray());
    }

    public static Map<String, Object> toMap(JSONObject value) {
        return value == null ? null : JsonTree.toMap(value.element());
    }

    private static JsonElement toElement(Object value) {
        if (value instanceof JSONObject) {
            return ((JSONObject) value).element();
        }
        if (value instanceof JSONArray) {
            return ((JSONArray) value).element();
        }
        try {
            return GsonHolder.gson().toJsonTree(value);
        } catch (RuntimeException e) {
            throw wrap("serialize", e);
        }
    }

    private static <T> T fromElement(JsonElement value, Class<T> type) {
        try {
            return GsonHolder.gson().fromJson(value, type);
        } catch (RuntimeException e) {
            throw wrap("deserialize", e);
        }
    }

    private static JSONException wrap(String operation, RuntimeException cause) {
        return new JSONException("Failed to " + operation + " JSON: " + cause.getMessage(), cause);
    }
}
