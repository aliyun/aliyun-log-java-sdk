package com.aliyun.openservices.log.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetworkUtilsTest {

    @Test
    public void testIsIPAddr() {
        String[] validIPAddresses = new String[]{
                "1.1.1.1",
                "255.255.255.255",
                "192.168.1.1",
                "10.10.1.1",
                "132.254.111.10",
                "26.10.2.10",
                "127.0.0.1"
        };
        for (String ipAddr : validIPAddresses) {
            assertTrue(NetworkUtils.isIPAddr(ipAddr));
        }
        String[] invalidIPAddresses = new String[]{
                "10.10.10",
                "10.10",
                "10",
                "a.a.a.a",
                "10.0.0.a",
                "10.10.10.256",
                "222.222.2.999",
                "999.10.10.20",
                "2222.22.22.22",
                "22.2222.22.2",
                "10.10.10",
                "10.10.10",
        };
        for (String ipAddr : invalidIPAddresses) {
            assertFalse(NetworkUtils.isIPAddr(ipAddr));
        }
    }
}
