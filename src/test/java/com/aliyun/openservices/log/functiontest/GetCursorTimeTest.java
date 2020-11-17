package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.GetCursorTimeResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetCursorTimeTest extends BaseDataTest {
    @Test
    public void testGetCursorTime() throws LogException {
        enableIndex();
        prepareLogs();
        for (int i = 0; i < SHARD_COUNT; i++) {
            GetCursorResponse begin = client.GetCursor(project, logStore.GetLogStoreName(), i, Consts.CursorMode.BEGIN);
            GetCursorTimeResponse startCursorTime = client.GetCursorTime(project, logStore.GetLogStoreName(), i, begin.GetCursor());
            assertTrue(startCursorTime.GetCursorTime() > timestamp - 1800);

            GetCursorResponse end = client.GetCursor(project, logStore.GetLogStoreName(), i, Consts.CursorMode.END);
            GetCursorTimeResponse endCursorTime = client.GetCursorTime(project, logStore.GetLogStoreName(), i, end.GetCursor());
            assertTrue(endCursorTime.GetCursorTime() < timestamp + 1800);
        }
    }
}
