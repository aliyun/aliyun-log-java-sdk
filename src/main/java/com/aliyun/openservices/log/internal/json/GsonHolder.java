package com.aliyun.openservices.log.internal.json;

import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.Gson;

/**
 * Holds the package-private {@link Gson} instance configured for the SDK wire
 * contract:
 * <ul>
 * <li>HTML escaping disabled (queries/regex contain {@code < > = &})</li>
 * <li>integers stay integers ({@code LONG_OR_DOUBLE})</li>
 * <li>explicitly annotated enums use their declared value/creator methods</li>
 * </ul>
 */
final class GsonHolder {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .registerTypeAdapterFactory(JsonEnumAdapterFactory.INSTANCE)
            .create();

    private GsonHolder() {
    }

    static Gson gson() {
        return GSON;
    }
}
