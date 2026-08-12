package com.aliyun.openservices.log.internal.json;

import org.junit.Test;

import com.aliyun.openservices.log.common.ExportGeneralSink;
import com.aliyun.openservices.log.common.GeneralJobConfiguration;
import com.aliyun.openservices.log.common.IngestionGeneralSource;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;

public class GeneralJsonAdapterTest {

    @Test
    public void explicitSerializationUsesRawGeneralObjectShape() throws Exception {
        IngestionGeneralSource source = new IngestionGeneralSource();
        source.put("type", "ElasticSearch");
        source.put("endpoint", "http://example.com");
        assertJsonEquals("{\"type\":\"ElasticSearch\",\"endpoint\":\"http://example.com\"}",
                source.toJsonString());

        ExportGeneralSink sink = new ExportGeneralSink();
        sink.put("type", "CustomSink");
        sink.put("batchSize", 100);
        assertJsonEquals("{\"type\":\"CustomSink\",\"batchSize\":100}",
                sink.toJsonString());

        GeneralJobConfiguration configuration =
                new GeneralJobConfiguration("{\"kind\":\"custom\",\"enabled\":true}");
        assertJsonEquals("{\"kind\":\"custom\",\"enabled\":true}",
                configuration.toJsonString());
    }

    @Test
    public void explicitDeserializationRestoresGeneralModels() {
        IngestionGeneralSource source = new IngestionGeneralSource();
        source.fromJsonObject(JSONObject.parseObject(
                "{\"type\":\"ElasticSearch\",\"endpoint\":\"http://example.com\"}"));
        assertEquals("ElasticSearch", source.get("type"));
        assertEquals("http://example.com", source.get("endpoint"));

        ExportGeneralSink sink = new ExportGeneralSink();
        sink.fromJsonObject(JSONObject.parseObject(
                "{\"type\":\"CustomSink\",\"batchSize\":100}"));
        assertEquals("CustomSink", sink.get("type"));
        assertEquals(100, ((Number) sink.get("batchSize")).intValue());

        GeneralJobConfiguration configuration = new GeneralJobConfiguration();
        configuration.fromJsonObject(JSONObject.parseObject(
                "{\"kind\":\"custom\",\"enabled\":true}"));
        assertJsonEquals("{\"kind\":\"custom\",\"enabled\":true}",
                configuration.getDetail());
    }
}
