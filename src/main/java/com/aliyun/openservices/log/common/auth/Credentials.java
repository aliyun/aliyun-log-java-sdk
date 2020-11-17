package com.aliyun.openservices.log.common.auth;

public interface Credentials {
    String getAccessKeyId();

    String getAccessKeySecret();

    String getSecurityToken();

    void setAccessKeyId(String accessKeyId);

    void setAccessKeySecret(String accessKeySecret);

    void setSecurityToken(String securityToken);
}
