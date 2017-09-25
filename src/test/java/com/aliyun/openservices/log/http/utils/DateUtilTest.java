package com.aliyun.openservices.log.http.utils;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class DateUtilTest {

    @Test
    public void testFormatGMTDate() {
        String expectedRegex = "\\w{3}, \\d{2} \\w{3} \\d{4} \\d{2}:\\d{2}:\\d{2} GMT";
        
        String actual = DateUtil.formatRfc822Date(new Date());
        assertTrue(actual.matches(expectedRegex));
    }
}
