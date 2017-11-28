package com.aliyun.openservices.log.http.utils;

/**
 * Utils for common coding.
 * 
 * @author xiaoming.yin
 * 
 */
public class CodingUtils {
	public static void assertParameterNotNull(Object param, String paramName) {
		if (param == null) {
			throw new NullPointerException(paramName + " is null");
		}
	}
	public static void assertStringNotNullOrEmpty(String param, String paramName) {
		assertParameterNotNull(param, paramName);
		assert (param != null);
		if (param.length() == 0) {
			throw new IllegalArgumentException(paramName + " is empty");
		}
	}
	public static boolean isNullOrEmpty(String value) {
		return value == null || value.length() == 0;
	}
}
