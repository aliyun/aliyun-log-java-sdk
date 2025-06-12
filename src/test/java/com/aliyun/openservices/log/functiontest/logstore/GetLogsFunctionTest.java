package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetLogsFunctionTest extends BaseDataTest {

    @Test
    public void testGetLogs() throws LogException {
        enableIndex();
        int count = prepareLogs();
        for (int i = 0; i < 100; ++i) {
            int totalSize = verifyGet();
            assertEquals(count * 10, totalSize);
            verifySQLQuery(totalSize);
        }
        GetHistogramsResponse histograms = client.GetHistograms(project, logStore.GetLogStoreName(),
                timestamp - 1800, timestamp + 1800, "", "");
        assertEquals(count * 10, histograms.GetTotalCount());
        assertTrue(histograms.IsCompleted());
    }
}
