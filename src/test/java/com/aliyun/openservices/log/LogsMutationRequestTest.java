package com.aliyun.openservices.log;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.request.DeleteLogsV2Request;
import com.aliyun.openservices.log.request.UpdateLogsRequest;
import com.aliyun.openservices.log.response.DeleteLogsV2Response;
import com.aliyun.openservices.log.response.UpdateLogsResponse;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LogsMutationRequestTest {

    @Test
    public void testDeleteLogsV2Request() {
        DeleteLogsV2Request request = new DeleteLogsV2Request(
                "project", "logstore", 100, 200, "level:error", "row-id-1");

        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/logstores/logstore/deletelogs", request.getUri());

        JSONObject body = (JSONObject) request.getBody();
        assertEquals(100, body.getIntValue("from"));
        assertEquals(200, body.getIntValue("to"));
        assertEquals("level:error", body.getString("query"));
        assertEquals("row-id-1", body.getString("rowId"));

        request.setQuery(null);
        request.setRowId(null);
        body = (JSONObject) request.getBody();
        assertFalse(body.containsKey("query"));
        assertFalse(body.containsKey("rowId"));
    }

    @Test
    public void testUpdateLogsRequest() {
        UpdateLogsRequest request = new UpdateLogsRequest(
                "project", "logstore", 100, 200, "level:error", "row-id-1", "partial", "{\"level\":\"warn\"}");

        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/logstores/logstore/updatelogs", request.getUri());

        JSONObject body = (JSONObject) request.getBody();
        assertEquals(100, body.getIntValue("from"));
        assertEquals(200, body.getIntValue("to"));
        assertEquals("level:error", body.getString("query"));
        assertEquals("row-id-1", body.getString("rowId"));
        assertEquals("partial", body.getString("updateMode"));
        assertJsonEquals("{\"level\":\"warn\"}", body.getString("data"));

        request.setUpdateMode(null);
        request.setData(null);
        body = (JSONObject) request.getBody();
        assertFalse(body.containsKey("updateMode"));
        assertFalse(body.containsKey("data"));
    }

    @Test
    public void testUpdateLogsRequestConvertsDataHelpers() {
        UpdateLogsRequest request = new UpdateLogsRequest("project", "logstore");

        LogItem logItem = new LogItem();
        logItem.PushBack("status", "REFUNDED");
        request.setLogItem(logItem);
        assertJsonEquals("{\"status\":\"REFUNDED\"}", request.getData());

        Map<String, String> data = new HashMap<String, String>();
        data.put("status", "PENDING");
        data.put("refund_at", "2026-05-25T10:00:00Z");
        request.setDataFromMap(data);
        assertJsonEquals("{\"status\":\"PENDING\",\"refund_at\":\"2026-05-25T10:00:00Z\"}", request.getData());
    }

    @Test
    public void testLogsMutationResponses() {
        Map<String, String> headers = new HashMap<String, String>();
        DeleteLogsV2Response deleteResponse = new DeleteLogsV2Response(headers, 300000000L);
        UpdateLogsResponse updateResponse = new UpdateLogsResponse(headers, 500000000L);

        assertEquals(300000000L, deleteResponse.getAffectedRows());
        assertEquals(500000000L, updateResponse.getAffectedRows());
    }

    @Test
    public void testDeleteLogsV2ClientSendsJsonAndParsesAffectedRows() throws LogException {
        CapturingClient client = new CapturingClient("{\"affectedRows\":300000000}");
        DeleteLogsV2Response response = client.deleteLogsV2(
                new DeleteLogsV2Request("project", "logstore", 100, 200, "level:error", "row-id-1"));

        assertEquals("/logstores/logstore/deletelogs", client.resourceUri);
        assertEquals(HttpMethod.POST, client.method);
        assertEquals(Consts.CONST_SLS_JSON, client.headers.get(Consts.CONST_CONTENT_TYPE));
        assertEquals(String.valueOf(client.body.length), client.headers.get(Consts.CONST_X_SLS_BODYRAWSIZE));
        assertEquals(300000000L, response.getAffectedRows());

        JSONObject body = JSONObject.parseObject(new String(client.body, StandardCharsets.UTF_8));
        assertEquals(100, body.getIntValue("from"));
        assertEquals("row-id-1", body.getString("rowId"));
    }

    @Test
    public void testUpdateLogsClientSendsJsonAndParsesAffectedRows() throws LogException {
        CapturingClient client = new CapturingClient("{\"affectedRows\":500000000}");
        UpdateLogsResponse response = client.updateLogs(
                new UpdateLogsRequest("project", "logstore", 100, 200, "level:error",
                        "row-id-1", "partial", "{\"level\":\"warn\"}"));

        assertEquals("/logstores/logstore/updatelogs", client.resourceUri);
        assertEquals(HttpMethod.POST, client.method);
        assertEquals(Consts.CONST_SLS_JSON, client.headers.get(Consts.CONST_CONTENT_TYPE));
        assertEquals(String.valueOf(client.body.length), client.headers.get(Consts.CONST_X_SLS_BODYRAWSIZE));
        assertEquals(500000000L, response.getAffectedRows());

        JSONObject body = JSONObject.parseObject(new String(client.body, StandardCharsets.UTF_8));
        assertEquals("partial", body.getString("updateMode"));
        assertJsonEquals("{\"level\":\"warn\"}", body.getString("data"));
    }

    private static class CapturingClient extends Client {
        private final String responseBody;
        private String project;
        private HttpMethod method;
        private String resourceUri;
        private Map<String, String> parameters;
        private Map<String, String> headers;
        private byte[] body;

        private CapturingClient(String responseBody) {
            super("http://mock-sls.aliyun-inc.com", "access-key-id", "access-key-secret");
            this.responseBody = responseBody;
        }

        @Override
        protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
                                           Map<String, String> parameters, Map<String, String> headers, byte[] body)
                throws LogException {
            this.project = project;
            this.method = method;
            this.resourceUri = resourceUri;
            this.parameters = parameters;
            this.headers = headers;
            this.body = body;

            ResponseMessage response = new ResponseMessage();
            response.addHeader(Consts.CONST_X_SLS_REQUESTID, "request-id");
            response.SetBody(responseBody.getBytes(StandardCharsets.UTF_8));
            return response;
        }
    }
}
