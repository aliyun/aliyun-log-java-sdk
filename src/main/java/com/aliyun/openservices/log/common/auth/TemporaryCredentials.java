package com.aliyun.openservices.log.common.auth;

/**
 * Credentials with expiration.
 */
public class TemporaryCredentials implements Credentials {

    public static final double DEFAULT_EXPIRED_FACTOR = 0.8;

    /**
     * Constructs ECSRoleCredentialProvider.
     *
     * @param expiration  the expiration of the credentials, in millisecond format.
     * @param lastUpdated the last updated time of the credentials, in millisecond format.
     */
    public TemporaryCredentials(String accessKeyId, String accessKeySecret, String securityToken,
                                long expiration, long lastUpdated) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.securityToken = securityToken;
        this.expiration = expiration;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Whether the credentials should be refreshed.
     */
    public boolean shouldRefresh() {
        long now = System.currentTimeMillis();
        return (now - lastUpdated) >= (expiration - lastUpdated) * expiredFactor;
    }

    /**
     * Constructs ECSRoleCredentialProvider.
     *
     * @param expiredFactor the expiration factor of the credentials, the
     *                      value should be less than 1.0 and greater than 0. Determines when
     *                      to refresh the credentials.
     */
    public TemporaryCredentials withExpiredFactor(double expiredFactor) {
        this.expiredFactor = expiredFactor;
        return this;
    }

    @Override
    public String getAccessKeyId() {
        return accessKeyId;
    }

    @Override
    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    @Override
    public String getSecurityToken() {
        return securityToken;
    }

    @Override
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    @Override
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    @Override
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    protected String accessKeyId;
    protected String accessKeySecret;
    protected String securityToken;

    protected long expiration;
    protected long lastUpdated;
    protected double expiredFactor = DEFAULT_EXPIRED_FACTOR;

}
