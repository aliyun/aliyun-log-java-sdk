package com.aliyun.openservices.log.common.auth;

import java.util.Random;

public class ECSRoleCredentialsProvider implements CredentialsProvider {

    private static final long PRE_FETCH_IN_MILLIS = 1000 * 60 * 3;
    private static final int RANDOM_DELAY_SETP_IN_MILLIS = 500;
    private static final int MAX_RANDOM_DELAY_IN_MILLIS = 1000 * 10;

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
        if (shouldRefresh()) {
            credentials = fetcher.fetch();
        }
        return credentials;
    }

    private boolean shouldRefresh() {
        if (null == credentials) {
            return true;
        }
        // now credentials will never be null
        // use randomDelayMills to avoid multiple thread fetch at the same time
        int randomDelayMills = new Random().nextInt(MAX_RANDOM_DELAY_IN_MILLIS / RANDOM_DELAY_SETP_IN_MILLIS) * RANDOM_DELAY_SETP_IN_MILLIS;
        return credentials.getExpirationInMills() <= System.currentTimeMillis() + PRE_FETCH_IN_MILLIS - randomDelayMills;
    }

    private volatile TemporaryCredentials credentials;
    private final CredentialsFetcher fetcher;
}
