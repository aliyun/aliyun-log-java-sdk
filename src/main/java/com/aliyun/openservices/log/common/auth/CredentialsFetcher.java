package com.aliyun.openservices.log.common.auth;

public interface CredentialsFetcher {


    /**
     * Fetches temporary credentials from the authorization server with retry.
     *
     * @param maxRetry the max retry times to fetch the credentials, if [maxRetry] equals or
     *                less than 0, no retry will be performed.
     * @return credentials
     */
    TemporaryCredentials fetch(int maxRetry);
}
