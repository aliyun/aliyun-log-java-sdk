package com.aliyun.openservices.log;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Test;

import com.aliyun.openservices.log.common.AuditJob;
import com.aliyun.openservices.log.common.AuditJobConfiguration;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.request.CreateAuditJobRequest;
import com.aliyun.openservices.log.request.UpdateAuditJobRequest;

import static org.junit.Assert.assertEquals;

public class AuditJobRequestSerializationTest {

    private static final String EXPECTED_BODY = "{"
            + "\"name\":\"audit\","
            + "\"type\":\"AuditJob\","
            + "\"displayName\":\"Audit\","
            + "\"description\":\"desc\","
            + "\"schedule\":{\"type\":\"Resident\"},"
            + "\"configuration\":{\"test\":\"abc\"}"
            + "}";

    @Test
    public void createAndUpdateUseAuditJobsExistingEncoder() throws Exception {
        CapturingClient client = new CapturingClient();
        AuditJob job = createAuditJob();

        client.createAuditJob(new CreateAuditJobRequest("project", job));
        assertEquals(EXPECTED_BODY, client.getRequestBody());

        client.updateAuditJob(new UpdateAuditJobRequest("project", job));
        assertEquals(EXPECTED_BODY, client.getRequestBody());
    }

    private static AuditJob createAuditJob() {
        AuditJob job = new AuditJob();
        job.setName("audit");
        job.setDisplayName("Audit");
        job.setDescription("desc");
        job.setRecyclable(true);
        job.setStatus("Running");
        job.getSchedule().setRunImmediately(true);
        job.setConfiguration(new AuditJobConfiguration("{\"test\":\"abc\"}"));
        return job;
    }

    private static final class CapturingClient extends Client {

        private byte[] requestBody;

        private CapturingClient() {
            super("http://localhost", "access-id", "access-key");
        }

        @Override
        protected ResponseMessage SendData(String project, HttpMethod method, String resourceUri,
                Map<String, String> parameters, Map<String, String> headers, byte[] body)
                throws LogException {
            requestBody = body;
            return new ResponseMessage();
        }

        private String getRequestBody() {
            return new String(requestBody, StandardCharsets.UTF_8);
        }
    }
}
