package com.aliyun.openservices.log;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.request.EnableLogStoreModifyRequest;
import com.aliyun.openservices.log.response.VoidResponse;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnableLogStoreModifyTest {

    @Test
    public void testRequestBodyOnlyEnablesModification() {
        EnableLogStoreModifyRequest request = new EnableLogStoreModifyRequest("project", "logstore");

        assertEquals("project", request.GetProject());
        assertEquals("logstore", request.getLogStore());

        JSONObject body = JSONObject.parseObject(request.getRequestBody());
        assertEquals(1, body.size());
        assertTrue(body.getBooleanValue("enabled"));
    }

    @Test
    public void testClientSendsDedicatedModificationRequest() throws LogException {
        CapturingClient client = new CapturingClient();
        EnableLogStoreModifyRequest request = new EnableLogStoreModifyRequest("project", "logstore");
        request.SetParam("test-param", "test-value");

        VoidResponse response = client.enableLogStoreModify(request);

        assertEquals("request-id", response.GetRequestId());
        assertEquals("project", client.project);
        assertEquals(HttpMethod.PUT, client.method);
        assertEquals("/logstores/logstore/modification", client.resourceUri);
        assertEquals("test-value", client.parameters.get("test-param"));
        assertEquals(Consts.CONST_SLS_JSON, client.headers.get(Consts.CONST_CONTENT_TYPE));
        assertEquals(String.valueOf(client.body.length),
                client.headers.get(Consts.CONST_X_SLS_BODYRAWSIZE));

        JSONObject body = JSONObject.parseObject(new String(client.body, StandardCharsets.UTF_8));
        assertEquals(1, body.size());
        assertTrue(body.getBooleanValue("enabled"));
    }

    @Test
    public void testConvenienceOverloadUsesSameRequest() throws LogException {
        CapturingClient client = new CapturingClient();

        client.enableLogStoreModify("project", "logstore");

        assertEquals(HttpMethod.PUT, client.method);
        assertEquals("/logstores/logstore/modification", client.resourceUri);
    }

    private static class CapturingClient extends Client {
        private String project;
        private HttpMethod method;
        private String resourceUri;
        private Map<String, String> parameters;
        private Map<String, String> headers;
        private byte[] body;

        private CapturingClient() {
            super("http://mock-sls.example.com", "access-key-id", "access-key-secret");
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
            return response;
        }
    }
}
