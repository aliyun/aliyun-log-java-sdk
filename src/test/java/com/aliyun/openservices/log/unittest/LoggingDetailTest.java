package com.aliyun.openservices.log.unittest;

import com.aliyun.openservices.log.common.LoggingDetail;
import net.sf.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LoggingDetailTest {

    @Test
    public void testMarshal() {
        LoggingDetail detail = new LoggingDetail("type1", "logstore1");
        JSONObject asJson = detail.marshal();
        assertEquals("type1", asJson.getString("type"));
        assertEquals("logstore1", asJson.getString("logstore"));
        try {
            new LoggingDetail("", "logstore1");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("[type] must not be null or empty!", ex.getMessage());
        }
        try {
            new LoggingDetail("type1", "");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("[logstore] must not be null or empty!", ex.getMessage());
        }
    }

    @Test
    public void testUnmarshal() {
        JSONObject detailAsJson = new JSONObject();
        detailAsJson.put("type", "type1");
        detailAsJson.put("logstore", "logstore1");
        LoggingDetail detail = LoggingDetail.unmarshal(detailAsJson);
        assertEquals("type1", detail.getType());
        assertEquals("logstore1", detail.getLogstore());
        try {
            detailAsJson.put("type", "");
            LoggingDetail.unmarshal(detailAsJson);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("[logstore] must not be null or empty!", ex.getMessage());
        }
        try {
            detailAsJson.put("logstore", "");
            detailAsJson.put("type", "type1");
            LoggingDetail.unmarshal(detailAsJson);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("[logstore] must not be null or empty!", ex.getMessage());
        }
    }
}
