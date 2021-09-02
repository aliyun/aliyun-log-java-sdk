package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetLogsResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetProjectLogsTest extends BaseDataTest {

    @Test
    public void testGetProjectLogs() throws LogException {
        enableIndex();
        int count = prepareLogs();
        //Correct
        String sql1 = "SELECT * FROM " + logStore.GetLogStoreName() + " where __time__ > " + (timestamp - 1800)
                + " and __time__ < " + (timestamp + 1800) + " LIMIT 1000;";
        GetLogsResponse logs = client.GetProjectLogs(project, sql1);

        assertEquals(count * 10, logs.GetCount());
        assertTrue(logs.IsCompleted());

        for (QueriedLog log : logs.GetLogs()) {
            assertEquals("test-source", log.mSource);
            for (LogContent mContent : log.mLogItem.mContents) {
                String mKey = mContent.mKey;
                String mValue = mContent.mValue;
                if (mKey.startsWith("key-")) {
                    if (!mValue.startsWith("value-") || !mKey.substring(4).equals(mValue.substring(6))) {
                        throw new RuntimeException("Inconsistent data");
                    }
                }
            }
        }
        //ErrorProject
        try {
            String sql2 = "SELECT * FROM " + logStore.GetLogStoreName() + " where __time__ > " + (timestamp - 1800)
                    + " and __time__ < " + (timestamp + 1800) + " LIMIT 1000;";
            client.GetProjectLogs(project + "-fake", sql2);
            fail();
        } catch (LogException e) {
            assertEquals("ProjectNotExist", e.GetErrorCode());
        }

        //ErrorLimit
        try {
            String sql3 = "SELECT * FROM " + logStore.GetLogStoreName() + " where __time__ > " + (timestamp - 1800)
                    + " and __time__ < " + (timestamp + 1800) + " LIMIT 10000;";
            client.GetProjectLogs(project, sql3);
            fail();
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
        }

        //ErrorSql
        try {
            String sql4 = "*|SELECT * FROM " + logStore.GetLogStoreName() + " where __time__ > " + (timestamp - 1800)
                    + " and __time__ < " + (timestamp + 1800) + " LIMIT 1000;";
            client.GetProjectLogs(project, sql4);
            fail();
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
        }
    }
}
