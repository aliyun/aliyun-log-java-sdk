package com.aliyun.openservices.log.functiontest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.exception.LogException;

public class PostWebtrackingTest extends BaseDataTest {
    @Test
    public void testPostWebTracking() throws LogException {
        enableIndex();
        int count = mockPostRequest(Consts.CompressType.GZIP);

        int pull = verifyPull();
        assertEquals(count, pull);

        int get = verifyGet();
        assertEquals(count, get);
    }
}
