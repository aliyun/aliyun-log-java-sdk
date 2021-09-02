package com.aliyun.openservices.log.util;

import java.util.Date;
import java.util.List;
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

    public static boolean validateProject(final String projectName) {
        // Why not use ^[0-9a-zA-Z][0-9a-zA-Z_-]{1,126}[0-9a-zA-Z]$ here?
        // Some very old project name is their id like 1, 15.
        if (projectName == null) {
            return false;
        }
        int n = projectName.length();
        if (n <= 0 || n > 128) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            char ch = projectName.charAt(i);
            if ((ch >= '0' && ch <= '9')
                    || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                continue;
            }
            if ((ch == '-' || ch == '_') && (i > 0 && i < n - 1)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static String normalizeHostName(String hostName) {
        int n = hostName.length();
        while (n > 0 && hostName.charAt(n - 1) == '/') {
            n--;
        }
        if (n <= 0) {
            return null;
        }
        if (n < hostName.length()) {
            hostName = hostName.substring(0, n);
        }
        for (int i = 0; i < n; i++) {
            final char ch = hostName.charAt(i);
            if (ch == '-' || ch == '_' || ch == '.' || (ch >= '0' && ch <= '9')
                    || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                continue;
            }
            return null;
        }
        return hostName;
    }

    public static String join(String delimiter, List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < values.size(); idx ++) {
            if (idx == (values.size() - 1)) {
                sb.append(values.get(idx));
            } else {
                sb.append(values.get(idx)).append(delimiter);
            }
        }
        return sb.toString();
    }
}
