package com.aliyun.openservices.log.response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.internal.json.JsonCodec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetLogsResponseDeserializationTest {

    private static final String SPECIAL = "中文<>&=\"\\line";

    @Test
    public void deserializesFullQueryResultWithoutLosingWireValues() throws Exception {
        String body = "{\"meta\":{\"progress\":\"Complete\","
                + "\"aggQuery\":\"select count(*)\",\"whereQuery\":\"latency>=10\","
                + "\"hasSQL\":true,\"processedRows\":123456789,"
                + "\"elapsedMillisecond\":12,\"cpuSec\":1.25,\"cpuCores\":2,"
                + "\"keys\":[\"host\",\"message\"],"
                + "\"terms\":[{\"key\":\"message\",\"term\":\"error\"}],"
                + "\"limited\":3,\"marker\":\"marker-a\",\"mode\":1,"
                + "\"phraseQueryInfo\":{\"scanAll\":true,\"beginOffset\":1,"
                + "\"endOffset\":9,\"endTime\":1700003600},\"scanBytes\":12345,"
                + "\"highlights\":[{\"message\":\"<em>error</em>\"}],"
                + "\"columnTypes\":[\"varchar\",\"bigint\"]},"
                + "\"data\":[{\"__source__\":\"source-a\",\"__time__\":\"1700000000\","
                + "\"__time_ns_part__\":\"123\",\"host\":\"host-a\","
                + "\"message\":" + JsonCodec.toJson(SPECIAL) + "}]}";
        ResponseMessage message = new ResponseMessage();
        message.setStatusCode(200);
        message.addHeader(Consts.CONST_X_SLS_REQUESTID, "test-request-id");
        message.SetBody(body.getBytes(StandardCharsets.UTF_8));

        GetLogsResponse response = GetLogsResponse.fromResponse(message, true);

        assertTrue(response.IsCompleted());
        assertEquals("select count(*)", response.getAggQuery());
        assertEquals("latency>=10", response.getWhereQuery());
        assertTrue(response.isHasSQL());
        assertEquals(123456789L, response.getProcessedRow());
        assertEquals(12L, response.getElapsedMilliSecond());
        assertEquals(1.25D, response.getCpuSec(), 0D);
        assertEquals(2L, response.getCpuCores());
        assertEquals(3L, response.getLimited());
        assertEquals("marker-a", response.getMarker());
        assertTrue(response.IsPhraseQuery());
        assertTrue(response.IsScanAll());
        assertEquals(1L, response.GetBeginOffset());
        assertEquals(9L, response.GetEndOffset());
        assertEquals(1700003600L, response.GetEndTime());
        assertEquals(12345L, response.GetScanBytes());
        assertEquals(java.util.Arrays.asList("host", "message"), response.getKeys());
        assertEquals(java.util.Arrays.asList("error", "message"), response.getTerms().get(0));
        assertEquals(java.util.Arrays.asList("varchar", "bigint"), response.getColumnTypes());

        List<QueriedLog> logs = response.getLogs();
        assertEquals(1, logs.size());
        assertEquals("source-a", logs.get(0).GetSource());
        LogItem item = logs.get(0).GetLogItem();
        assertEquals(1700000000, item.GetTime());
        assertEquals(123, item.GetTimeNsPart());
        Map<String, String> contents = contentsByKey(item.GetLogContents());
        assertEquals("host-a", contents.get("host"));
        assertEquals(SPECIAL, contents.get("message"));

        Map<String, String> highlights = contentsByKey(response.getHighlights().get(0));
        assertEquals("<em>error</em>", highlights.get("message"));
    }

    private static Map<String, String> contentsByKey(List<LogContent> contents) {
        Map<String, String> values = new HashMap<String, String>();
        for (LogContent content : contents) {
            values.put(content.GetKey(), content.GetValue());
        }
        return values;
    }
}
