package com.aliyun.openservices.log;

import com.aliyun.openservices.log.common.AnonymousWriteStatus;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.MultimodalStatus;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.request.DeleteObjectRequest;
import com.aliyun.openservices.log.request.GeneratePresignedUrlRequest;
import com.aliyun.openservices.log.request.GetLogStoreMultimodalConfigurationRequest;
import com.aliyun.openservices.log.request.PutLogStoreMultimodalConfigurationRequest;
import com.aliyun.openservices.log.response.GeneratePresignedUrlResponse;
import com.aliyun.openservices.log.response.GetLogStoreMultimodalConfigurationResponse;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MultimodalObjectClientTest {
    private static final String KEY = "目录/object +%?.txt";
    private static final String URL = "https://objects.example.com/object?expires=3600&download=1";
    private static final String ROLE_ARN = "acs:ram::1234567890123456:role/oss-role";

    @Test
    public void testPresignOmitsDefaultExpiration() throws Exception {
        CapturingClient client = new CapturingClient("{\"url\":\"" + URL + "\"}");

        GeneratePresignedUrlResponse response = client.generatePresignedUrl(
                new GeneratePresignedUrlRequest("project", "logstore", KEY, "GET"));

        client.assertRequest(HttpMethod.POST, "/logstores/logstore/presign");
        assertEquals(Consts.CONST_SLS_JSON, client.headers.get(Consts.CONST_CONTENT_TYPE));
        assertJsonEquals("{\"key\":\"目录/object +%?.txt\",\"method\":\"GET\"}", client.body);
        assertEquals(URL, response.getUrl());
        assertEquals("test-request-id", response.GetRequestId());
    }

    @Test
    public void testPresignIncludesExplicitExpiration() throws Exception {
        CapturingClient client = new CapturingClient("{\"url\":\"" + URL + "\"}");
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                "project", "logstore", KEY, "PUT", 3600L);

        client.generatePresignedUrl(request);

        client.assertRequest(HttpMethod.POST, "/logstores/logstore/presign");
        assertJsonEquals("{\"key\":\"目录/object +%?.txt\",\"method\":\"PUT\",\"expires\":3600}", client.body);

        request.setExpires(null);
        client.generatePresignedUrl(request);
        assertJsonEquals("{\"key\":\"目录/object +%?.txt\",\"method\":\"PUT\"}", client.body);
    }

    @Test
    public void testPutUserBucketConfiguration() throws Exception {
        CapturingClient client = new CapturingClient("");
        PutLogStoreMultimodalConfigurationRequest request = new PutLogStoreMultimodalConfigurationRequest(
                "project", "logstore", MultimodalStatus.ENABLED);
        request.setAnonymousWrite(AnonymousWriteStatus.DISABLED);
        request.setOssBucket("example-bucket");
        request.setRoleArn(ROLE_ARN);

        client.putLogStoreMultimodalConfiguration(request);

        client.assertRequest(HttpMethod.PUT, "/logstores/logstore/multimodalconfiguration");
        assertEquals(Consts.CONST_SLS_JSON, client.headers.get(Consts.CONST_CONTENT_TYPE));
        assertJsonEquals("{\"status\":\"Enabled\",\"anonymousWrite\":\"Disabled\","
                + "\"ossBucket\":\"example-bucket\",\"roleArn\":\"" + ROLE_ARN + "\"}", client.body);
    }

    @Test
    public void testPutBuiltInBucketOmitsOptionalFields() throws Exception {
        CapturingClient client = new CapturingClient("");

        client.putLogStoreMultimodalConfiguration(new PutLogStoreMultimodalConfigurationRequest(
                "project", "logstore", MultimodalStatus.ENABLED));

        assertJsonEquals("{\"status\":\"Enabled\"}", client.body);
    }

    @Test
    public void testGetUserBucketConfiguration() throws Exception {
        CapturingClient client = new CapturingClient("{\"status\":\"Enabled\",\"anonymousWrite\":\"Disabled\","
                + "\"ossBucket\":\"example-bucket\",\"roleArn\":\"" + ROLE_ARN + "\"}");

        GetLogStoreMultimodalConfigurationResponse response = client.getLogStoreMultimodalConfiguration(
                new GetLogStoreMultimodalConfigurationRequest("project", "logstore"));

        client.assertRequest(HttpMethod.GET, "/logstores/logstore/multimodalconfiguration");
        assertEquals(MultimodalStatus.ENABLED, response.getStatus());
        assertEquals(AnonymousWriteStatus.DISABLED, response.getAnonymousWrite());
        assertEquals("example-bucket", response.getOssBucket());
        assertEquals(ROLE_ARN, response.getRoleArn());
    }

    @Test
    public void testGetBuiltInBucketConfiguration() throws Exception {
        CapturingClient client = new CapturingClient("{\"status\":\"Enabled\"}");

        GetLogStoreMultimodalConfigurationResponse response = client.getLogStoreMultimodalConfiguration(
                new GetLogStoreMultimodalConfigurationRequest("project", "logstore"));

        assertEquals(MultimodalStatus.ENABLED, response.getStatus());
        assertNull(response.getAnonymousWrite());
        assertNull(response.getOssBucket());
        assertNull(response.getRoleArn());
    }

    @Test
    public void testDeleteObjectOverloadsEncodeObjectName() throws Exception {
        CapturingClient client = new CapturingClient("");
        String path = "/logstores/logstore/objects/%E7%9B%AE%E5%BD%95%2Fobject%20%2B%25%3F.txt";

        assertEquals("test-request-id", client.deleteObject("project", "logstore", KEY).GetRequestId());
        client.assertRequest(HttpMethod.DELETE, path);
        assertEquals("", client.body);

        assertEquals("test-request-id", client.deleteObject(
                new DeleteObjectRequest("project", "logstore", KEY)).GetRequestId());
        client.assertRequest(HttpMethod.DELETE, path);
        assertEquals("", client.body);
    }

    private static class CapturingClient extends Client {
        private final String responseBody;
        private HttpMethod method;
        private String resourceUri;
        private Map<String, String> headers;
        private String body;

        private CapturingClient(String responseBody) {
            super("http://mock-sls.example.com", "access-key-id", "access-key-secret");
            this.responseBody = responseBody;
        }

        private void assertRequest(HttpMethod method, String resourceUri) {
            assertEquals(method, this.method);
            assertEquals(resourceUri, this.resourceUri);
        }

        @Override
        protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
                                           Map<String, String> parameters, Map<String, String> headers, byte[] body) {
            assertEquals("project", project);
            assertTrue(parameters.isEmpty());
            this.method = method;
            this.resourceUri = resourceUri;
            this.headers = new HashMap<String, String>(headers);
            this.body = new String(body, StandardCharsets.UTF_8);
            ResponseMessage response = new ResponseMessage();
            response.setStatusCode(200);
            response.addHeader(Consts.CONST_X_SLS_REQUESTID, "test-request-id");
            response.SetBody(responseBody.getBytes(StandardCharsets.UTF_8));
            return response;
        }
    }
}
