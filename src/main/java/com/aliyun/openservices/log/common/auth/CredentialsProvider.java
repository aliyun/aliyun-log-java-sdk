package com.aliyun.openservices.log.common.auth;

public interface CredentialsProvider {
    /**
     * This method is called everytime AccessKeyId/AccessKeySecret/StsToken is needed by log client,
     * the {@link CredentialsProvider} should cache credentials to avoid update credentials too frequently.
     * @note This method must be thread safe to avoid data race.
     */
    Credentials getCredentials();
}
