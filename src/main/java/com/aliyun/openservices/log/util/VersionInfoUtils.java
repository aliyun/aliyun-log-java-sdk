package com.aliyun.openservices.log.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Unit test for VersionInfo.
 */
public class VersionInfoUtils {
    private static final String USER_AGENT_PREFIX = "aliyun-log-sdk-java";

    private static String defaultUserAgent = null;

    public static String getDefaultUserAgent() {
        if (defaultUserAgent == null) {
            String version = null;
            // note packageVersion not available in unittest
            Package pkg = VersionInfoUtils.class.getPackage();
            if (pkg != null) {
                version = pkg.getImplementationVersion();
            }
            if (version == null) {
                version = "unknown-version";
            }
            defaultUserAgent = USER_AGENT_PREFIX + "-" + version + "/" + System.getProperty("java.version");
        }
        return defaultUserAgent;
    }

}
