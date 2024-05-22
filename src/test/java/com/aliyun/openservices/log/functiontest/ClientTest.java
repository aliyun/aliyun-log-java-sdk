package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.auth.CredentialsProvider;
import com.aliyun.openservices.log.common.auth.DefaultCredentials;
import com.aliyun.openservices.log.common.auth.StaticCredentialsProvider;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.comm.DefaultServiceClient;
import com.aliyun.openservices.log.http.comm.ServiceClient;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientTest {
    static class EnvVar{
        public String accessKeyID;
        public String accessKeySecret;
        public String endpoint;
        public String project;
        public String logStore;
        private static boolean isEmpty(String str) {
            return str == null || str.isEmpty();
        }
        EnvVar() {
            Map<String, String> envVariables = System.getenv();
            this.accessKeyID = envVariables.get("LOG_TEST_ACCESS_KEY_ID");
            this.accessKeySecret = envVariables.get("LOG_TEST_ACCESS_KEY_SECRET");
            this.endpoint = envVariables.get("LOG_TEST_ENDPOINT");
            this.project = envVariables.get("LOG_TEST_PROJECT");
            this.logStore = envVariables.get("LOG_TEST_LOGSTORE");
            if (isEmpty(this.accessKeyID) || isEmpty(this.accessKeySecret) || isEmpty(this.endpoint)) {
                throw new RuntimeException("accessKeyId / accessKeySecret / endpoint must be set in environment variables");
            }
        }
    }
    private EnvVar env = new EnvVar();

    @Test
    public void TestClientCtor() throws LogException {
        CredentialsProvider provider = new StaticCredentialsProvider(new DefaultCredentials(env.accessKeyID,env.accessKeySecret));
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setMaxConnections(10);
        clientConfig.setConnectionTimeout(5 * 1000);
        clientConfig.setSocketTimeout(5 * 1000);
        ServiceClient serviceClient = new DefaultServiceClient(clientConfig);

        List<LogItem> items = new ArrayList<LogItem>();
        ArrayList<LogContent> content = new ArrayList<LogContent>();
        content.add(new LogContent("a", "b"));
        items.add(new LogItem((int) (System.currentTimeMillis() / 1000), content));

        {
            Client client = new Client(env.endpoint, provider, serviceClient, null);
            client.PutLogs(env.project, env.logStore, "", items, "127.0.0.1");
            ListLogStoresResponse resp = client.ListLogStores(env.project, 0, 100, env.logStore);
            Assert.assertTrue(resp.GetCount() >= 1);
        }

        {
            Client client2 = new Client(env.endpoint, provider, clientConfig, null);
            client2.PutLogs(env.project, env.logStore, "", items, "127.0.0.1");
            ListLogStoresResponse resp = client2.ListLogStores(env.project, 0, 100, env.logStore);
            Assert.assertTrue(resp.GetCount() >= 1);
        }

    }
}
