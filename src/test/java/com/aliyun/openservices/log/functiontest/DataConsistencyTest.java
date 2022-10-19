package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.LZ4Encoder;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetContextLogsResponse;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import java.util.ArrayList;
import java.util.zip.Deflater;

import static org.junit.Assert.*;

public class DataConsistencyTest extends BaseDataTest {
    //putLogs->pullLogs
    @Test
    public void testPutLogs() throws LogException {
        enableIndex();
        int count = prepareLogs();
        int logGroupSize = verifyPull();
        assertEquals(count, logGroupSize);
    }

    //putLogs->getLogstoreLogs/getHistogram
    @Test
    public void testGetLogs() throws LogException {
        enableIndex();
        int count = prepareLogs();
        int totalSize = verifyGet();
        assertEquals(count * 10, totalSize);

        GetHistogramsResponse histograms = client.GetHistograms(project, logStore.GetLogStoreName(),
                timestamp - 1800, timestamp + 1800, "", "");
        assertEquals(count * 10, histograms.GetTotalCount());
        assertEquals(true, histograms.IsCompleted());
    }

    //putLogs->getContextLogs
    @Test
    public void testGetContextLogs() throws LogException {
        enableIndex();
        int count = prepareLogs();
        String startPackID = PACK_ID_PREFIX + (count / 2);
        String startPackMeta;
        GetLogsResponse logs = client.GetLogs(project, logStore.GetLogStoreName(),
                timestamp - 1800, timestamp + 1800, "", "__tag__:__pack_id__:" + startPackID + "|with_pack_meta");

        PackInfo info = extractPackInfo(logs.getLogs().get(0));
        startPackMeta = info.packMeta;
        GetContextLogsResponse contextLogs = client.getContextLogs(project, logStore.GetLogStoreName(),
                startPackID, startPackMeta, 10, 10);
        for (QueriedLog log : contextLogs.getLogs()) {
            //assertEquals("test-source", log.mSource); 暂时拿不到source
            for (LogContent mContent : log.mLogItem.mContents) {
                String mKey = mContent.mKey;
                String mValue = mContent.mValue;
                if (mKey.startsWith("key-")) {
                    if (!mValue.startsWith("value-") || !mKey.substring(4).equals(mValue.substring(6))) {
                        throw new RuntimeException("Inconsistent data");
                    }
                } else if ("__tag__:__extra_tag__".equals(mKey)) {
                    assertEquals("extra_tag_value", mValue);
                } else if ("__tag__:__pack_id__".equals(mKey)) {
                    assertTrue(mValue.startsWith(PACK_ID_PREFIX));
                }
            }
        }
        assertTrue(contextLogs.isCompleted());
        assertEquals(contextLogs.getTotalLines(), 21);
        assertEquals(contextLogs.getBackLines(), 10);
        assertEquals(contextLogs.getForwardLines(), 10);
        assertEquals(contextLogs.getLogs().size(), 21);
    }

    @Test
    public void testGetWebTracking() throws LogException {
        enableIndex();
        int count = mockGetRequest();

        int pull = verifyPull();
        assertEquals(count, pull);

        int get = verifyGet();
        assertEquals(count, get);
    }

    @Test
    public void testPostWebTracking() throws LogException {
        enableIndex();
        int count = mockPostRequest(Consts.CompressType.GZIP);

        int pull = verifyPull();
        assertEquals(count, pull);

        int get = verifyGet();
        assertEquals(count, get);
    }

    @Test
    public void testPostWebTrackingForLZ4() throws LogException {
        enableIndex();
        int count = mockPostRequest(Consts.CompressType.LZ4);

        int pull = verifyPull();
        assertEquals(count, pull);

        int get = verifyGet();
        assertEquals(count, get);
    }

