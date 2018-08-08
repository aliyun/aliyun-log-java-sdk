package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;


public class PushLogsFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = "project1";
    private static final String TEST_LOGSTORE = "logstore1";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(TEST_LOGSTORE);
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.setAppendMeta(true);
        reCreateLogStore(TEST_PROJECT, logStore);
    }

    @Test
    public void testPutData() {
        int round = randomBetween(10, 20);
        for (int i = 0; i < round; i++) {
            List<LogItem> logGroup = new ArrayList<LogItem>(600);
            for (int j = 0; j < 600; j++) {
                LogItem logItem = new LogItem(timestampNow());
                logItem.PushBack("ID", "id_" + String.valueOf(i * 600 + j));
                logGroup.add(logItem);
            }
            String topic = "topic_" + String.valueOf(i);
            try {
                client.PutLogs(TEST_PROJECT, TEST_LOGSTORE, topic, logGroup, "");
            } catch (LogException e) {
                fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
            }
        }
        waitForSeconds(10);
    }

}
