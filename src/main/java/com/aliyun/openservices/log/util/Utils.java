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
}
