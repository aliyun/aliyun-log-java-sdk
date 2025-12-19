package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.auth.CredentialsProvider;
import com.aliyun.openservices.log.common.auth.DefaultCredentials;
import com.aliyun.openservices.log.common.auth.StaticCredentialsProvider;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.MetaAPIBaseFunctionTest;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.comm.DefaultServiceClient;
import com.aliyun.openservices.log.http.comm.ServiceClient;
import com.aliyun.openservices.log.response.ListLogStoresResponse;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ClientCtorTest extends MetaAPIBaseFunctionTest {

    @Test
    public void TestClientCtor() throws LogException, InterruptedException {
        CredentialsProvider provider = new StaticCredentialsProvider(
                new DefaultCredentials(credentials.getAccessKeyId(), credentials.getAccessKey()));
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setMaxConnections(10);
        clientConfig.setConnectionTimeout(5 * 1000);
        clientConfig.setSocketTimeout(5 * 1000);
        ServiceClient serviceClient = new DefaultServiceClient(clientConfig);

        String logstore = "test";
        client.CreateLogStore(TEST_PROJECT, new LogStore(logstore, 1, 1));
        Thread.sleep(1000 * 10);

        List<LogItem> items = new ArrayList<LogItem>();
        ArrayList<LogContent> content = new ArrayList<LogContent>();
        content.add(new LogContent("a", "b"));
        items.add(new LogItem((int) (System.currentTimeMillis() / 1000), content));

        Thread.sleep(1000 * 60);
        {
            Client client = new Client(TEST_ENDPOINT, provider, serviceClient, null);
            client.PutLogs(TEST_PROJECT, logstore, "", items, "127.0.0.1");
            ListLogStoresResponse resp = client.ListLogStores(TEST_PROJECT, 0, 100, logstore);
            Assert.assertTrue(resp.GetCount() >= 1);
        }

        {
            Client client2 = new Client(TEST_ENDPOINT, provider, clientConfig, null);
            client2.PutLogs(TEST_PROJECT, logstore, "", items, "127.0.0.1");
            ListLogStoresResponse resp = client2.ListLogStores(TEST_PROJECT, 0, 100, logstore);
            Assert.assertTrue(resp.GetCount() >= 1);
        }

    }
}
