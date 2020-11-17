package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PullLogsRequest;
import com.aliyun.openservices.log.response.GetCursorResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.PullLogsResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbnormalOperationTest extends BaseDataTest {

    @Test
    public void testGetLogsByNotEnableIndex() {
        try {
            prepareLogs();
            verifyGet();
            fail();
        } catch (LogException e) {
            assertEquals("IndexConfigNotExist", e.GetErrorCode());
            assertEquals("logstore without index config", e.GetErrorMessage());
        }
    }

    @Test
    public void testPullLogsByErrorParameter() throws LogException {
        enableIndex();
        prepareLogs();
        GetCursorResponse begin = client.GetCursor(project, logStore.GetLogStoreName(), 0, Consts.CursorMode.BEGIN);

        //  project error
        try {
            client.pullLogs(new PullLogsRequest(project + "-fake", logStore.GetLogStoreName(), 0, 10, begin.GetCursor()));
            fail();
        } catch (LogException le) {
            assertEquals("ProjectNotExist", le.GetErrorCode());
        }

        //  logstore error
        try {
            client.pullLogs(new PullLogsRequest(project, logStore.GetLogStoreName() + "-fake", 0, 10, begin.GetCursor()));
            fail();
        } catch (LogException le) {
            assertEquals("LogStoreNotExist", le.GetErrorCode());
        }

        //  shard error
        try {
            client.pullLogs(new PullLogsRequest(project, logStore.GetLogStoreName(), logStore.GetShardCount() + 1, 10, begin.GetCursor()));
            fail();
        } catch (LogException le) {
            assertEquals("ShardNotExist", le.GetErrorCode());
        }

        //  cursor error
        try {
            client.pullLogs(new PullLogsRequest(project, logStore.GetLogStoreName(), 0, 10, begin.GetCursor().substring(10)));
            fail();
        } catch (LogException le) {
            assertEquals("ParameterInvalid", le.GetErrorCode());
        }

        // end cursor
        for (int i = 0; i < logStore.GetShardCount(); i++) {
            GetCursorResponse end = client.GetCursor(project, logStore.GetLogStoreName(), i, Consts.CursorMode.END);
            PullLogsResponse pullLogs = client.pullLogs(new PullLogsRequest(project, logStore.GetLogStoreName(), i, 10, end.GetCursor()));
            assertEquals(0, pullLogs.getCount());
        }
    }

    @Test
    public void testGetLogsByErrorParameter() throws LogException {
        enableIndex();
        prepareLogs();
        //  project error
        try {
            GetLogsResponse logs = client.GetLogs(project + "-fake", logStore.GetLogStoreName(),
                    timestamp - 1800, timestamp + 1800, "", "");
            fail();
        } catch (LogException e) {
            assertEquals("ProjectNotExist", e.GetErrorCode());
        }

        //  logstore error
        try {
            client.GetLogs(project, logStore.GetLogStoreName() + "-fake",
                    timestamp - 1800, timestamp + 1800, "", "");
            fail();
        } catch (LogException e) {
            assertEquals("LogStoreNotExist", e.GetErrorCode());
        }

        //  time error
        try {
            GetLogsResponse logs = client.GetLogs(project, logStore.GetLogStoreName(),
                    -1, timestamp + 1800, "", "");
            fail();
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
            assertEquals("from : -1 pair is invalid", e.GetErrorMessage());
        }

        //  query error
        try {
            client.GetLogs(project, logStore.GetLogStoreName(),
                    timestamp - 1800, timestamp + 1800, "", "and source: *");
            fail();
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
        }

        //  query error by no field index
        try {
            client.GetLogs(project, logStore.GetLogStoreName(),
                    timestamp - 1800, timestamp + 1800, "", "* | select key-11 from log");
            fail();
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
        }

        //  line too big
        try {
            GetLogsResponse logs = client.GetLogs(project, logStore.GetLogStoreName(),
                    timestamp - 1800, timestamp + 1800, "", "", 1000, 0, true);
            assertEquals(100, logs.GetCount());
        } catch (LogException e) {
            fail("line too big error");
        }

        //  offset error
        try {
            GetLogsResponse logs = client.GetLogs(project, logStore.GetLogStoreName(),
                    timestamp - 1800, timestamp + 1800, "", "", 100, -1, true);
            fail();
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
            assertEquals("offset : -1 pair is invalid", e.GetErrorMessage());
        }
    }
}
