package com.aliyun.openservices.log.functiontest.logstore;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.exception.LogException;

public class PostWebtrackingLz4Test extends BaseDataTest {
    @Test
    public void testPostWebTrackingForLZ4() throws LogException {
        enableIndex();
        int count = mockPostRequest(Consts.CompressType.LZ4);

        int pull = verifyPull();
        assertEquals(count, pull);

        int get = verifyGet();
        assertEquals(count, get);
    }
}
