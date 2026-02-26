package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.exception.LogException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GetWebtrackingTest extends BaseDataTest {
    @Test
    public void testGetWebTracking() throws LogException {
        enableIndex();
        int count = mockGetRequest();

        int pull = verifyPull();
        assertEquals(count, pull);

        int get = verifyGet();
        assertEquals(count, get);
    }

    protected int mockGetRequest() {
        int count = randomBetween(1, 5);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        StringBuilder params = new StringBuilder("APIVersion=0.6.0");
        for (int i = 1; i <= 2; i++) {
            params.append("&key-").append(i).append("=").append("value-").append(i);
        }
        HttpGet httpGet = new HttpGet("http://" + project + "." + TEST_ENDPOINT + "/logstores/"
                + logStore.GetLogStoreName() + "/track?" + params);
        try {
            for (int i = 0; i < count; i++) {
                HttpResponse res = httpClient.execute(httpGet);
                if (res.getCode() / 200 != 1) {
                    fail();
                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    // do not care
                }
            }
        }
        waitForSeconds(10);
        return count;
    }

}
