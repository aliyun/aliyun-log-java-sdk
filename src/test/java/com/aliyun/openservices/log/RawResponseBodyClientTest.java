package com.aliyun.openservices.log;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.CursorMode;
import com.aliyun.openservices.log.common.ConsumerGroupShardCheckPoint;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.ProjectConsumerGroupCheckPointResponse;
import com.aliyun.openservices.log.response.ProjectConsumerGroupHeartBeatResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RawResponseBodyClientTest {

    @Test
    public void getCursorCapturesRawResponseBody() throws Exception {
        String raw = "{\"cursor\":\"cursor-a\",\"futureField\":\"future-value\"}";
        StubClient client = new StubClient(raw);

        GetCursorResponse response = client.GetCursor("project", "logstore", 2, CursorMode.BEGIN);

        assertEquals("cursor-a", response.GetCursor());
        assertEquals(raw, response.getRawResponseBody());
    }

    @Test
    public void projectConsumerGroupCheckpointCapturesRawResponseBody() throws Exception {
        String raw = "{\"logstore-a\":[{\"shard\":2,\"checkpoint\":\"checkpoint-a\","
                + "\"updateTime\":1700000000123,\"consumer\":\"consumer-a\","
                + "\"futureField\":\"future-value\"}]}";
        StubClient client = new StubClient(raw);

        ProjectConsumerGroupCheckPointResponse response =
                client.GetProjectConsumerGroupCheckPoint("project", "group-a", "logstore-a", 2);

        ConsumerGroupShardCheckPoint checkpoint = response.getCheckPoints().get("logstore-a").get(0);
        assertEquals(2, checkpoint.getShard());
        assertEquals("checkpoint-a", checkpoint.getCheckPoint());
        assertEquals(1700000000123L, checkpoint.getUpdateTime());
        assertEquals("consumer-a", checkpoint.getConsumer());
        assertEquals(raw, response.getRawResponseBody());
    }

    @Test
    public void projectConsumerGroupHeartbeatCapturesRawResponseBody() throws Exception {
        String raw = "{\"logstores\":{\"logstore-a\":[0,2],\"logstore-b\":[1]},"
                + "\"futureField\":\"future-value\"}";
        StubClient client = new StubClient(raw);
        Map<String, ArrayList<Integer>> heldShards = new HashMap<String, ArrayList<Integer>>();
        heldShards.put("logstore-a", new ArrayList<Integer>(Arrays.asList(0, 2)));

        ProjectConsumerGroupHeartBeatResponse response = client.ProjectConsumerGroupHeartBeat(
                "project", "group-a", "consumer-a", heldShards);

        assertEquals(Arrays.asList(0, 2), response.getLogStoreShards().get("logstore-a"));
        assertEquals(Collections.singletonList(1), response.getLogStoreShards().get("logstore-b"));
        assertEquals(raw, response.getRawResponseBody());
    }

    @Test
    public void responseWithoutClientPassthroughHasNoRawBody() {
        GetCursorResponse response = new GetCursorResponse(Collections.<String, String>emptyMap(), "cursor-a");
        assertNull(response.getRawResponseBody());
    }

    private static final class StubClient extends Client {

        private final String responseBody;

        private StubClient(String responseBody) {
            super("http://localhost", "access-id", "access-key");
            this.responseBody = responseBody;
        }

        @Override
        protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
                Map<String, String> parameters, Map<String, String> headers, byte[] body)
                throws LogException {
            ResponseMessage response = new ResponseMessage();
            response.setStatusCode(200);
            response.addHeader(Consts.CONST_X_SLS_REQUESTID, "test-request-id");
            response.SetBody(responseBody.getBytes(StandardCharsets.UTF_8));
            return response;
        }
    }
}
