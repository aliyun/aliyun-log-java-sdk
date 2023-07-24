package com.aliyun.openservices.log.common.auth;

import org.junit.Assert;
import org.junit.Test;

public class TemporaryCredentialsTest {

    @Test
    public void test() {
        long now = System.currentTimeMillis();
        long durationInMillis = 10000000;
        // reach expiration time, expire duration = 0 ms
        TemporaryCredentials credentials = new TemporaryCredentials("", "", "",
                now, now);
        Assert.assertTrue(credentials.shouldRefresh());

        // expire duration = 10000000 ms
        credentials = new TemporaryCredentials("", "", "",
                now + durationInMillis, now);
        Assert.assertFalse(credentials.shouldRefresh());

        // reach expiration time, expire duration = 10000000 ms
        credentials = new TemporaryCredentials("", "", "",
                now, now - durationInMillis);
        Assert.assertTrue(credentials.shouldRefresh());

        // 0.8 * expire duration
        double factor = TemporaryCredentials.DEFAULT_EXPIRED_FACTOR;
        long passed_time = Math.round(durationInMillis * factor) + 1;
        long left_time = durationInMillis - passed_time;
        credentials = new TemporaryCredentials("", "", "",
                now + left_time, now - passed_time);
        Assert.assertTrue(credentials.shouldRefresh());

        // 0.4 * expire duration
        passed_time = Math.round(durationInMillis * factor / 2) ;
        left_time = durationInMillis - passed_time;
        credentials = new TemporaryCredentials("", "", "",
                now + left_time, now - passed_time);
        Assert.assertFalse(credentials.shouldRefresh());

    }
}
