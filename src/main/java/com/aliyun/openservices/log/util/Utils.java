package com.aliyun.openservices.log.util;

import java.util.Date;
import java.util.Map;

public class Utils {

    private Utils() {
    }

    public static long getTimestamp(final Date date) {
        return date.getTime() / 1000;
    }

    public static String getOrEmpty(Map<String, String> map, String key) {
        return map.containsKey(key) ? map.get(key) : "";
    }

    public static String safeToString(final Object object) {
        return object == null ? null : object.toString();
    }
}
