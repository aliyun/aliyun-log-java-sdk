package com.aliyun.openservices.log.util;

import java.util.Date;
import java.util.Map;

public final class Utils {

    private Utils() {
    }

    public static long dateToTimestamp(final Date date) {
        return date.getTime() / 1000;
    }

    public static Date timestampToDate(final long timestamp) {
        return new Date(timestamp * 1000);
    }

    public static String getOrEmpty(Map<String, String> map, String key) {
        return map.containsKey(key) ? map.get(key) : "";
    }

    public static String safeToString(final Object object) {
        return object == null ? null : object.toString();
    }

    private static long parseLongWithoutSuffix(String s) {
        return Long.parseLong(s.substring(0, s.length() - 1).trim());
    }

    public static long parseDuration(String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Duration could not be empty: " + s);
        }
        if (s.endsWith("s")) {
            return parseLongWithoutSuffix(s);
        } else if (s.endsWith("m")) {
            return 60L * parseLongWithoutSuffix(s);
        } else if (s.endsWith("h")) {
            return 3600L * parseLongWithoutSuffix(s);
        } else if (s.endsWith("d")) {
            return 86400L * parseLongWithoutSuffix(s);
        } else {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ex) {
                throw new NumberFormatException("'" + s + "' is not a valid duration. Should be numeric value followed by a unit, i.e. 20s. Valid units are s, m, h and d.");
            }
        }
    }
}
