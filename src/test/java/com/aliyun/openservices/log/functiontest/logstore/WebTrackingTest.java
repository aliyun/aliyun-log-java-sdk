package com.aliyun.openservices.log.functiontest.logstore;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.FastLog;
import com.aliyun.openservices.log.common.FastLogContent;
import com.aliyun.openservices.log.common.FastLogGroup;
import com.aliyun.openservices.log.common.FastLogTag;
import com.aliyun.openservices.log.common.LogStore;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WebTrackingTest extends LogTest {

    private boolean testGetWebtracking(String logStoreName) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        StringBuilder params = new StringBuilder("APIVersion=0.6.0");
        for (int i = 0; i < 100; ++i) {
            params.append("&key").append(i).append("=").append("value").append(i);
        }
        HttpGet httpGet = new HttpGet("http://" + TEST_PROJECT + "." + TEST_ENDPOINT + "/logstores/" + logStoreName + "/track?" + params);
        try {
            HttpResponse res = httpClient.execute(httpGet);
            if (res.getCode() / 200 != 1) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                // ignore it
            }
        }
        return true;
    }

    @Override
    protected void checkLogGroup(FastLogGroup logGroup) {
        assertEquals(1, logGroup.getLogsCount());
        for (FastLog log : logGroup.getLogs()) {
            assertEquals(log.getContentsCount(), 100);
            Map<String, String> toMap = new HashMap<String, String>(100);
            for (int i = 0; i < 100; i++) {
                FastLogContent content = log.getContents(i);
                toMap.put(content.getKey(), content.getValue());
            }
            for (int i = 0; i < 100; i++) {
                assertEquals("value" + i, toMap.get("key" + i));
            }
        }
    }

    @Test
    public void testWebTracking() throws Exception {
        String logStoreName = "logstore-" + getNowTimestamp();
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.SetLogStoreName(logStoreName);
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(true);
        createOrUpdateLogStore(TEST_PROJECT, logStore);

        assertTrue(testGetWebtracking(logStoreName));
        waitForSeconds(3);
        // assertEquals(1, countLogGroupWithClientIpTag(TEST_PROJECT, logStoreName, 2));

        logStore.setEnableWebTracking(false);
        client.UpdateLogStore(TEST_PROJECT, logStore);
        waitOneMinutes();
        assertFalse(testGetWebtracking(logStoreName));
        waitForSeconds(3);
        // assertEquals(1, countLogGroupWithClientIpTag(TEST_PROJECT, logStoreName, 2));

        logStore.setEnableWebTracking(true);
        client.UpdateLogStore(TEST_PROJECT, logStore);
        waitOneMinutes();
        assertTrue(testGetWebtracking(logStoreName));
        waitForSeconds(3);
        // assertEquals(2, countLogGroupWithClientIpTag(TEST_PROJECT, logStoreName, 2));
    }

    @Test
    public void testPostWebTracking() throws Exception {
        String logStoreName = "logstore-" + getNowTimestamp();
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(1);
        logStore.SetLogStoreName(logStoreName);
        logStore.setEnableWebTracking(false);
        logStore.setAppendMeta(randomBoolean());
        createOrUpdateLogStore(TEST_PROJECT, logStore);

        JSONObject object = new JSONObject();
        object.put("__topic__", "test-topic");
        object.put("__source__", "test-source");
        JSONObject tags = new JSONObject();
        tags.put("tag1", "hello");
        object.put("__tags__", tags);
        JSONArray array = new JSONArray();
        JSONObject log = new JSONObject();
        log.put("field1", "value1");
        log.put("__time__", System.currentTimeMillis() / 1000);
        array.add(log);
        object.put("__logs__", array);
        Response response = postTestLogs(logStoreName, object, false);
        // should fail as web tracking is not enabled
        assertEquals(401, response.status);

        logStore.setEnableWebTracking(true);
        client.UpdateLogStore(TEST_PROJECT, logStore);
        waitOneMinutes();

        postTestLogs(logStoreName, object, false);
        List<FastLogGroup> logGroups = pullAllLogGroups(TEST_PROJECT, logStoreName, 1);
        waitForSeconds(5);
        assertEquals(1, logGroups.size());
        FastLogGroup logGroup = logGroups.get(0);
        assertEquals("test-topic", logGroup.getTopic());
        assertEquals("test-source", logGroup.getSource());
        if (logStore.isAppendMeta()) {
            assertTrue(logGroup.getLogTagsCount() >= 2);
        } else {
            assertEquals(1, logGroup.getLogTagsCount());
            FastLogTag tag = logGroup.getLogTags(0);
            assertEquals("tag1", tag.getKey());
            assertEquals("hello", tag.getValue());
        }

        List<FastLog> logs = logGroup.getLogs();
        assertEquals(1, logs.size());
        FastLog log1 = logs.get(0);
        assertEquals(1, log1.getContentsCount());
        FastLogContent logContent = log1.getContents(0);
        assertEquals("field1", logContent.getKey());
        assertEquals("value1", logContent.getValue());
    }

    @Test
    public void testCompressLogs() throws Exception {
        String logStoreName = "logstore-" + getNowTimestamp();
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(1);
        logStore.SetLogStoreName(logStoreName);
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(randomBoolean());
        createOrUpdateLogStore(TEST_PROJECT, logStore);

        JSONObject object = new JSONObject();
        object.put("__topic__", "test-topic");
        object.put("__source__", "test-source");
        JSONObject tags = new JSONObject();
        tags.put("tag1", "hello");
        object.put("__tags__", tags);
        JSONArray array = new JSONArray();
        JSONObject log = new JSONObject();
        log.put("field1", "value1");
        log.put("__time__", System.currentTimeMillis() / 1000);
        array.add(log);
        object.put("__logs__", array);
        Response response = postTestLogs(logStoreName, object, true);
        assertEquals(200, response.status);
        waitForSeconds(5);

        List<FastLogGroup> logGroups = pullAllLogGroups(TEST_PROJECT, logStoreName, 1);
        assertEquals(1, logGroups.size());
        FastLogGroup logGroup = logGroups.get(0);
        assertEquals("test-topic", logGroup.getTopic());
        assertEquals("test-source", logGroup.getSource());

        if (logStore.isAppendMeta()) {
            assertTrue(logGroup.getLogTagsCount() >= 2);
        } else {
            assertEquals(1, logGroup.getLogTagsCount());
            FastLogTag tag = logGroup.getLogTags(0);
            assertEquals("tag1", tag.getKey());
            assertEquals("hello", tag.getValue());
        }

        List<FastLog> logs = logGroup.getLogs();
        assertEquals(1, logs.size());
        FastLog log1 = logs.get(0);
        assertEquals(1, log1.getContentsCount());
        FastLogContent logContent = log1.getContents(0);
        assertEquals("field1", logContent.getKey());
        assertEquals("value1", logContent.getValue());
    }

    private static class Response {
        private final int status;

        Response(int status) {
            this.status = status;
        }
    }

    private byte[] compressData(byte[] data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
        Deflater compressor = new Deflater();
        compressor.setInput(data);
        compressor.finish();

        byte[] buf = new byte[10240];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            out.write(buf, 0, count);
        }
        return out.toByteArray();
    }

    private Response postTestLogs(String logStoreName, JSONObject log, boolean compress) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String URL = "http://" + TEST_PROJECT + "." + TEST_ENDPOINT + "/logstores/" + logStoreName + "/track";

            HttpPost httpPost = new HttpPost(URL);
            String body = log.toString();
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.addHeader(Consts.CONST_X_SLS_APIVERSION, Consts.DEFAULT_API_VESION);
            if (compress) {
                byte[] toBytes = body.getBytes("UTF-8");
                byte[] bytes = compressData(toBytes);
                ByteArrayEntity entity = new ByteArrayEntity(bytes, ContentType.APPLICATION_JSON, "UTF-8");
                httpPost.setEntity(entity);
                httpPost.addHeader(Consts.CONST_X_SLS_COMPRESSTYPE, Consts.CompressType.GZIP.toString());
                httpPost.addHeader(Consts.CONST_X_SLS_BODYRAWSIZE, String.valueOf(toBytes.length));
            } else {
                StringEntity stringEntity = new StringEntity(log.toString(), ContentType.APPLICATION_JSON, "UTF-8", false);
                httpPost.setEntity(stringEntity);
                httpPost.addHeader(Consts.CONST_X_SLS_BODYRAWSIZE, String.valueOf(body.length()));
            }
            HttpClientResponseHandler<Response> responseHandler = new HttpClientResponseHandler<Response>() {
                @Override
                public Response handleResponse(final ClassicHttpResponse response) throws HttpException, IOException {
                    int status = response.getCode();
                    return new Response(status);
                }
            };
            return httpclient.execute(httpPost, responseHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testProjectPolicyRejectWebtracking() throws Exception {
        String logStoreName = "logstore-webtracking";
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(1);
        logStore.SetLogStoreName(logStoreName);
        logStore.setEnableWebTracking(true);
        client.CreateLogStore(TEST_PROJECT, logStore);
        String policyText = "{" +
                "   \"Version\": \"1\",\n" +
                "   \"Statement\": [{" +
                "       \"Action\": [\"log:WebTracking\", \"log:PutWebTracking\"]," +
                "       \"Resource\": \"*\",\n" +
                "       \"Condition\": {" +
                "         \"NotIpAddress\": {" +
                "            \"acs:SourceIp\": [\"10.0.0.1\"]" +
                "          }" +
                "       }," +
                "       \"Effect\": \"Deny\"" +
                "   }]" +
                " }";
        client.setProjectPolicy(TEST_PROJECT, policyText);
        waitForSeconds(60);

        JSONObject object = new JSONObject();
        object.put("__topic__", "test-topic");
        object.put("__source__", "test-source");
        JSONObject tags = new JSONObject();
        tags.put("tag1", "hello");
        object.put("__tags__", tags);
        JSONArray array = new JSONArray();
        JSONObject log = new JSONObject();
        log.put("field1", "value1");
        log.put("__time__", System.currentTimeMillis() / 1000);
        array.add(log);
        object.put("__logs__", array);
        Response response = postTestLogs(logStoreName, object, true);
        assertEquals(401, response.status);
        assertFalse(testGetWebtracking(logStoreName));

        client.deleteProjectPolicy(TEST_PROJECT);
        waitOneMinutes();
        response = postTestLogs(logStoreName, object, true);
        assertEquals(200, response.status);
        assertTrue(testGetWebtracking(logStoreName));
    }
}