package com.aliyun.openservices.log.common.auth;

import com.aliyun.openservices.log.Client;
import org.junit.Assert;
import org.junit.Test;

public class ClientTest {

    @Test
    public void test() {
        Client client = new Client("cn-hangzhou", "fake-ak-id", "fake-ak-key");
        // static provider should return the same object of credentials
        Assert.assertEquals(client.getCredentials(),client.getCredentials());
        // back-forward compatibility
        Assert.assertEquals(client.getCredentials().getAccessKeyId(), client.getAccessId());
        Assert.assertEquals(client.getCredentials().getAccessKeySecret(), client.getAccessKey());
        Assert.assertEquals(client.getCredentials().getSecurityToken(), client.getSecurityToken());

        client.setAccessId("fake-2");
        Assert.assertEquals(client.getCredentials().getAccessKeyId(), client.getAccessId());
        Assert.assertEquals(client.getCredentials().getAccessKeyId(), "fake-2");

    }
}
