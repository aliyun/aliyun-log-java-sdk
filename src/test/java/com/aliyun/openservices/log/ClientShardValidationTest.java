package com.aliyun.openservices.log;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ClientShardValidationTest {
    private static final Client client = new Client(
            "http://mock-sls.aliyun-inc.com",
            "test-access-key-id",
            "test-access-key");

    @Test
    public void testSplitShardRejectsNegativeShardId() throws LogException {
        try {
            client.SplitShard("project", "logstore", -1, "");
            fail("Should reject negative shardId");
        } catch (IllegalArgumentException ex) {
            assertEquals("shardId must be >= 0", ex.getMessage());
        }
    }

    @Test
    public void testMergeShardsRejectsNegativeShardId() throws LogException {
        try {
            client.MergeShards("project", "logstore", -1);
            fail("Should reject negative shardId");
        } catch (IllegalArgumentException ex) {
            assertEquals("shardId must be >= 0", ex.getMessage());
        }
    }

    @Test
    public void testGetPrevCursorTimeRejectsNullCursor() {
        try {
            client.GetPrevCursorTime("project", "logstore", 0, null);
            fail("Should reject null cursor");
        } catch (LogException ex) {
            assertEquals(ErrorCodes.INVALID_CURSOR, ex.GetErrorCode());
            assertEquals("empty cursor string", ex.GetErrorMessage());
        }
    }
}