    protected int mockGetRequest() {
        int count = randomBetween(50, 100);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        StringBuilder params = new StringBuilder("APIVersion=0.6.0");
        for (int i = 1; i <= 10; i++) {
            params.append("&key-").append(i).append("=").append("value-").append(i);
        }
        HttpGet httpGet = new HttpGet("http://" + project + "." + credentials.getEndpoint() + "/logstores/" + logStore.GetLogStoreName() + "/track?" + params);
        try {
            for (int i = 0; i < count; i++) {
                HttpResponse res = httpClient.execute(httpGet);
                if (res.getStatusLine().getStatusCode() / 200 != 1) {
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

    private int mockPostRequest(Consts.CompressType type) {
        int count = randomBetween(50, 100);
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject log = new JSONObject();
        for (int j = 1; j <= 10; j++) {
            log.put("key-" + j, "value-" + j);
        }
        array.add(log);
        object.put("__logs__", array);
        if (Consts.CompressType.GZIP.equals(type)) {
            for (int i = 0; i < count; i++) {
                postTestLogs(object, true, Consts.CompressType.GZIP);
            }
        } else {
            for (int i = 0; i < count; i++) {
                postTestLogs(object, true, Consts.CompressType.LZ4);
            }
        }
        waitForSeconds(10);
        return count;
    }

    private PackInfo extractPackInfo(QueriedLog log) {
        PackInfo info = new PackInfo("", "");
        ArrayList<LogContent> contents = log.GetLogItem().GetLogContents();
        for (int i = 0; i < contents.size(); ++i) {
            LogContent content = contents.get(i);
            if ("__tag__:__pack_id__".equals(content.GetKey())) {
                info.packID = content.GetValue();
            } else if ("__pack_meta__".equals(content.GetKey())) {
                info.packMeta = content.GetValue();
            }
        }
        return info;
    }

    private class PackInfo {
        public String packID;
        public String packMeta;

        public PackInfo(String id, String meta) {
            this.packID = id;
            this.packMeta = meta;
        }
    }

    private Response postTestLogs(JSONObject log, boolean compress, Consts.CompressType type) {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.createDefault();
            String URL = "http://" + project + "." + credentials.getEndpoint() + "/logstores/" + logStore.GetLogStoreName() + "/track";

            HttpPost httpPost = new HttpPost(URL);
            String body = log.toString();
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.addHeader(Consts.CONST_X_SLS_APIVERSION, Consts.DEFAULT_API_VESION);
            if (compress) {
                byte[] toBytes = body.getBytes("UTF-8");
                byte[] bytes;
                if (type.equals(Consts.CompressType.GZIP)) {
                    bytes = compressData(toBytes, type);
                    httpPost.addHeader(Consts.CONST_X_SLS_COMPRESSTYPE, Consts.CompressType.GZIP.toString());
                } else {
                    bytes = compressData(toBytes, type);
                    httpPost.addHeader(Consts.CONST_X_SLS_COMPRESSTYPE, Consts.CompressType.LZ4.toString());
                }
                ByteArrayEntity entity = new ByteArrayEntity(bytes);
                httpPost.setEntity(entity);
                entity.setContentEncoding("UTF-8");
                httpPost.addHeader(Consts.CONST_X_SLS_BODYRAWSIZE, String.valueOf(toBytes.length));
            } else {
                StringEntity stringEntity = new StringEntity(log.toString(), "UTF-8");
                stringEntity.setContentEncoding("UTF-8");
                httpPost.setEntity(stringEntity);
                httpPost.addHeader(Consts.CONST_X_SLS_BODYRAWSIZE, String.valueOf(body.length()));
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
            return responseBody;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    // do not care
                }
            }
        }
    }

    private byte[] compressData(byte[] data, Consts.CompressType type) throws LogException {
        if (type.equals(Consts.CompressType.GZIP)) {
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
        } else {
            return LZ4Encoder.compressToLhLz4Chunk(data);
        }
    }

    private static class Response {
        private int status;
        private String body;

        Response(int status, String body) {
            this.status = status;
            this.body = body;
        }
    }
}
