package com.aliyun.openservices.log.common.auth;

public interface CredentialsFetcher {
    /**
     * Fetches credentials from the authorization server.
     *
     * @return credentials
     */
    Credentials fetch();
}
