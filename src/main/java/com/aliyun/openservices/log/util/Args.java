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
        notNullOrEmpty(duration, "duration");
        final char suffix = duration.charAt(duration.length() - 1);
        boolean valid = (suffix == 's' || suffix == 'm' || suffix == 'h' || suffix == 'd' || Character.isDigit(suffix));
        check(valid, "Invalid duration: " + duration);
        for (int i = 0; i < duration.length() - 1; i++) {
            if (!Character.isDigit(duration.charAt(i))) {
                throw new IllegalArgumentException("Invalid duration: " + duration);
            }
        }
    }
}
