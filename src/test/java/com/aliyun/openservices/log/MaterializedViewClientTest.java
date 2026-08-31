package com.aliyun.openservices.log;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.request.CreateMaterializedViewRequest;
import com.aliyun.openservices.log.response.GetMaterializedViewResponse;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MaterializedViewClientTest {
    private static final String BODY = "{\"name\":\"view\",\"logstore\":\"source\","
            + "\"originalSql\":\"select * from source\",\"aggIntervalMins\":5,"
            + "\"startTime\":1700000000,\"ttl\":30";

    @Test
    public void testLegacyCreateOmitsShardCount() {
        CreateMaterializedViewRequest request = new CreateMaterializedViewRequest(
                "project", "view", "source", "select * from source", 5, 1700000000, 30);

        assertNull(request.getShardCount());
        assertJsonEquals(BODY + "}", new String(request.getRequestBody(), StandardCharsets.UTF_8));
    }

    @Test
    public void testCreateIncludesShardCount() {
        CreateMaterializedViewRequest request = new CreateMaterializedViewRequest(
                "project", "view", "source", "select * from source", 5, 1700000000, 30, 2);

        assertEquals(Integer.valueOf(2), request.getShardCount());
        assertJsonEquals(BODY + ",\"shardCount\":2}",
                new String(request.getRequestBody(), StandardCharsets.UTF_8));
    }

    @Test
    public void testGetParsesStatusAndLargeNumbers() throws Exception {
        String body = BODY + ",\"shardCount\":2,\"createTime\":1700000000123,\"enabled\":true,"
                + "\"status\":{\"maxCursorTime\":1700000001,\"lastRunTime\":1700000002,"
                + "\"lastRunError\":\"\",\"stats\":{\"hits\":4294967296,"
                + "\"queries\":[\"select * from source\",\"中文 <>&=\\\"quoted\\\"\",null]}}}";

        GetMaterializedViewResponse response = new StubClient(body).getMaterializedView("project", "view");

        assertEquals("test-request-id", response.GetRequestId());
        assertEquals("view", response.getName());
        assertEquals("source", response.getLogstore());
        assertEquals("select * from source", response.getOriginalSql());
        assertEquals(5, response.getAggIntervalMins());
        assertEquals(1700000000, response.getStartTime());
        assertEquals(30, response.getTtl());
        assertEquals(2, response.getShardCount());
        assertEquals(1700000000123L, response.getCreateTime());
        assertTrue(response.isEnabled());
        assertEquals(1700000001, response.getStatus().getMaxCursorTime());
        assertEquals(1700000002, response.getStatus().getLastRunTime());
        assertEquals("", response.getStatus().getLastRunError());
        assertEquals(4294967296L, response.getStatus().getStats().getHits());
        assertEquals(Arrays.asList("select * from source", "中文 <>&=\"quoted\"", "null"),
                response.getStatus().getStats().getQueries());
    }

    @Test
    public void testGetHandlesMissingOptionalFields() throws Exception {
        for (String status : Arrays.asList("", ",\"status\":null")) {
            GetMaterializedViewResponse response = new StubClient(BODY + ",\"enabled\":true" + status + "}")
                    .getMaterializedView("project", "view");
            assertEquals(0, response.getShardCount());
            assertEquals(0L, response.getCreateTime());
            assertNull(response.getStatus());
        }

        GetMaterializedViewResponse response = new StubClient(BODY + ",\"status\":{}}")
                .getMaterializedView("project", "view");
        assertEquals(0, response.getStatus().getMaxCursorTime());
        assertEquals(0, response.getStatus().getLastRunTime());
        assertNull(response.getStatus().getLastRunError());
        assertNull(response.getStatus().getStats());
    }

    @Test
    public void testGetHandlesAbsentQueries() throws Exception {
        for (String stats : Arrays.asList("{}", "{\"queries\":null}", "{\"queries\":[]}")) {
            GetMaterializedViewResponse response = new StubClient(
                    BODY + ",\"status\":{\"stats\":" + stats + "}}")
                    .getMaterializedView("project", "view");
            assertEquals(0L, response.getStatus().getStats().getHits());
            assertTrue(response.getStatus().getStats().getQueries().isEmpty());
        }
    }

    @Test
    public void testExistingResponseConstructorRemainsCompatible() {
        GetMaterializedViewResponse response = new GetMaterializedViewResponse(
                Collections.<String, String>emptyMap(), "view", "source", "select * from source",
                5, 1700000000, 30, true);

        assertEquals(0, response.getShardCount());
        assertEquals(0L, response.getCreateTime());
        assertNull(response.getStatus());
        assertTrue(response.isEnabled());
    }

    private static class StubClient extends Client {
        private final String responseBody;

        private StubClient(String responseBody) {
            super("http://mock-sls.example.com", "access-key-id", "access-key-secret");
            this.responseBody = responseBody;
        }

        @Override
        protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
                                           Map<String, String> parameters, Map<String, String> headers, byte[] body) {
            assertEquals("project", project);
            assertEquals(HttpMethod.GET, method);
            assertEquals("/materializedviews/view", resourceUri);
            assertTrue(parameters.isEmpty());
            assertEquals(Consts.CONST_SLS_JSON, headers.get(Consts.CONST_CONTENT_TYPE));
            ResponseMessage response = new ResponseMessage();
            response.setStatusCode(200);
            response.addHeader(Consts.CONST_X_SLS_REQUESTID, "test-request-id");
            response.SetBody(responseBody.getBytes(StandardCharsets.UTF_8));
            return response;
        }
    }
}
