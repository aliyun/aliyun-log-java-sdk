package com.aliyun.openservices.log.functiontest.logstore;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateIndexRequest;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.request.PullLogsRequest;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.PullLogsResponse;
import com.aliyun.openservices.log.util.LZ4Encoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class BaseDataTest extends FunctionTest {
    protected String project;
    protected LogStore logStore;
    protected int timestamp;
    protected String PACK_ID_PREFIX;
    protected int SHARD_COUNT = 4;

    @Before
    public void ensureDataReady() {
        timestamp = getNowTimestamp();
        PACK_ID_PREFIX = "ABCDEF" + timestamp + "-";
        project = makeProjectName();
        logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(SHARD_COUNT);
        logStore.SetLogStoreName("test-logstore-" + timestamp);
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(randomBoolean());
        createOrUpdateLogStore(project, logStore);
    }

    @After
    public void cleanUp() {
        safeDeleteLogStore(project, logStore.GetLogStoreName());
        safeDeleteProject(project);
    }

    protected int prepareLogs() throws LogException {
        return prepareLogs(1, 5);
    }

    protected int prepareLogs(int low, int high) throws LogException {
        int logGroupCount = randomBetween(low, high);
        for (int i = 1; i <= logGroupCount; i++) {
            List<LogItem> logItems = new ArrayList<LogItem>(10);
            for (int j = 1; j <= 10; j++) {
                LogItem logItem = new LogItem(timestamp);
                logItem.PushBack("key-" + j, "value-" + j);
                logItems.add(logItem);
            }
            List<TagContent> tags = new ArrayList<TagContent>(2);
            tags.add(new TagContent("__pack_id__", PACK_ID_PREFIX + i));
            tags.add(new TagContent("__extra_tag__", "extra_tag_value"));
            PutLogsRequest request = new PutLogsRequest(project, logStore.GetLogStoreName(), "", "test-source", logItems);
            request.SetTags(tags);
            client.PutLogs(request);
        }
        waitForSeconds(10);
        return logGroupCount;
    }

    protected int verifyPull() throws LogException {
        int logGroupSize = 0;
        int logGroupSizeByPull;
        for (int i = 0; i < SHARD_COUNT; i++) {
            String cur;
            GetCursorResponse endCur = client.GetCursor(project, logStore.GetLogStoreName(), i, Consts.CursorMode.END);
            GetCursorResponse cursor = client.GetCursor(project, logStore.GetLogStoreName(), i, Consts.CursorMode.BEGIN);
            cur = cursor.GetCursor();
            do {
                PullLogsRequest request = new PullLogsRequest(project, logStore.GetLogStoreName(), i, 10, cur);
                PullLogsResponse pullLogs = client.pullLogs(request);
                cur = pullLogs.getNextCursor();
                logGroupSizeByPull = pullLogs.getCount();
                logGroupSize += logGroupSizeByPull;
                for (LogGroupData logGroup : pullLogs.getLogGroups()) {
                    for (Logs.Log log : logGroup.GetLogGroup().getLogsList()) {
                        for (Logs.Log.Content content : log.getContentsList()) {
                            String key = content.getKey();
                            String value = content.getValue();
                            if (!key.startsWith("key-") || !value.startsWith("value-") || !key.substring(4).equals(value.substring(6))) {
                                throw new RuntimeException("Inconsistent data");
                            }
                        }
                    }
                }
            } while (!endCur.GetCursor().equals(cur));
        }
        return logGroupSize;
    }

    protected int verifyGet() throws LogException {
        int totalSize = 0;
        int size;
        do {
            GetLogsRequest getLogsRequest = new GetLogsRequest(project, logStore.GetLogStoreName(), timestamp - 1800,
                    timestamp + 1800, "", "", totalSize, 100, true);
            Consts.CompressType[] types = new Consts.CompressType[]{
                Consts.CompressType.GZIP,
                Consts.CompressType.LZ4,
                Consts.CompressType.NONE,
            };
            getLogsRequest.setCompressType(randomFrom(types));
            GetLogsResponse logs = client.GetLogs(getLogsRequest);
            size = logs.GetCount();
            totalSize += size;
            for (QueriedLog log : logs.getLogs()) {
                for (LogContent mContent : log.mLogItem.mContents) {
                    String mKey = mContent.mKey;
                    String mValue = mContent.mValue;
                    if (mKey.startsWith("key-")) {
                        if (!mValue.startsWith("value-") || !mKey.substring(4).equals(mValue.substring(6))) {
                            throw new RuntimeException("Inconsistent data");
                        }
                    }
                }
            }
        } while (size != 0);
        return totalSize;
    }

    protected void verifySQLQuery(int totalSize) throws LogException {
        GetLogsRequest getLogsRequest = new GetLogsRequest(project, logStore.GetLogStoreName(), timestamp - 1800,
                timestamp + 1800, "", "* | select * limit " + totalSize);
        while (true) {
            Consts.CompressType[] types = new Consts.CompressType[]{
                Consts.CompressType.GZIP,
                Consts.CompressType.LZ4,
                Consts.CompressType.NONE,
            };
            getLogsRequest.setCompressType(randomFrom(types));
            GetLogsResponse response = client.GetLogs(getLogsRequest);
            if (!response.IsCompleted()) {
                System.out.println("Query result is not complete, wait 15s");
                waitForSeconds(15);
                continue;
            }
            if (response.GetCount() != totalSize) {
                throw new RuntimeException("response count is " + response.GetCount() + " but expect " + totalSize);
            }
            for (QueriedLog log : response.getLogs()) {
                for (LogContent mContent : log.mLogItem.mContents) {
                    String mKey = mContent.mKey;
                    String mValue = mContent.mValue;
                    if (mKey.startsWith("key-")) {
                        if (mValue.startsWith("value-")) {
                            if (!mKey.substring(4).equals(mValue.substring(6))) {
                                throw new RuntimeException("Inconsistent data");
                            }
                        } else if (!mValue.equals("null")) {
                            throw new RuntimeException("Inconsistent data");
                        }
                    }
                }
            }
            break;
        }
    }

    protected void enableIndex() throws LogException {
        Index index = new Index();
        index.SetTtl(7);
        index.setMaxTextLen(2048);
        index.setLogReduceEnable(false);
        List<String> list = Arrays.asList(",", " ", "'", "\"", ";", "=", "(", ")", "[", "]", "{", "}", "?", "@", "&", "<", ">", "/", ":", "\n", "\t", "\r");
        IndexKeys indexKeys = new IndexKeys();
        for (int i = 1; i <= 10; i++) {
            indexKeys.AddKey("key-" + i, new IndexKey(list, false, "text", ""));
        }
        index.SetKeys(indexKeys);
        try {
            client.CreateIndex(new CreateIndexRequest(project, logStore.GetLogStoreName(), index));
        } catch (LogException e) {
            fail("Enable Index Failed!");
        }
        waitOneMinutes();
    }

    protected void putLogs() throws LogException {
        int count = prepareLogs();
        int logGroupSize = verifyPull();
        assertEquals(count, logGroupSize);
    }

    protected byte[] compressData(byte[] data, Consts.CompressType type) throws LogException {
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

    protected static class Response {
        private int status;
        private String body;

        Response(int status, String body) {
            this.status = status;
            this.body = body;
        }
    }

    protected int mockPostRequest(Consts.CompressType type) {
        int count = randomBetween(1, 5);
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

    private Response postTestLogs(JSONObject log, boolean compress, Consts.CompressType type) {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.createDefault();
            String URL = "http://" + project + "." + TEST_ENDPOINT + "/logstores/" + logStore.GetLogStoreName()
                    + "/track";

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
            return httpclient.execute(httpPost, responseHandler);
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
}
