package com.aliyun.openservices.log.internal.json;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/** Package-private bridge between the SDK holders and Gson's JSON tree. */
final class JsonTree {

    private JsonTree() {
    }

    static JsonElement parse(String text) {
        if (text == null) {
            throw new JSONException("JSON text must not be null");
        }
        try {
            JsonReader reader = new JsonReader(new StringReader(text));
            reader.setStrictness(Strictness.STRICT);
            if (reader.peek() == JsonToken.END_DOCUMENT) {
                throw new JSONException("JSON document must not be empty");
            }
            JsonElement element = GsonHolder.gson().getAdapter(JsonElement.class).read(reader);
            if (reader.peek() != JsonToken.END_DOCUMENT) {
                throw new JSONException("Trailing content after JSON document");
            }
            return element == null ? JsonNull.INSTANCE : element;
        } catch (JsonParseException e) {
            throw new JSONException("Failed to parse JSON: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new JSONException("Failed to parse JSON: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new JSONException("Failed to parse JSON: " + e.getMessage(), e);
        }
    }

    static String write(JsonElement element) {
        StringWriter output = new StringWriter();
        JsonWriter writer = new JsonWriter(output);
        writer.setSerializeNulls(false);
        writer.setHtmlSafe(false);
        writer.setStrictness(Strictness.STRICT);
        try {
            GsonHolder.gson().getAdapter(JsonElement.class).write(writer, element);
            return output.toString();
        } catch (IOException e) {
            throw new JSONException("Failed to write JSON: " + e.getMessage(), e);
        } catch (JSONException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new JSONException("Failed to write JSON: " + e.getMessage(), e);
        }
    }

    static Map<String, Object> toMap(com.google.gson.JsonObject object) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            result.put(entry.getKey(), toPlainValue(entry.getValue()));
        }
        return result;
    }

    private static Object toPlainValue(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonObject()) {
            return toMap(element.getAsJsonObject());
        }
        if (element.isJsonArray()) {
            List<Object> result = new ArrayList<Object>(element.getAsJsonArray().size());
            for (JsonElement item : element.getAsJsonArray()) {
                result.add(toPlainValue(item));
            }
            return result;
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        }
        if (primitive.isString()) {
            return primitive.getAsString();
        }
        return parseNumber(primitive.getAsString());
    }

    private static Number parseNumber(String literal) {
        if (literal.indexOf('.') >= 0 || literal.indexOf('e') >= 0 || literal.indexOf('E') >= 0) {
            return new BigDecimal(literal);
        }
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

    static void requireFinite(Number value) {
        if (value instanceof Double && !Double.isFinite(value.doubleValue())) {
            throw new JSONException("Non-finite double is not valid JSON: " + value);
        }
        if (value instanceof Float && !Float.isFinite(value.floatValue())) {
            throw new JSONException("Non-finite float is not valid JSON: " + value);
        }
    }

    static String typeName(JsonElement element) {
        if (element == null) {
            return "missing";
        }
        if (element.isJsonNull()) {
            return "null";
        }
        if (element.isJsonObject()) {
            return "object";
        }
        if (element.isJsonArray()) {
            return "array";
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isString()) {
            return "string";
        }
        if (primitive.isBoolean()) {
            return "boolean";
        }
        return "number";
    }
}
