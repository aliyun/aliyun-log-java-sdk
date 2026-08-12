package com.aliyun.openservices.log.common;

import java.util.Arrays;

import org.junit.Test;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShipperConfigJsonStringTest {

    @Test
    public void ossConfigSupportsPublicJsonStringRoundTrip() throws Exception {
        OssShipperConfig original = new OssShipperConfig(
                "bucket-中文", "prefix/\"quoted\"/\\path",
                "acs:ram::<>&=:role/shipper", 0, 64, "snappy",
                "%Y/%m/%d/%H", "csv", "+08:00");
        OssShipperCsvStorageDetail storage =
                (OssShipperCsvStorageDetail) original.getStorageDetail();
        storage.setmStorageColumns(new java.util.ArrayList<String>(
                Arrays.asList("time", "message-中文")));
        storage.setDelimiter("|");
        storage.setQuote("\"");
        storage.setLineFeed("\r\n");
        storage.setNullIdentifier("NULL\\value");
        storage.setHeader(false);

        ShipperConfig consumerView = original;
        String json = consumerView.toJsonString();
        assertJsonEquals("{\"ossBucket\":\"bucket-中文\","
                        + "\"ossPrefix\":\"prefix/\\\"quoted\\\"/\\\\path\","
                        + "\"roleArn\":\"acs:ram::<>&=:role/shipper\","
                        + "\"bufferInterval\":0,\"bufferSize\":64,"
                        + "\"compressType\":\"snappy\",\"pathFormat\":\"%Y/%m/%d/%H\","
                        + "\"timeZone\":\"+08:00\",\"storage\":{\"format\":\"csv\","
                        + "\"detail\":{\"columns\":[\"time\",\"message-中文\"],"
                        + "\"delimiter\":\"|\",\"quote\":\"\\\"\",\"lineFeed\":\"\\r\\n\","
                        + "\"nullIdentifier\":\"NULL\\\\value\",\"header\":false}}}",
                json);

        ShipperConfig decodedView = new OssShipperConfig();
        decodedView.fromJsonString(json);
        OssShipperConfig decoded = (OssShipperConfig) decodedView;
        assertEquals("bucket-中文", decoded.getOssBucket());
        assertEquals("prefix/\"quoted\"/\\path", decoded.getOssPrefix());
        assertEquals(0, decoded.getBufferInterval());
        assertTrue(decoded.getStorageDetail() instanceof OssShipperCsvStorageDetail);
        OssShipperCsvStorageDetail decodedStorage =
                (OssShipperCsvStorageDetail) decoded.getStorageDetail();
        assertEquals(Arrays.asList("time", "message-中文"), decodedStorage.getmStorageColumns());
        assertEquals("\r\n", decodedStorage.getLineFeed());
        assertFalse(decodedStorage.isHeader());
        assertJsonEquals(json, decodedView.toJsonString());
    }

    @Test
    public void odpsConfigSupportsPublicJsonStringRoundTrip() throws Exception {
        OdpsShipperConfig original = new OdpsShipperConfig();
        original.setOdpsEndPoint("https://service.example.com/api?x=<>&y=中文");
        original.setOdpsProject("project-中文");
        original.setOdpsTable("table-\"quoted\"-\\path");
        original.setLogFieldsList(Arrays.asList("time", "message", "字段"));
        original.setPartitionColumn(Arrays.asList("region", "day"));
        original.setPartitionTimeFormat("yyyy_MM_dd_HH");
        original.setBufferInterval(1800);

        ShipperConfig consumerView = original;
        String json = consumerView.toJsonString();
        assertJsonEquals("{\"odpsEndpoint\":\"https://service.example.com/api?x=<>&y=中文\","
                        + "\"odpsProject\":\"project-中文\","
                        + "\"odpsTable\":\"table-\\\"quoted\\\"-\\\\path\","
                        + "\"fields\":[\"time\",\"message\",\"字段\"],"
                        + "\"partitionColumn\":[\"region\",\"day\"],"
                        + "\"partitionTimeFormat\":\"yyyy_MM_dd_HH\",\"bufferInterval\":1800}",
                json);

        ShipperConfig decodedView = new OdpsShipperConfig();
        decodedView.fromJsonString(json);
        OdpsShipperConfig decoded = (OdpsShipperConfig) decodedView;
        assertEquals("https://service.example.com/api?x=<>&y=中文", decoded.GetOdpsEndPoint());
        assertEquals("table-\"quoted\"-\\path", decoded.GetOdpsTable());
        assertEquals(Arrays.asList("time", "message", "字段"), decoded.GetLogFieldsList());
        assertEquals(Arrays.asList("region", "day"), decoded.GetPartitionColumn());
        assertEquals(1800, decoded.getBufferInterval());
        assertJsonEquals(json, decodedView.toJsonString());
    }
}
