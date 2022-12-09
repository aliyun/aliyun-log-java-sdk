package com.aliyun.openservices.log.http.utils;

import org.junit.Assert;
import org.junit.Test;

public class HttpUtilTest {

    @Test
    public void testPercentEncode() {
        String value = "123abc!@#$%^&*()-=_+ ~|\\/";
        String res = HttpUtil.percentEncode(value);
        Assert.assertEquals("123abc%21%40%23%24%25%5E%26%2A%28%29-%3D_%2B%20~%7C%5C%2F", res);
        String encoded = HttpUtil.percentEncode("!@#$%^&*()=-+ ~./_[()]%20你好\0\u0111❤\uD83D\uDE13");
        Assert.assertEquals("%21%40%23%24%25%5E%26%2A%28%29%3D-%2B%20~.%2F_%5B%28%29%5D%2520%E4%BD%A0%E5%A5%BD%00%C4%91%E2%9D%A4%F0%9F%98%93", encoded);
    }
}
