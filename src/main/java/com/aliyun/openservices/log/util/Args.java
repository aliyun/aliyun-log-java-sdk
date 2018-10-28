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

    public static void check(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            throw new IllegalArgumentException("Illegal duration: " + duration);
        }
        final String suffix = duration.substring(duration.length() - 1);
        if (!(suffix.equalsIgnoreCase("s")
                || suffix.equalsIgnoreCase("m")
                || suffix.equalsIgnoreCase("h")
                || suffix.equalsIgnoreCase("d"))) {
            throw new IllegalArgumentException("Illegal duration: " + duration);
        }
        for (int i = 0; i < duration.length() - 1; i++) {
            if (!Character.isDigit(duration.charAt(i))) {
                throw new IllegalArgumentException("Illegal duration: " + duration);
            }
        }
    }
}
