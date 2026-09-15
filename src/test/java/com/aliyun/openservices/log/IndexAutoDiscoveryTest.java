package com.aliyun.openservices.log;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.IndexLine;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class IndexAutoDiscoveryTest {

    @Test
    public void testDefaultsAndLegacyResponse() throws LogException {
        IndexLine line = new IndexLine();
        assertFalse(line.isAutoKeyDetect());
        assertTrue(line.getAutoTextKeys().isEmpty());
        assertFalse(line.ToRequestJson().getBooleanValue("auto_key_detect"));
        assertTrue(line.ToRequestJson().getJSONArray("auto_text_keys").isEmpty());

        line.setAutoKeyDetect(true);
        line.setAutoTextKeys(Arrays.asList("host"));
        line.FromJsonString("{\"token\":[],\"caseSensitive\":false}");
        assertFalse(line.isAutoKeyDetect());
        assertTrue(line.getAutoTextKeys().isEmpty());
    }

    @Test
    public void testCopiesAndIndependentSerialization() throws LogException {
        IndexLine line = new IndexLine();
        List<String> fields = new ArrayList<String>(Arrays.asList("host"));
        line.setAutoTextKeys(fields);
        fields.clear();
        assertEquals(Arrays.asList("host"), line.getAutoTextKeys());
        IndexLine copy = new IndexLine(line);
        line.getAutoTextKeys().clear();
        assertEquals(Arrays.asList("host"), copy.getAutoTextKeys());
        assertFalse(copy.isAutoKeyDetect());
        assertEquals(Arrays.asList("host"), copy.ToRequestJson().getJSONArray("auto_text_keys"));
        copy.setAutoKeyDetect(true);
        assertEquals(Arrays.asList("host"), copy.ToRequestJson().getJSONArray("auto_text_keys"));
        copy.setAutoTextKeys(null);
        assertTrue(copy.ToRequestJson().getJSONArray("auto_text_keys").isEmpty());

        line.FromJsonString("{\"token\":[],\"caseSensitive\":false,"
                + "\"auto_key_detect\":false,\"auto_text_keys\":[\"host\"]}");
        assertEquals(Arrays.asList("host"), line.getAutoTextKeys());
        assertFalse(line.isAutoKeyDetect());
        assertEquals(Arrays.asList("host"), line.ToRequestJson().getJSONArray("auto_text_keys"));
    }

    @Test
    public void testCreateGetUpdatePreservesAndReplacesFields() throws LogException {
        assertCreateGetUpdatePreservesAndReplacesFields(true);
    }

    @Test
    public void testCreateGetUpdateWithDetectionDisabled() throws LogException {
        assertCreateGetUpdatePreservesAndReplacesFields(false);
    }

    private void assertCreateGetUpdatePreservesAndReplacesFields(boolean autoKeyDetect) throws LogException {
        CapturingClient client = new CapturingClient(autoKeyDetect);
        List<String> fields = Arrays.asList("host", "request_id", "latency");
        Index index = new Index();
        index.GetLine().SetToken(Arrays.asList(",", " "));
        index.GetLine().setAutoKeyDetect(autoKeyDetect);
        index.GetLine().setAutoTextKeys(fields);
        client.CreateIndex("my-project", "my-logs", index);

        index = client.GetIndex("my-project", "my-logs").GetIndex();
        assertEquals(autoKeyDetect, index.GetLine().isAutoKeyDetect());
        assertEquals(fields, index.GetLine().getAutoTextKeys());
        index.GetLine().SetCaseSensitive(true);
        client.UpdateIndex("my-project", "my-logs", index);
        index.GetLine().setAutoTextKeys(Arrays.asList("host"));
        client.UpdateIndex("my-project", "my-logs", index);
        index.GetLine().setAutoTextKeys(Collections.<String>emptyList());
        client.UpdateIndex("my-project", "my-logs", index);
        index.GetLine().setAutoTextKeys(fields);
        index.GetLine().setAutoKeyDetect(false);
        client.UpdateIndex("my-project", "my-logs", index);

        assertEquals(Arrays.asList(HttpMethod.POST, HttpMethod.GET, HttpMethod.PUT,
                HttpMethod.PUT, HttpMethod.PUT, HttpMethod.PUT), client.methods);
        for (JSONObject body : client.requestLines.subList(0, 2)) {
            assertEquals(autoKeyDetect, body.getBooleanValue("auto_key_detect"));
            assertEquals(fields, body.getJSONArray("auto_text_keys"));
        }
        assertTrue(client.requestLines.get(1).getBooleanValue("caseSensitive"));
        assertEquals(Arrays.asList("host"), client.requestLines.get(2).getJSONArray("auto_text_keys"));
        assertTrue(client.requestLines.get(3).getJSONArray("auto_text_keys").isEmpty());
        assertFalse(client.requestLines.get(4).getBooleanValue("auto_key_detect"));
        assertEquals(fields, client.requestLines.get(4).getJSONArray("auto_text_keys"));
    }

    private static class CapturingClient extends Client {
        private final List<HttpMethod> methods = new ArrayList<HttpMethod>();
        private final List<JSONObject> requestLines = new ArrayList<JSONObject>();
        private final boolean autoKeyDetect;

        private CapturingClient(boolean autoKeyDetect) {
            super("http://mock-sls.example.com", "access-key-id", "access-key-secret");
            this.autoKeyDetect = autoKeyDetect;
        }

        @Override
        protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
                                           Map<String, String> parameters, Map<String, String> headers,
                                           byte[] body) {
            assertEquals("my-project", project);
            assertEquals("/logstores/my-logs/index", resourceUri);
            methods.add(method);
            ResponseMessage response = new ResponseMessage();
            response.setStatusCode(200);
            response.addHeader(Consts.CONST_X_SLS_REQUESTID, "test-request-id");
            if (method == HttpMethod.GET) {
                response.SetBody(("{\"line\":{\"token\":[\",\",\" \"],\"caseSensitive\":false,"
                        + "\"auto_key_detect\":" + autoKeyDetect
                        + ",\"auto_text_keys\":[\"host\",\"request_id\",\"latency\"]}}")
                        .getBytes(StandardCharsets.UTF_8));
            } else {
                requestLines.add(JSONObject.parseObject(new String(body, StandardCharsets.UTF_8))
                        .getJSONObject("line"));
            }
            return response;
        }
    }
}
