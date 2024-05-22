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
            credentials = fetcher.fetch();
        }
        return credentials;
    }


    private volatile TemporaryCredentials credentials;
    private final CredentialsFetcher fetcher;
}
