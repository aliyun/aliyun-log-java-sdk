package com.aliyun.openservices.log.internal.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.DataSinkType;
import com.aliyun.openservices.log.common.DataSourceType;
import com.aliyun.openservices.log.common.ExportGeneralSink;
import com.aliyun.openservices.log.common.GeneralJobConfiguration;
import com.aliyun.openservices.log.common.IngestionGeneralSource;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.common.JobState;
import com.aliyun.openservices.log.common.JobType;
import com.aliyun.openservices.log.common.KafKaSource;
import com.aliyun.openservices.log.common.NotificationType;
import com.aliyun.openservices.log.common.ResourceName;
import com.aliyun.openservices.log.common.ScheduledSQLBaseParameters;
import com.aliyun.openservices.log.common.TimeSpanType;
import com.aliyun.openservices.log.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.ToNumberPolicy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.aliyun.openservices.log.annotation.InternalApi;

/**
 * Internal use only. Holds the singleton {@link Gson} instance configured to
 * be wire-compatible with the legacy fastjson-based serialization:
 * <ul>
 * <li>HTML escaping disabled (queries/regex contain {@code < > = &})</li>
 * <li>integers stay integers ({@code LONG_OR_DOUBLE})</li>
 * <li>{@code Date} is written as Unix timestamp in seconds</li>
 * <li>SDK enums are written using their {@code toString()} value</li>
 * </ul>
 */
@InternalApi
public final class GsonHolder {

    private static final JsonSerializer<Object> TO_STRING_SERIALIZER = new JsonSerializer<Object>() {
        @Override
        public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString());
        }
    };

    /**
     * Legacy ToGeneralSerializer behavior: toString() returns a JSON document
     * which must be embedded as raw JSON, not as a quoted string.
     */
    private static final JsonSerializer<Object> RAW_JSON_SERIALIZER = new JsonSerializer<Object>() {
        @Override
        public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? JsonNull.INSTANCE : JsonTree.parse(src.toString());
        }
    };

    /**
     * fastjson serialized anonymous/local subclasses (e.g. double-brace
     * initialization) via getters, but gson skips them entirely and writes
     * {@code null}. Route such types to the nearest named superclass adapter.
     */
    private static final TypeAdapterFactory ANONYMOUS_CLASS_FACTORY = new TypeAdapterFactory() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
            Class<?> rawType = type.getRawType();
            if (!rawType.isAnonymousClass() && !rawType.isLocalClass()) {
                return null;
            }
            Class<?> named = rawType;
            while (named.isAnonymousClass() || named.isLocalClass()) {
                named = named.getSuperclass();
            }
            return (TypeAdapter<T>) gson.getAdapter(TypeToken.get(named));
        }
    };

    /**
     * Replicates fastjson semantics on {@link ScheduledSQLBaseParameters}:
     * {@code @JSONField(unwrapped = true)} flattens baseParams entries into the
     * parent object, and {@code @JSONField(serialize = false)} drops the
     * internal fields set.
     */
    private static final TypeAdapterFactory SCHEDULED_SQL_PARAMS_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
            if (!ScheduledSQLBaseParameters.class.isAssignableFrom(type.getRawType())) {
                return null;
            }
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    JsonElement tree = delegate.toJsonTree(value);
                    JsonObject obj = tree.getAsJsonObject();
                    obj.remove("fields");
                    JsonElement base = obj.remove("baseParams");
                    if (base != null && base.isJsonObject()) {
                        for (Map.Entry<String, JsonElement> entry : base.getAsJsonObject().entrySet()) {
                            obj.add(entry.getKey(), entry.getValue());
                        }
                    }
                    elementAdapter.write(out, obj);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    return delegate.read(in);
                }
            };
        }
    };

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapterFactory(ANONYMOUS_CLASS_FACTORY)
            .registerTypeAdapterFactory(SCHEDULED_SQL_PARAMS_FACTORY)
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                @Override
                public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                    return src == null ? JsonNull.INSTANCE : new JsonPrimitive(Utils.dateToTimestamp(src));
                }
            })
            .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                @Override
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    return json == null || json.isJsonNull() ? null : Utils.timestampToDate(json.getAsLong());
                }
            })
            .registerTypeAdapter(JobType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(JobState.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(JobScheduleType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(TimeSpanType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(DataSinkType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(DataSourceType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(NotificationType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(ResourceName.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(KafKaSource.KafkaPosition.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(KafKaSource.ValueType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(AlertConfiguration.JoinType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(AlertConfiguration.GroupType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(AlertConfiguration.StoreType.class, TO_STRING_SERIALIZER)
            .registerTypeAdapter(GeneralJobConfiguration.class, RAW_JSON_SERIALIZER)
            .registerTypeAdapter(IngestionGeneralSource.class, RAW_JSON_SERIALIZER)
            .registerTypeAdapter(ExportGeneralSink.class, RAW_JSON_SERIALIZER)
            .create();

    private GsonHolder() {
    }

    public static Gson gson() {
        return GSON;
    }
}
