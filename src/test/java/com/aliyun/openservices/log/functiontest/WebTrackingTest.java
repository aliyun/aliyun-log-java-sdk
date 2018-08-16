package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Logs;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WebTrackingTest extends BaseMetadataTest {

    private static final String TEST_PROJECT = "project-test-webtracking";
    private static final String TEST_LOGSTORE = "logstore1";

    private boolean testPutLogs() {
        HttpClient httpClient = HttpClientBuilder.create().build();
        StringBuilder params = new StringBuilder("APIVersion=0.6.0");
        for (int i = 0; i < 100; ++i) {
            params.append("&key").append(i).append("=").append("value").append(i);
        }
        HttpGet httpGet = new HttpGet("http://" + TEST_PROJECT + "." + client.getEndpoint() + "/logstores/" + TEST_LOGSTORE + "/track?" + params);
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

    @Override
    protected void checkLogGroup(Logs.LogGroup logGroup) {
        assertEquals(1, logGroup.getLogsCount());
        for (Logs.Log log : logGroup.getLogsList()) {
            assertEquals(log.getContentsCount(), 100);
            Map<String, String> toMap = new HashMap<String, String>(100);
            for (int i = 0; i < 100; i++) {
                Logs.Log.Content content = log.getContents(i);
                toMap.put(content.getKey(), content.getValue());
            }
            for (int i = 0; i < 100; i++) {
                assertEquals("value" + i, toMap.get("key" + i));
            }
        }
    }

    @Test
    public void testWebTracking() throws Exception {
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.SetLogStoreName(TEST_LOGSTORE);
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(true);
        reCreateLogStore(TEST_PROJECT, logStore);

        assertTrue(testPutLogs());
        waitForSeconds(3);
        assertEquals(1, countAppended(TEST_PROJECT, TEST_LOGSTORE, 2));

        logStore.setEnableWebTracking(false);
        client.UpdateLogStore(TEST_PROJECT, logStore);
        waitOneMinutes();
        assertFalse(testPutLogs());
        waitForSeconds(3);
        assertEquals(1, countAppended(TEST_PROJECT, TEST_LOGSTORE, 2));

        logStore.setEnableWebTracking(true);
        client.UpdateLogStore(TEST_PROJECT, logStore);
        waitOneMinutes();
        assertTrue(testPutLogs());
        waitForSeconds(3);
        assertEquals(2, countAppended(TEST_PROJECT, TEST_LOGSTORE, 2));
    }
}
