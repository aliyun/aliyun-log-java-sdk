package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.LogStore;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WebTrackingTest extends FunctionTest {

    private static final String TEST_PROJECT = "project1";
    private static final String TEST_LOGSTORE = "logstore1";

    private boolean testPutLogs() {
        HttpClient httpClient = HttpClientBuilder.create().build();
        StringBuilder params = new StringBuilder("APIVersion=0.6.0");
        for (int i = 0; i < 100; ++i) {
            params.append("&key").append(i).append("=").append("value").append(i);
        }
        Config config = readConfig();
        HttpGet httpGet = new HttpGet("http://" + TEST_PROJECT + "." + config.getEndpoint() + "/logstores/" + TEST_LOGSTORE + "/track?" + params);
        try {
            HttpResponse res = httpClient.execute(httpGet);
            if (res.getStatusLine().getStatusCode() / 200 != 1) {
                return false;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        return true;
    }

    @Test
    public void testWebTracking() throws Exception {
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.SetLogStoreName(TEST_LOGSTORE);
        logStore.setEnableWebTracking(true);
        reCreateLogStore(TEST_PROJECT, logStore);

        assertTrue(testPutLogs());

        logStore.setEnableWebTracking(false);
        client.UpdateLogStore(TEST_PROJECT, logStore);
        waitOneMinutes();
        assertFalse(testPutLogs());

        logStore.setEnableWebTracking(true);
        client.UpdateLogStore(TEST_PROJECT, logStore);
        waitOneMinutes();
        assertTrue(testPutLogs());
    }
}
