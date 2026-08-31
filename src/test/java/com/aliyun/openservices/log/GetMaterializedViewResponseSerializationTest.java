package com.aliyun.openservices.log;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.response.GetMaterializedViewResponse;
import com.aliyun.openservices.log.response.GetMaterializedViewResponse.Status;
import com.aliyun.openservices.log.response.GetMaterializedViewResponse.Status.Stats;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GetMaterializedViewResponseSerializationTest {
    @Test
    public void testResponseWithStatusAndStatsRoundTrip() throws Exception {
        List<String> queries = Arrays.asList("select * from source", "中文 <>&=\"quoted\"", null);
        Stats stats = new Stats(4294967296L, queries);
        Status status = new Status(1700000001, 1700000002, "retrying", stats);

        GetMaterializedViewResponse restored = roundTrip(responseWithStatus(status));

        assertEquals(1700000001, restored.getStatus().getMaxCursorTime());
        assertEquals(1700000002, restored.getStatus().getLastRunTime());
        assertEquals("retrying", restored.getStatus().getLastRunError());
        assertEquals(4294967296L, restored.getStatus().getStats().getHits());
        assertEquals(queries, restored.getStatus().getStats().getQueries());
    }

    @Test
    public void testResponseWithStatusWithoutStatsRoundTrip() throws Exception {
        Status status = new Status(1700000001, 1700000002, null);

        GetMaterializedViewResponse restored = roundTrip(responseWithStatus(status));

        assertEquals(1700000001, restored.getStatus().getMaxCursorTime());
        assertEquals(1700000002, restored.getStatus().getLastRunTime());
        assertNull(restored.getStatus().getLastRunError());
        assertNull(restored.getStatus().getStats());
    }

    @Test
    public void testLegacyResponseRoundTrip() throws Exception {
        GetMaterializedViewResponse response = new GetMaterializedViewResponse(
                Collections.singletonMap(Consts.CONST_X_SLS_REQUESTID, "test-request-id"),
                "view", "source", "select * from source", 5, 1700000000, 30, true);

        GetMaterializedViewResponse restored = roundTrip(response);

        assertNull(restored.getStatus());
    }

    private static GetMaterializedViewResponse responseWithStatus(Status status) {
        return new GetMaterializedViewResponse(
                Collections.singletonMap(Consts.CONST_X_SLS_REQUESTID, "test-request-id"),
                "view", "source", "select * from source", 5, 1700000000, 30,
                2, 1700000000123L, true, status);
    }

    private static GetMaterializedViewResponse roundTrip(GetMaterializedViewResponse response)
            throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(response);
        }

        GetMaterializedViewResponse restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (GetMaterializedViewResponse) input.readObject();
        }

        assertEquals(response.GetAllHeaders(), restored.GetAllHeaders());
        assertEquals(response.getName(), restored.getName());
        assertEquals(response.getLogstore(), restored.getLogstore());
        assertEquals(response.getOriginalSql(), restored.getOriginalSql());
        assertEquals(response.getAggIntervalMins(), restored.getAggIntervalMins());
        assertEquals(response.getStartTime(), restored.getStartTime());
        assertEquals(response.getTtl(), restored.getTtl());
        assertEquals(response.getShardCount(), restored.getShardCount());
        assertEquals(response.getCreateTime(), restored.getCreateTime());
        assertEquals(response.isEnabled(), restored.isEnabled());
        return restored;
    }
}
