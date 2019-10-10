package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

    private boolean testPutLogs(String logStoreName) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        StringBuilder params = new StringBuilder("APIVersion=0.6.0");
        for (int i = 0; i < 100; ++i) {
            params.append("&key").append(i).append("=").append("value").append(i);
        }
        HttpGet httpGet = new HttpGet("http://" + TEST_PROJECT + "." + credentials.getEndpoint() + "/logstores/" + logStoreName + "/track?" + params);
        try {
            HttpResponse res = httpClient.execute(httpGet);
            if (res.getStatusLine().getStatusCode() / 200 != 1) {
                return false;
            }
        } catch (Exception e) {
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
        String logStoreName = "logstore-" + getNowTimestamp();
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.SetLogStoreName(logStoreName);
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(true);
        createOrUpdateLogStore(TEST_PROJECT, logStore);

        assertTrue(testPutLogs(logStoreName));
        waitForSeconds(3);
        assertEquals(1, countLogGroupWithClientIpTag(TEST_PROJECT, logStoreName, 2));

        logStore.setEnableWebTracking(false);
        client.UpdateLogStore(TEST_PROJECT, logStore);
        waitOneMinutes();
        assertFalse(testPutLogs(logStoreName));
        waitForSeconds(3);
        assertEquals(1, countLogGroupWithClientIpTag(TEST_PROJECT, logStoreName, 2));

        logStore.setEnableWebTracking(true);
        client.UpdateLogStore(TEST_PROJECT, logStore);
        waitOneMinutes();
        assertTrue(testPutLogs(logStoreName));
        waitForSeconds(3);
        assertEquals(2, countLogGroupWithClientIpTag(TEST_PROJECT, logStoreName, 2));
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
        List<Logs.LogGroup> logGroups = pullAllLogGroups(TEST_PROJECT, logStoreName, 1);
        waitForSeconds(5);
        assertEquals(1, logGroups.size());
        Logs.LogGroup logGroup = logGroups.get(0);
        assertEquals("test-topic", logGroup.getTopic());
        assertEquals("test-source", logGroup.getSource());

        List<Logs.LogTag> logTags = logGroup.getLogTagsList();
        if (logStore.isAppendMeta()) {
            assertEquals(3, logTags.size());
        } else {
            assertEquals(1, logTags.size());
            Logs.LogTag tag = logTags.get(0);
            assertEquals("tag1", tag.getKey());
            assertEquals("hello", tag.getValue());
        }

        List<Logs.Log> logs = logGroup.getLogsList();
        assertEquals(1, logs.size());
        Logs.Log log1 = logs.get(0);
        assertEquals(1, log1.getContentsCount());
        Logs.Log.Content logContent = log1.getContents(0);
        assertEquals("field1", logContent.getKey());
        assertEquals("value1", logContent.getValue());
    }

    @Test
    public void testPullLogs() throws LogException {
        String logStoreName = "logstore-1556243198";
        List<Logs.LogGroup> logGroups = pullAllLogGroups(TEST_PROJECT, logStoreName, 1);
        assertEquals(1, logGroups.size());
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

        List<Logs.LogGroup> logGroups = pullAllLogGroups(TEST_PROJECT, logStoreName, 0);
        assertEquals(1, logGroups.size());
        Logs.LogGroup logGroup = logGroups.get(0);
        assertEquals("test-topic", logGroup.getTopic());
        assertEquals("test-source", logGroup.getSource());

        List<Logs.LogTag> logTags = logGroup.getLogTagsList();
        if (logStore.isAppendMeta()) {
            assertEquals(3, logTags.size());
        } else {
            assertEquals(1, logTags.size());
            Logs.LogTag tag = logTags.get(0);
            assertEquals("tag1", tag.getKey());
            assertEquals("hello", tag.getValue());
        }

        List<Logs.Log> logs = logGroup.getLogsList();
        assertEquals(1, logs.size());
        Logs.Log log1 = logs.get(0);
        assertEquals(1, log1.getContentsCount());
        Logs.Log.Content logContent = log1.getContents(0);
        assertEquals("field1", logContent.getKey());
        assertEquals("value1", logContent.getValue());
    }

    private static class Response {
        private int status;
        private String body;

        Response(int status, String body) {
            this.status = status;
            this.body = body;
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
            String URL = "http://" + TEST_PROJECT + "." + credentials.getEndpoint() + "/logstores/" + logStoreName + "/track";

            HttpPost httpPost = new HttpPost(URL);
            String body = log.toString();
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.addHeader(Consts.CONST_X_SLS_APIVERSION, Consts.DEFAULT_API_VESION);
            if (compress) {
                byte[] toBytes = body.getBytes("UTF-8");
                byte[] bytes = compressData(toBytes);
                ByteArrayEntity entity = new ByteArrayEntity(bytes);
                httpPost.setEntity(entity);
                entity.setContentEncoding("UTF-8");
                httpPost.addHeader(Consts.CONST_X_SLS_COMPRESSTYPE, Consts.CompressType.GZIP.toString());
                httpPost.addHeader(Consts.CONST_X_SLS_BODYRAWSIZE, String.valueOf(toBytes.length));
            } else {
                StringEntity stringEntity = new StringEntity(log.toString(), "UTF-8");
                stringEntity.setContentEncoding("UTF-8");
                httpPost.setEntity(stringEntity);
                httpPost.addHeader(Consts.CONST_X_SLS_BODYRAWSIZE, String.valueOf(body.length()));
            }
            System.out.println("Executing request " + httpPost.getRequestLine());
            for (Header header : httpPost.getAllHeaders()) {
                System.out.println(header.getName() + "=" + header.getValue());
            }
            ResponseHandler<Response> responseHandler = new ResponseHandler<Response>() {
                @Override
                public Response handleResponse(final HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    HttpEntity entity = response.getEntity();
                    String body = entity != null ? EntityUtils.toString(entity) : null;
                    return new Response(status, body);
                }
            };
            Response responseBody = httpclient.execute(httpPost, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody.status);
            System.out.println(responseBody.body);
            return responseBody;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}