package com.aliyun.openservices.log;

import com.aliyun.openservices.log.common.auth.CredentialsProvider;
import com.aliyun.openservices.log.common.auth.DefaultCredentials;
import com.aliyun.openservices.log.common.auth.StaticCredentialsProvider;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.comm.DefaultServiceClient;
import com.aliyun.openservices.log.http.comm.ServiceClient;
import com.aliyun.openservices.log.http.comm.TimeoutServiceClient;
import com.aliyun.openservices.log.request.UpdateProjectRequest;
import junit.framework.TestCase;

import static com.aliyun.openservices.log.Client.buildServiceClient;

public class ClientBuilderTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testClientBuilder() throws LogException {
        String endpoint = System.getenv("LOG_TEST_ENDPOINT");
        String accessKeyId = System.getenv("LOG_TEST_ACCESS_KEY_ID");

        String accessKeySecret = System.getenv("LOG_TEST_ACCESS_KEY_SECRET");
        String sourceIp = "127.2.2.2";
        String project = System.getenv("LOG_TEST_PROJECT");
        DefaultCredentials credentials = new DefaultCredentials(accessKeyId, accessKeySecret);
        CredentialsProvider provider = new StaticCredentialsProvider(credentials);

        ClientConfiguration config = new ClientConfiguration();
        ClientConfiguration config2 = new ClientConfiguration();
        config2.setMaxConnections(1000);
        ServiceClient serviceClient = new DefaultServiceClient(config);

        {
            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.clientConfiguration(config).sourceIp(sourceIp).build();
            assertEquals(config, client.getClientConfiguration());
            client.GetProject(project);
        }
        // multiple clientConfiguration, last config win
        {
            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.clientConfiguration(config).sourceIp(sourceIp).clientConfiguration(config2).build();
            assertEquals(config2, client.getClientConfiguration());
            client.updateProject(new UpdateProjectRequest(project, "test"));
        }
        // service client is prior to clientConfiguration
        {
            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.clientConfiguration(config).sourceIp(sourceIp).clientConfiguration(config2).serviceClient(serviceClient).build();
            assertEquals(config, client.getClientConfiguration());
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
            ClientConfiguration config3 = new ClientConfiguration();
            config3.setRequestTimeoutEnabled(true);
            config3.setRequestTimeout(1000);
            assertEquals(buildServiceClient(config3).getClass(), TimeoutServiceClient.class);

            ClientBuilder builder = new ClientBuilder(endpoint, provider);
            Client client = builder.clientConfiguration(config3).build();
            assertEquals(config3, client.getClientConfiguration());
            client.GetProject(project);
        }

    }
}