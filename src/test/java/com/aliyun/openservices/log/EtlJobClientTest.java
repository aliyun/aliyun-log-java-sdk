package com.aliyun.openservices.log;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.EtlFunctionConfig;
import com.aliyun.openservices.log.common.EtlJob;
import com.aliyun.openservices.log.common.EtlLogConfig;
import com.aliyun.openservices.log.common.EtlSourceConfig;
import com.aliyun.openservices.log.common.EtlTriggerConfig;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.aliyun.openservices.log.request.CreateEtlJobRequest;
import com.aliyun.openservices.log.request.DeleteEtlJobRequest;
import com.aliyun.openservices.log.request.GetEtlJobRequest;
import com.aliyun.openservices.log.request.UpdateEtlJobRequest;
import com.aliyun.openservices.log.response.GetEtlJobResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class EtlJobClientTest {
    private static final String PROJECT = "etl-project";
    private static final int[] NAME_LENGTHS = {2, 63, 64, 128, 129, 256};
    private CapturingClient client;

    @Before
    public void setUp() {
        client = new CapturingClient();
    }

    @After
    public void tearDown() {
        client.shutdown();
    }

    @Test
    public void testCreateEtlJobAcceptsNamesUpTo128Characters() throws Exception {
        for (int length : new int[]{2, 63, 64, 127, 128}) {
            String name = jobName(length);
            client.createEtlJob(new CreateEtlJobRequest(PROJECT, createJob(name)));

            assertEquals(HttpMethod.POST, client.method);
            assertEquals("/etljobs", client.resourceUri);
            JSONObject body = JSONObject.parseObject(new String(client.body, StandardCharsets.UTF_8));
            assertEquals(name, body.getString(Consts.ETL_JOB_NAME));
        }
    }

    @Test
    public void testCreateEtlJobRejectsNamesLongerThan128Characters() throws Exception {
        for (int length : new int[]{129, 256, 257}) {
            assertInvalidName(jobName(length), HttpMethod.POST);
        }
    }

    @Test
    public void testGetEtlJobAcceptsLongNames() throws Exception {
        for (int length : NAME_LENGTHS) {
            String name = jobName(length);
            client.responseBody = createJob(name).toJsonString(true, true);

            GetEtlJobResponse response = client.getEtlJob(new GetEtlJobRequest(PROJECT, name));

            assertEquals(HttpMethod.GET, client.method);
            assertEquals("/etljobs/" + name, client.resourceUri);
            assertEquals(name, response.getEtljob().getJobName());
        }
    }

    @Test
    public void testUpdateEtlJobAcceptsLongNames() throws Exception {
        for (int length : NAME_LENGTHS) {
            String name = jobName(length);
            EtlJob job = createJob(name);
            client.updateEtlJob(new UpdateEtlJobRequest(PROJECT, job));

            assertEquals(HttpMethod.PUT, client.method);
            assertEquals("/etljobs/" + name, client.resourceUri);
            assertEquals(job.toJsonObject(false, false),
                    JSONObject.parseObject(new String(client.body, StandardCharsets.UTF_8)));
        }
    }

    @Test
    public void testDeleteEtlJobAcceptsLongNames() throws Exception {
        for (int length : NAME_LENGTHS) {
            String name = jobName(length);
            client.deleteEtlJob(new DeleteEtlJobRequest(PROJECT, name));

            assertEquals(HttpMethod.DELETE, client.method);
            assertEquals("/etljobs/" + name, client.resourceUri);
        }
    }

    @Test
    public void testEtlJobOperationsRejectNamesLongerThan256Characters() throws Exception {
        for (HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE}) {
            assertInvalidName(jobName(257), method);
        }
    }

    @Test
    public void testEtlJobOperationsRejectInvalidNames() throws Exception {
        String[] invalidNames = {null, "", "a", "_ab", "ab_", "Ab", "a/b", "a?b", "a b"};
        for (String name : invalidNames) {
            for (HttpMethod method : new HttpMethod[]{HttpMethod.POST, HttpMethod.GET,
                    HttpMethod.PUT, HttpMethod.DELETE}) {
                assertInvalidName(name, method);
            }
        }
    }

    private void assertInvalidName(String name, HttpMethod method) throws Exception {
        client.sent = false;
        try {
            if (method == HttpMethod.POST) {
                client.createEtlJob(new CreateEtlJobRequest(PROJECT, createJob(name)));
            } else if (method == HttpMethod.GET) {
                client.responseBody = createJob(name).toJsonString(true, true);
                client.getEtlJob(new GetEtlJobRequest(PROJECT, name));
            } else if (method == HttpMethod.PUT) {
                client.updateEtlJob(new UpdateEtlJobRequest(PROJECT, createJob(name)));
            } else {
                client.deleteEtlJob(new DeleteEtlJobRequest(PROJECT, name));
            }
            fail("Expected invalid parameter for " + method + " with name: " + name);
        } catch (LogException e) {
            assertEquals(ErrorCodes.INVALID_PARAMETER, e.GetErrorCode());
            assertFalse(client.sent);
        }
    }

    private static String jobName(int length) {
        char[] name = new char[length];
        Arrays.fill(name, 'a');
        return new String(name);
    }

    private static EtlJob createJob(String name) {
        return new EtlJob(name, new EtlSourceConfig("source-logstore"),
                new EtlTriggerConfig("test-role", 60, 3),
                new EtlFunctionConfig(Consts.FUNCTION_PROVIDER_LOG_DISPATCH), "{}",
                new EtlLogConfig("cn-hangzhou.log.aliyuncs.com", PROJECT, "result-logstore"), true);
    }

    private static class CapturingClient extends Client {
        private HttpMethod method;
        private String resourceUri;
        private byte[] body;
        private String responseBody = "";
        private boolean sent;

        CapturingClient() {
            super("https://cn-hangzhou.log.aliyuncs.com", "test-access-id", "test-access-key");
        }

        @Override
        protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
                                           Map<String, String> parameters, Map<String, String> headers,
                                           byte[] body) {
            this.method = method;
            this.resourceUri = resourceUri;
            this.body = body;
            this.sent = true;

            ResponseMessage response = new ResponseMessage();
            response.setStatusCode(200);
            response.addHeader(Consts.CONST_X_SLS_REQUESTID, "test-request-id");
            response.SetBody(responseBody.getBytes(StandardCharsets.UTF_8));
            return response;
        }
    }
}
