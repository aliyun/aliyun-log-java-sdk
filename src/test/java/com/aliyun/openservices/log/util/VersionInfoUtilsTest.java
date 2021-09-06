package com.aliyun.openservices.log.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for VersionInfo.
 */
public class VersionInfoUtilsTest {

    @Test
    public void testGetVersion() {
        String userAgent = VersionInfoUtils.getDefaultUserAgent();
        assertTrue(userAgent.startsWith("aliyun-log-sdk-java-0.6.64"));
    }
}
