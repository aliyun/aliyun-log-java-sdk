package com.aliyun.openservices.log.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DigestUtilsTest {

    public static final String[][] TEST_HEX2MD5 = {
            {"20", "7215EE9C7D9DC229D2921A40E899EC5F"}, // " "
            {"6f6c735f73646b", "6130B39CFD626F8A4D48BFB3E44DAA79"}, // "ols_sdk"
            {"6d6435", "1BC29B36F623BA82AAF6724FD3B16718"}, // "md5"
            {"6b6c3338797353016b64220a0964736938", "802EAEB3D5AC8CBE65934997C0B83E4B"}, // "kl38ys\123\001kd\"\n\tdsi8"
            {"6b6c3338797353016b64220a090864736938", "04568016387F3BF053211F01CF55E52E"}, // "kl38ys\123\001kd\"\n\t\bdsi8"
            {"89", "2854272FEC044D0BDB16DE12CB62D07E"}, // "\211"
            {"6b6c333879735301896b64220a090864736938", "F2A4958FB6C4CCB82B129512BBB98205"}, // "kl38ys\123\001\211kd\"\n\t\bdsi8"
            {"6b6c33387973530001896b64220a090864736938", "344676963785D56724F5B8228F9B2B69"} // "kl38ys\123\000\001\211kd\"\n\t\bdsi8"
    };

    /**
     * @param str hexadecimal string without leading "0x"
     * @return byte array: group each two contiguous characters as a byte
     */
    private byte[] hex2Byte(String str) {
        if (str == null) {
            return null;
        }

        int len = str.length();

        if (len == 0) {
            return null;
        }
        if (len % 2 == 1) {
            str = "0" + str;
            ++len;
        }

        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer
                        .decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void testMd5Crypt() {
        for (String[] testCase : TEST_HEX2MD5) {
            assertEquals(testCase[1], DigestUtils.md5Crypt(hex2Byte(testCase[0])));
        }
    }

    @Test
    public void testUrlParametersToString() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "log");
        params.put("topic", "topic_1");
        params.put("from", "1300");
        params.put("to", "1400");
        params.put("query", "sls query string");
        params.put("reverse", "true");
        String urlParam = "from=1300&query=sls query string&reverse=true&to=1400&topic=topic_1&type=log";
        assertEquals(urlParam, DigestUtils.urlParametersToString(params));
    }

}
