package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.auth.CredentialsProvider;
import com.aliyun.openservices.log.common.auth.DefaultCredentials;
import com.aliyun.openservices.log.common.auth.StaticCredentialsProvider;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.comm.DefaultServiceClient;
import com.aliyun.openservices.log.http.comm.ServiceClient;
import com.aliyun.openservices.log.http.comm.TimeoutServiceClient;
import com.aliyun.openservices.log.request.UpdateProjectRequest;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.ClientBuilder;

public class ClientBuilderTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testClientBuilder() throws LogException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        String sourceIp = "127.2.2.2";
        String project = TEST_PROJECT;
        String endpoint = TEST_ENDPOINT;
        DefaultCredentials cred = new DefaultCredentials(credentials.getAccessKeyId(), credentials.getAccessKey());
        CredentialsProvider provider = new StaticCredentialsProvider(cred);

        ClientConfiguration config = new ClientConfiguration();
        ClientConfiguration config2 = new ClientConfiguration();
        config2.setMaxConnections(1000);
        ServiceClient serviceClient = new DefaultServiceClient(config);

        {
            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.clientConfiguration(config).sourceIp(sourceIp).build();
            Assert.assertEquals(config, client.getClientConfiguration());
            client.GetProject(project);
        }
        // multiple clientConfiguration, last config win
        {
            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.clientConfiguration(config).sourceIp(sourceIp).clientConfiguration(config2).build();
            Assert.assertEquals(config2, client.getClientConfiguration());
            client.updateProject(new UpdateProjectRequest(project, "test"));
        }
        // service client is prior to clientConfiguration
        {
            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.clientConfiguration(config).sourceIp(sourceIp).clientConfiguration(config2).serviceClient(serviceClient).build();
            Assert.assertEquals(config, client.getClientConfiguration());
            client.updateProject(new UpdateProjectRequest(project, "test"));
        }
        {
            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.build();
            client.GetProject(project);
        }
        {
            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.build();
            client.GetProject(project);
        }
        // timeout service client
        {
            {
                ClientBuilder builder = new ClientBuilder(endpoint, provider);
                Client client = builder.build();
                Field serviceClientField = Client.class.getDeclaredField("serviceClient");
                serviceClientField.setAccessible(true);
                Assert.assertEquals(serviceClientField.get(client).getClass(), DefaultServiceClient.class);
            }
            ClientConfiguration config3 = new ClientConfiguration();
            config3.setRequestTimeoutEnabled(true);
            config3.setRequestTimeout(1000);

            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.clientConfiguration(config3).build();
            Assert.assertEquals(config3, client.getClientConfiguration());
            {
                Field serviceClientField = Client.class.getDeclaredField("serviceClient");
                serviceClientField.setAccessible(true);
                Assert.assertEquals(serviceClientField.get(client).getClass(), TimeoutServiceClient.class);
            }
            client.GetProject(project);
        }

    }
}