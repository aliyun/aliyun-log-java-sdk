package com.aliyun.openservices.log.common.auth;

public class DefaultCredentails implements Credentials {
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;

    public DefaultCredentails(String accessKeyId, String accessKeySecret) {
        this(accessKeyId, accessKeySecret, null);
    }

    public DefaultCredentails(String accessKeyId, String accessKeySecret, String securityToken) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.securityToken = securityToken;
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

}
