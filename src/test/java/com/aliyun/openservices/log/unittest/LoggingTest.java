package com.aliyun.openservices.log.unittest;

import com.aliyun.openservices.log.common.Logging;
import com.aliyun.openservices.log.common.LoggingDetail;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LoggingTest {


    @Test
    public void testMarshal() {
        final String project = "test-project";
        final List<LoggingDetail> loggingDetails = new ArrayList<LoggingDetail>();
        LoggingDetail detail = new LoggingDetail("type1", "logstore1");
        loggingDetails.add(detail);

        Logging logging = new Logging(project, loggingDetails);
        JSONObject object = logging.marshal();
        assertEquals(2, object.size());
        assertEquals(project, object.getString("loggingProject"));

        JSONArray details = object.getJSONArray("loggingDetails");
        assertEquals(1, details.size());
        JSONObject detailAsJson = details.getJSONObject(0);
        assertEquals(2, detailAsJson.size());

        assertEquals("type1", detailAsJson.getString("type"));
        assertEquals("logstore1", detailAsJson.getString("logstore"));
    }


    @Test
    public void testUnmarshal() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("loggingProject", "test-project");
        JSONArray array = new JSONArray();
        JSONObject detailAsJson = new JSONObject();
        detailAsJson.put("type", "type1");
        detailAsJson.put("logstore", "logstore1");
        array.add(detailAsJson);
        jsonObject.put("loggingDetails", array);

        Logging logging = Logging.unmarshal(jsonObject);
        assertEquals(logging.getLoggingProject(), "test-project");
        assertEquals(1, logging.getLoggingDetails().size());
        LoggingDetail detail = logging.getLoggingDetails().get(0);
        assertEquals("type1", detail.getType());
        assertEquals("logstore1", detail.getLogstore());
    }
}
