package com.aliyun.openservices.log.util;

import java.util.Collection;

public final class Args {
    private Args() {
    }

    public static void notNull(final Object value, final String name) {
        if (value == null) {
            throw new IllegalArgumentException("[" + name + "] must not be null");
        }
    }

    public static void notNullOrEmpty(final Collection collection, final String name) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("[" + name + "] must not be null or empty!");
        }
    }

    public static void notNullOrEmpty(final String value, final String name) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("[" + name + "] must not be null or empty!");
        }
    }
}
