package com.aliyun.openservices.log;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.ResourcePolicyResourceType;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.request.DeleteResourcePolicyRequest;
import com.aliyun.openservices.log.request.GetResourcePolicyRequest;
import com.aliyun.openservices.log.request.PutResourcePolicyRequest;
import com.aliyun.openservices.log.response.GetResourcePolicyResponse;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ResourcePolicyClientTest {
    private static final String PROJECT = "resource-policy-project";
    private static final String LOGSTORE = "resource-policy-logstore";
    private static final String POLICY = "{\"Version\":\"1\","
            + "\"$ref\":\"literal-not-reference\",\"a.b\":\"中文<>&=\",\"Statement\":[]}";

    @Test
    public void testPutResourcePolicy() throws Exception {
        CapturingClient client = new CapturingClient();

        PutResourcePolicyRequest projectRequest = new PutResourcePolicyRequest(PROJECT,
                ResourcePolicyResourceType.PROJECT, "", POLICY);
        client.putResourcePolicy(projectRequest);
        assertEquals(HttpMethod.PUT, client.method);
        assertEquals("/resource-policies", client.resourceUri);
        assertTrue(client.parameters.isEmpty());
        assertEquals(Consts.CONST_SLS_JSON, client.headers.get(Consts.CONST_CONTENT_TYPE));
        assertEquals(String.valueOf(client.body.length),
                client.headers.get(Consts.CONST_X_SLS_BODYRAWSIZE));
        JSONObject projectBody = JSONObject.parseObject(new String(client.body, StandardCharsets.UTF_8));
        assertEquals("project", projectBody.getString("resourceType"));
        assertFalse(projectBody.containsKey("resourceName"));
        assertEquals(POLICY, projectBody.getString("policyDocument"));
        assertFalse(projectBody.getBooleanValue("dryRun"));

        PutResourcePolicyRequest logstoreRequest = new PutResourcePolicyRequest(PROJECT,
                ResourcePolicyResourceType.LOGSTORE, LOGSTORE, POLICY);
        logstoreRequest.setDryRun(true);
        client.putResourcePolicy(logstoreRequest);
        JSONObject logstoreBody = JSONObject.parseObject(new String(client.body, StandardCharsets.UTF_8));
        assertEquals("logstore", logstoreBody.getString("resourceType"));
        assertEquals(LOGSTORE, logstoreBody.getString("resourceName"));
        assertTrue(logstoreBody.getBooleanValue("dryRun"));
    }

    @Test
    public void testGetAndDeleteResourcePolicy() throws Exception {
        CapturingClient client = new CapturingClient();
        client.setResponseBody("{\"resourceType\":\"logstore\",\"resourceName\":\"" + LOGSTORE
                + "\",\"policyDocument\":" + JsonCodec.toJson(POLICY)
                + ",\"revision\":3,\"createTime\":10,\"updateTime\":20}");

        GetResourcePolicyResponse response = client.getResourcePolicy(new GetResourcePolicyRequest(PROJECT,
                ResourcePolicyResourceType.LOGSTORE, LOGSTORE));
        assertEquals(HttpMethod.GET, client.method);
        assertEquals("logstore", client.parameters.get("resourceType"));
        assertEquals(LOGSTORE, client.parameters.get("resourceName"));
        assertEquals(ResourcePolicyResourceType.LOGSTORE, response.getResourceType());
        assertEquals(LOGSTORE, response.getResourceName());
        assertEquals(POLICY, response.getPolicyDocument());
        assertEquals(3, response.getRevision());
        assertEquals(10, response.getCreateTime());
        assertEquals(20, response.getUpdateTime());

        client.deleteResourcePolicy(new DeleteResourcePolicyRequest(PROJECT,
                ResourcePolicyResourceType.PROJECT, ""));
        assertEquals(HttpMethod.DELETE, client.method);
        assertEquals("project", client.parameters.get("resourceType"));
        assertFalse(client.parameters.containsKey("resourceName"));
    }

    @Test
    public void testResourcePolicyTargetValidationIsDelegatedToServer() throws Exception {
        CapturingClient client = new CapturingClient();
        client.putResourcePolicy(new PutResourcePolicyRequest(PROJECT,
                ResourcePolicyResourceType.PROJECT, LOGSTORE, POLICY));
        JSONObject body = JSONObject.parseObject(new String(client.body, StandardCharsets.UTF_8));
        assertEquals(LOGSTORE, body.getString("resourceName"));

        client.setResponseBody("{\"resourceType\":\"logstore\",\"policyDocument\":\"{}\","
                + "\"revision\":1,\"createTime\":2,\"updateTime\":3}");
        client.getResourcePolicy(new GetResourcePolicyRequest(PROJECT,
                ResourcePolicyResourceType.LOGSTORE, ""));
        assertFalse(client.parameters.containsKey("resourceName"));

        client.deleteResourcePolicy(new DeleteResourcePolicyRequest(PROJECT,
                ResourcePolicyResourceType.PROJECT, LOGSTORE));
        assertEquals(LOGSTORE, client.parameters.get("resourceName"));
    }

    @Test
    public void testResourcePolicyDocumentValidation() throws Exception {
        CapturingClient client = new CapturingClient();
        assertInvalidParameter(new CheckedRunnable() {
            @Override
            public void run() throws Exception {
                client.putResourcePolicy(new PutResourcePolicyRequest(PROJECT,
                        ResourcePolicyResourceType.PROJECT, "", ""));
            }
        });
    }

    @Test
    public void testGetResourcePolicyUsesResponseResourceType() throws Exception {
        CapturingClient client = new CapturingClient();
        client.setResponseBody("{\"resourceType\":\"logstore\",\"resourceName\":\"unexpected\","
                + "\"policyDocument\":\"{}\",\"revision\":1,\"createTime\":2,\"updateTime\":3}");
        GetResourcePolicyResponse response = client.getResourcePolicy(new GetResourcePolicyRequest(PROJECT,
                ResourcePolicyResourceType.PROJECT, ""));
        assertEquals(ResourcePolicyResourceType.LOGSTORE, response.getResourceType());
        assertEquals("unexpected", response.getResourceName());
    }

    private static void assertInvalidParameter(CheckedRunnable runnable) throws Exception {
        try {
            runnable.run();
            fail("Expected invalid parameter");
        } catch (LogException e) {
            assertEquals("InvalidParameter", e.GetErrorCode());
        }
    }

    private interface CheckedRunnable {
        void run() throws Exception;
    }

    private static class CapturingClient extends Client {
        private HttpMethod method;
        private String resourceUri;
        private Map<String, String> parameters;
        private Map<String, String> headers;
        private byte[] body;
        private String responseBody = "";

        CapturingClient() {
            super("http://mock-sls.aliyun-inc.com", "access-id", "access-key");
        }

        void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        @Override
        protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
                                           Map<String, String> parameters, Map<String, String> headers,
                                           byte[] body) {
            this.method = method;
            this.resourceUri = resourceUri;
            this.parameters = new HashMap<String, String>(parameters);
            this.headers = new HashMap<String, String>(headers);
            this.body = body;

            ResponseMessage response = new ResponseMessage();
            response.setStatusCode(200);
            response.addHeader(Consts.CONST_X_SLS_REQUESTID, "test-request-id");
            response.SetBody(responseBody.getBytes(StandardCharsets.UTF_8));
            return response;
        }
    }
}
