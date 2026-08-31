package com.aliyun.openservices.log;

import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.MetricStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.request.CreateLogStoreRequest;
import com.aliyun.openservices.log.request.CreateMetricStoreRequest;
import com.aliyun.openservices.log.response.CreateLogStoreResponse;
import com.aliyun.openservices.log.response.CreateMetricStoreResponse;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
public class MetricStoreCreationTest {

    @Test
    public void testDeprecatedLogStoreOverloadUsesLabelsType() throws LogException {
        CapturingClient client = new CapturingClient();

        CreateLogStoreResponse response = client.createMetricStore(
                "project", new LogStore("metricstore", 30, 2));

        assertDefaultSubStore(client, response, 30);
    }

    @Test
    public void testDeprecatedRequestOverloadUsesLabelsType() throws LogException {
        CapturingClient client = new CapturingClient();

        CreateLogStoreResponse response = client.createMetricStore(
                new CreateLogStoreRequest("project", new LogStore("metricstore", 7, 3)));

        assertDefaultSubStore(client, response, 7);
    }

    @Test
    public void testMetricStoreRequestDoesNotCreateSubStore() throws LogException {
        CapturingClient client = new CapturingClient();

        CreateMetricStoreResponse response = client.createMetricStore(
                new CreateMetricStoreRequest("project", new MetricStore("metricstore", 30, 2)));

        assertEquals(Arrays.asList("/metricstores"), client.resourceUris);
        assertEquals("request-1", response.GetRequestId());
        JSONObject body = client.requestBodies.get(0);
        assertEquals("metricstore", body.getString("name"));
        assertEquals(30, body.getIntValue("ttl"));
        assertEquals(2, body.getIntValue("shardCount"));
    }

    private static void assertDefaultSubStore(CapturingClient client,
                                               CreateLogStoreResponse response, int ttl) {
        assertEquals(Arrays.asList("/logstores", "/logstores/metricstore/substores"), client.resourceUris);
        assertEquals("request-1", response.GetRequestId());
        assertEquals("Metrics", client.requestBodies.get(0).getString("telemetryType"));

        JSONObject subStore = client.requestBodies.get(1);
        assertEquals("prom", subStore.getString("name"));
        assertEquals(ttl, subStore.getIntValue("ttl"));
        assertEquals(2, subStore.getIntValue("sortedKeyCount"));
        assertEquals(2, subStore.getIntValue("timeIndex"));

        JSONArray keys = subStore.getJSONArray("keys");
        assertEquals(4, keys.size());
        String[] names = {"__name__", "__labels__", "__time_nano__", "__value__"};
        String[] types = {"text", "labels", "long", "double"};
        for (int i = 0; i < names.length; i++) {
            assertEquals(names[i], keys.getJSONObject(i).getString("name"));
            assertEquals(types[i], keys.getJSONObject(i).getString("type"));
        }
    }

    private static class CapturingClient extends Client {
        private final List<String> resourceUris = new ArrayList<String>();
        private final List<JSONObject> requestBodies = new ArrayList<JSONObject>();

        private CapturingClient() {
            super("http://mock-sls.example.com", "access-key-id", "access-key-secret");
        }

        @Override
        protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
                                           Map<String, String> parameters, Map<String, String> headers, byte[] body)
                throws LogException {
            assertEquals("project", project);
            assertEquals(HttpMethod.POST, method);
            assertEquals(Consts.CONST_SLS_JSON, headers.get(Consts.CONST_CONTENT_TYPE));
            resourceUris.add(resourceUri);
            requestBodies.add(JSONObject.parseObject(new String(body, StandardCharsets.UTF_8)));

            ResponseMessage response = new ResponseMessage();
            response.addHeader(Consts.CONST_X_SLS_REQUESTID, "request-" + resourceUris.size());
            return response;
        }
    }
}
