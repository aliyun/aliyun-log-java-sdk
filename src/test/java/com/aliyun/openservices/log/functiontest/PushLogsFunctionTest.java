package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Vector;

import static org.junit.Assert.fail;


public class PushLogsFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = "project1";
    private static final String TEST_LOGSTORE = "logstore1";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        safeCreateProject(TEST_PROJECT, "desc");
        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(TEST_LOGSTORE);
        logStore.SetTtl(1);
        logStore.SetShardCount(2);
        logStore.setAppendMeta(true);
        if (safeDeleteLogStore(TEST_PROJECT, TEST_LOGSTORE)) {
            waitForSeconds(60);
        }
        client.CreateLogStore(TEST_PROJECT, logStore);
    }

    @Test
    public void testPutData() {
        int round = randomBetween(10, 20);
        for (int i = 0; i < round; i++) {
            Vector<LogItem> logGroup = new Vector<LogItem>();
            for (int j = 0; j < 600; j++) {
                LogItem logItem = new LogItem(
                        (int) (new Date().getTime() / 1000));
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
