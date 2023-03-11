package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.LogStore;
import org.junit.Test;

public class ClearLogStoreStorageTest extends MetaAPIBaseFunctionTest {

    @Test
    public void testClearLogStoreStorage() throws Exception {
        LogStore logStore = new LogStore("logstore", 1, 1);
        client.CreateLogStore(TEST_PROJECT, logStore);
        for (int i = 0; i < 100; ++i) {
            client.ClearLogStoreStorage(TEST_PROJECT, logStore.GetLogStoreName());
        }
    }
}
