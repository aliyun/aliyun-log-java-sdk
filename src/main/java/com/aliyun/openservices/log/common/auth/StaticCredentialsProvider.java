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
}
