package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.common.LogStore;

import static org.junit.Assert.assertTrue;

import com.aliyun.openservices.log.functiontest.MetaAPIBaseFunctionTest;
import org.junit.Before;
import org.junit.Test;

public class ClearLogStoreStorageTest extends MetaAPIBaseFunctionTest {

    @Before
    @Override
    public void setUp() {
        super.setUp();
        assertTrue(safeCreateLogStore(TEST_PROJECT, new LogStore("logstore", 1, 1))); 
    }

    @Test
    public void testClearLogStoreStorage() throws Exception {
        Thread.sleep(1000 * 10);
        for (int i = 0; i < 100; ++i) {
            client.ClearLogStoreStorage(TEST_PROJECT, "logstore");
        }
    }
}
