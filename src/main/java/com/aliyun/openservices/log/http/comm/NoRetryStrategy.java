package com.aliyun.openservices.log.http.comm;

/**
 * No retry strategy that does nothing when HTTP request fails.
 */
public class NoRetryStrategy extends RetryStrategy {

    @Override
    public boolean shouldRetry(Exception ex, RequestMessage request, int retries) {
        return false;
    }

}