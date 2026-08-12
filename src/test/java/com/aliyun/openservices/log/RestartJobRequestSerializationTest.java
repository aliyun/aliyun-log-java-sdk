package com.aliyun.openservices.log;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Test;

import com.aliyun.openservices.log.common.ETLV2;
import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.common.ExportConfiguration;
import com.aliyun.openservices.log.common.ExportGeneralSink;
import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.common.IngestionConfiguration;
import com.aliyun.openservices.log.common.IngestionGeneralSource;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.request.CreateExportRequest;
import com.aliyun.openservices.log.request.CreateIngestionRequest;
import com.aliyun.openservices.log.request.ReStartETLV2Request;
import com.aliyun.openservices.log.request.RestartExportRequest;
import com.aliyun.openservices.log.request.RestartIngestionRequest;
import com.aliyun.openservices.log.request.UpdateExportRequest;
import com.aliyun.openservices.log.request.UpdateIngestionRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RestartJobRequestSerializationTest {

    @Test
    public void ingestionWritesFlattenedGeneralSourceForEveryWriteAction() throws Exception {
        Ingestion ingestion = new Ingestion();
        ingestion.setName("ingestion");
        IngestionConfiguration configuration = new IngestionConfiguration();
        IngestionGeneralSource source = new IngestionGeneralSource();
        source.put("type", "ElasticSearch");
        source.put("endpoint", "http://example.com");
        configuration.setSource(source);
        ingestion.setConfiguration(configuration);

        CapturingClient client = new CapturingClient();
        client.createIngestion(new CreateIngestionRequest("project", ingestion));
        assertIngestionSourceBody(client.getRequestBody());

        client.updateIngestion(new UpdateIngestionRequest("project", ingestion));
        assertIngestionSourceBody(client.getRequestBody());

        client.restartIngestion(new RestartIngestionRequest("project", ingestion));
        assertIngestionSourceBody(client.getRequestBody());
    }

    private static void assertIngestionSourceBody(String requestBody) {
        JSONObject sourceBody = JSONObject.parseObject(requestBody)
                .getJSONObject("configuration")
                .getJSONObject("source");
        assertEquals("ElasticSearch", sourceBody.getString("type"));
        assertEquals("http://example.com", sourceBody.getString("endpoint"));
        assertFalse(sourceBody.containsKey("fields"));
    }

    @Test
    public void exportWritesFlattenedGeneralSinkForEveryWriteAction() throws Exception {
        Export export = new Export();
        export.setName("export");
        ExportConfiguration configuration = new ExportConfiguration();
        ExportGeneralSink sink = new ExportGeneralSink();
        sink.put("type", "CustomSink");
        sink.put("endpoint", "http://example.com");
        configuration.setSink(sink);
        export.setConfiguration(configuration);

        CapturingClient client = new CapturingClient();
        client.createExport(new CreateExportRequest("project", export));
        assertExportSinkBody(client.getRequestBody());

        client.updateExport(new UpdateExportRequest("project", export));
        assertExportSinkBody(client.getRequestBody());

        client.restartExport(new RestartExportRequest("project", export));
        assertExportSinkBody(client.getRequestBody());
    }

    private static void assertExportSinkBody(String requestBody) {
        JSONObject sinkBody = JSONObject.parseObject(requestBody)
                .getJSONObject("configuration")
                .getJSONObject("sink");
        assertEquals("CustomSink", sinkBody.getString("type"));
        assertEquals("http://example.com", sinkBody.getString("endpoint"));
        assertFalse(sinkBody.containsKey("fields"));
    }

    @Test
    public void restartEtlV2UsesModelEncoder() throws Exception {
        ETLV2 etl = new ETLV2() {
            @Override
            public String toJsonString() {
                return "{\"encodedByModel\":true}";
            }
        };
        etl.setName("etl");

        CapturingClient client = new CapturingClient();
        client.reStartETLV2(new ReStartETLV2Request("project", etl));

        assertEquals("{\"encodedByModel\":true}", client.getRequestBody());
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
