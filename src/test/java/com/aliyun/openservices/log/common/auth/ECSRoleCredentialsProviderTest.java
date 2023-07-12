package com.aliyun.openservices.log.common.auth;

import org.junit.Assert;
import org.junit.Test;

public class ECSRoleCredentialsProviderTest {

    @Test
    public void test() {
        ECSRoleCredentialsProvider provider = new ECSRoleCredentialsProvider("");
        try{
            provider.getCredentials();
            Assert.fail("ecs role provider get credentials");
        }catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
