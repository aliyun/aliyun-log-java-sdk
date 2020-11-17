/**
 * Copyright (C) Alibaba Cloud Computing
 * All rights reserved.
 * 
 * 版权所有 （C）阿里云计算有限公司
 */
package com.aliyun.openservices.log.http.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Util class for Date.
 * @author xiaoming.yin
 *
 */
public class DateUtil {
    // RFC 822 Date Format
    private static final String RFC822_DATE_FORMAT =
            "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String FORMAT_TYPE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    /**
     * Formats Date to GMT string.
     * @param date
     * @return Rfc822Date
     */
    public static String formatRfc822Date(Date date){
        return getRfc822DateFormat().format(date);
    }

    private static DateFormat getRfc822DateFormat(){
        SimpleDateFormat rfc822DateFormat =
                new SimpleDateFormat(RFC822_DATE_FORMAT, Locale.US);
        rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));

        return rfc822DateFormat;
    }

    public static long stringToLong(String time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_TYPE);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = dateFormat.parse(time);
        return date.getTime();
    }
}
