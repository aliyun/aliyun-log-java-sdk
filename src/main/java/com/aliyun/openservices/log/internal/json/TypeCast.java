package com.aliyun.openservices.log.internal.json;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Internal use only. Lenient scalar coercions matching the legacy fastjson
 * accessor behavior ("" and "null" strings coerce to null, booleans map to
 * 1/0, numeric strings are parsed).
 */
final class TypeCast {

    private TypeCast() {
    }

    static String castToString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    private static boolean isNullText(String text) {
        return text.length() == 0 || "null".equalsIgnoreCase(text);
    }

    static Integer castToInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            String text = (String) value;
            if (isNullText(text)) {
                return null;
            }
            return Integer.parseInt(text);
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        throw new JSONException("Can not cast to int, value: " + value);
    }

    static Long castToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            String text = (String) value;
            if (isNullText(text)) {
                return null;
            }
            return Long.parseLong(text);
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1L : 0L;
        }
        throw new JSONException("Can not cast to long, value: " + value);
    }

    static Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            String text = (String) value;
            if (isNullText(text)) {
                return null;
            }
            return Double.parseDouble(text);
        }
        throw new JSONException("Can not cast to double, value: " + value);
    }

    static Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }
        if (value instanceof String) {
            String text = (String) value;
            if (isNullText(text)) {
                return null;
            }
            if ("true".equalsIgnoreCase(text) || "1".equals(text)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(text) || "0".equals(text)) {
                return Boolean.FALSE;
            }
        }
        throw new JSONException("Can not cast to boolean, value: " + value);
    }

    static BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        if (value instanceof String) {
            String text = (String) value;
            if (isNullText(text)) {
                return null;
            }
            return new BigDecimal(text);
        }
        throw new JSONException("Can not cast to BigDecimal, value: " + value);
    }
}
