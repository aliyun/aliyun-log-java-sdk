package com.aliyun.openservices.log.internal.json;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/** Creates adapters only for enum types that explicitly opt in with {@link JsonEnumAdapter}. */
final class JsonEnumAdapterFactory implements TypeAdapterFactory {

    static final JsonEnumAdapterFactory INSTANCE = new JsonEnumAdapterFactory();

    private JsonEnumAdapterFactory() {
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<?> rawType = type.getRawType();
        if (!rawType.isEnum() || rawType.getAnnotation(JsonEnumAdapter.class) == null) {
            return null;
        }
        Class enumType = rawType;
        Method valueMethod = findValueMethod(enumType);
        Method creatorMethod = findCreatorMethod(enumType);
        Map<String, Enum> constantsByName = new HashMap<String, Enum>();
        for (Object constant : enumType.getEnumConstants()) {
            Enum enumConstant = (Enum) constant;
            constantsByName.put(enumConstant.name(), enumConstant);
        }
        TypeAdapter<T> adapter = (TypeAdapter<T>) new EnumAdapter(
                enumType, valueMethod, creatorMethod, constantsByName);
        return adapter.nullSafe();
    }

    private static Method findValueMethod(Class<?> enumType) {
        Method result = null;
        for (Method method : enumType.getDeclaredMethods()) {
            if (method.getAnnotation(JsonEnumValue.class) == null) {
                continue;
            }
            if (result != null) {
                throw invalid(enumType, "must declare at most one @JsonEnumValue method");
            }
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
                    || method.getParameterTypes().length != 0 || method.getReturnType() != String.class) {
                throw invalid(enumType,
                        "@JsonEnumValue method must be public, non-static, have no parameters and return String");
            }
            result = method;
        }
        return result;
    }

    private static Method findCreatorMethod(Class<?> enumType) {
        Method result = null;
        for (Method method : enumType.getDeclaredMethods()) {
            if (method.getAnnotation(JsonEnumCreator.class) == null) {
                continue;
            }
            if (result != null) {
                throw invalid(enumType, "must declare at most one @JsonEnumCreator method");
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (!Modifier.isPublic(method.getModifiers()) || !Modifier.isStatic(method.getModifiers())
                    || parameterTypes.length != 1 || parameterTypes[0] != String.class
                    || method.getReturnType() != enumType) {
                throw invalid(enumType,
                        "@JsonEnumCreator method must be public static, accept one String and return the enum type");
            }
            result = method;
        }
        return result;
    }

    private static IllegalArgumentException invalid(Class<?> enumType, String message) {
        return new IllegalArgumentException(enumType.getName() + " " + message);
    }

    private static final class EnumAdapter<E extends Enum<E>> extends TypeAdapter<E> {

        private final Class<E> enumType;
        private final Method valueMethod;
        private final Method creatorMethod;
        private final Map<String, E> constantsByName;

        private EnumAdapter(Class<E> enumType, Method valueMethod, Method creatorMethod,
                Map<String, E> constantsByName) {
            this.enumType = enumType;
            this.valueMethod = valueMethod;
            this.creatorMethod = creatorMethod;
            this.constantsByName = constantsByName;
        }

        @Override
        public void write(JsonWriter out, E value) throws IOException {
            String jsonValue = value.name();
            if (valueMethod != null) {
                jsonValue = invokeValue(value);
            }
            if (jsonValue == null) {
                throw new JsonIOException(enumType.getName() + " @JsonEnumValue method returned null");
            }
            out.value(jsonValue);
        }

        @Override
        public E read(JsonReader in) throws IOException {
            if (in.peek() != JsonToken.STRING) {
                throw new JsonParseException("Expected JSON string for " + enumType.getName()
                        + " but was " + in.peek());
            }
            String jsonValue = in.nextString();
            if (creatorMethod == null) {
                return constantsByName.get(jsonValue);
            }
            return invokeCreator(jsonValue);
        }

        private String invokeValue(E value) {
            try {
                return (String) valueMethod.invoke(value);
            } catch (IllegalAccessException e) {
                throw new JsonIOException("Cannot invoke " + valueMethod, e);
            } catch (InvocationTargetException e) {
                throw new JsonIOException("Failed to invoke " + valueMethod, e.getCause());
            }
        }

        @SuppressWarnings("unchecked")
        private E invokeCreator(String value) {
            try {
                return (E) creatorMethod.invoke(null, value);
            } catch (IllegalAccessException e) {
                throw new JsonParseException("Cannot invoke " + creatorMethod, e);
            } catch (InvocationTargetException e) {
                throw new JsonParseException("Failed to invoke " + creatorMethod, e.getCause());
            }
        }
    }
}
