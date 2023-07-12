package com.aliyun.openservices.log.common.auth;

public class StaticCredentialsProvider implements CredentialsProvider {

    public StaticCredentialsProvider(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    private Credentials credentials;

    public void setAccessKeyId(String accessKeyId) {
        credentials.setAccessKeyId(accessKeyId);
    }

    public void setAccessKeySecret(String accessKeySecret) {
        credentials.setAccessKeySecret(accessKeySecret);
    }

    public void setSecretToken(String secretToken) {
        credentials.setSecurityToken(secretToken);
    }

}
