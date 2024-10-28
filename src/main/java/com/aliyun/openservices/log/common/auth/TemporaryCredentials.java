package com.aliyun.openservices.log.common.auth;

/**
 * Credentials with expiration.
 */
public class TemporaryCredentials implements Credentials {

    /**
     * Constructs TemporaryCredentials.
     *
     * @param expirationInMillis  the expiration of the credentials, in millisecond format.
     * @param updateTimeInMillis the last updated time of the credentials, in millisecond format.
     */
    public TemporaryCredentials(String accessKeyId, String accessKeySecret, String securityToken,
                                long expirationInMillis, long updateTimeInMillis) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.securityToken = securityToken;
        this.expirationInMills = expirationInMillis;
        this.updateTimeInMillis = updateTimeInMillis;
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

    public long getExpirationInMills() {
        return expirationInMills;
    }


    public void setExpirationInMills(long expiration) {
        this.expirationInMills = expiration;
    }


    public long getUpdateTimeInMillis() {
        return updateTimeInMillis;
    }


    public void setUpdateTimeInMillis(long lastUpdated) {
        this.updateTimeInMillis = lastUpdated;
    }

    protected String accessKeyId;
    protected String accessKeySecret;
    protected String securityToken;

    protected long expirationInMills;
    protected long updateTimeInMillis;
}
