package com.aliyun.openservices.log.common.auth;

public class ECSRoleCredentialsProvider implements CredentialsProvider {

    /**
     * Constructs ECSRoleCredentialProvider.
     *
     * @param ecsRamRole ram role for the ecs.
     */
    public ECSRoleCredentialsProvider(String ecsRamRole) {
        this.fetcher = new ECSRoleCredentialsFetcher(ecsRamRole);
    }

    @Override
    public Credentials getCredentials() {
        if (null == credentials || credentials.shouldRefresh()) {
            credentials = fetcher.fetch(FETCH_CREDENTIALS_MAX_RETRY_TIMES);
        }
        return credentials;
    }

    public static final int FETCH_CREDENTIALS_MAX_RETRY_TIMES = 3;

    private TemporaryCredentials credentials;
    private CredentialsFetcher fetcher;
}
