package com.aliyun.openservices.log.internal.json;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 * Internal use only. Converts between gson's JsonElement tree and the
 * plain-Java tree used by {@link JSONObject}/{@link JSONArray}.
 */
final class JsonTree {

    private JsonTree() {
    }

    static JsonElement parse(String text) {
        try {
            JsonReader reader = new JsonReader(new StringReader(text));
            reader.setStrictness(Strictness.LEGACY_STRICT);
            if (reader.peek() == JsonToken.END_DOCUMENT) {
                // fastjson returned null for empty input
                return JsonNull.INSTANCE;
            }
            // Not JsonParser.parseReader: it forces LENIENT and would accept garbage
            JsonElement element = GsonHolder.gson().getAdapter(JsonElement.class).read(reader);
            if (reader.peek() != JsonToken.END_DOCUMENT) {
                throw new JSONException("Trailing content after JSON document");
            }
            return element;
        } catch (JsonParseException e) {
            throw new JSONException("Failed to parse JSON: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new JSONException("Failed to parse JSON: " + e.getMessage(), e);
        }
    }

    static Object fromElement(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            JSONObject result = new JSONObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                result.put(entry.getKey(), fromElement(entry.getValue()));
            }
            return result;
        }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            JSONArray result = new JSONArray(array.size());
            for (JsonElement item : array) {
                result.add(fromElement(item));
            }
            return result;
        }
        // primitive
        if (element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsBoolean();
        }
        if (element.getAsJsonPrimitive().isString()) {
            return element.getAsString();
        }
        return parseNumber(element.getAsString());
    }

    /**
     * Follows fastjson semantics: integral values become Integer/Long/BigInteger
     * depending on magnitude, decimals become BigDecimal, so that
     * re-serialization preserves the original literal form.
     */
    static Number parseNumber(String literal) {
        boolean integral = true;
        for (int i = 0; i < literal.length(); i++) {
            char ch = literal.charAt(i);
            if (ch == '.' || ch == 'e' || ch == 'E') {
                integral = false;
                break;
            }
        }
        if (integral) {
            try {
                long value = Long.parseLong(literal);
                if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                    return (int) value;
                }
                return value;
            } catch (NumberFormatException e) {
                return new BigInteger(literal);
            }
        }
        return new BigDecimal(literal);
    }
}
