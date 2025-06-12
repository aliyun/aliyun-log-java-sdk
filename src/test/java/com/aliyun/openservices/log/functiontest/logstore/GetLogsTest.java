package com.aliyun.openservices.log.functiontest.logstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetHistogramsResponse;

public class GetLogsTest extends BaseDataTest {
    @Test
    public void testGetLogs() throws LogException {
        enableIndex();
        getLogs();
    }

    private void getLogs() throws LogException {
        int count = prepareLogs();
        int totalSize = verifyGet();
        assertEquals(count * 10, totalSize);

        GetHistogramsResponse histograms = client.GetHistograms(project, logStore.GetLogStoreName(),
                timestamp - 1800, timestamp + 1800, "", "");
        assertEquals(count * 10, histograms.GetTotalCount());
        assertTrue(histograms.IsCompleted());
    }
}
