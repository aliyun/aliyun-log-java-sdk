package com.aliyun.openservices.log.common.auth;

public interface CredentialsFetcher {


    /**
     * Fetches temporary credentials from the authorization server.
     *
     * @return credentials
     */
    TemporaryCredentials fetch();
}
