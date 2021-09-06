package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Histogram;
import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.IndexKey;
import com.aliyun.openservices.log.common.IndexKeys;
import com.aliyun.openservices.log.common.IndexLine;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class SlsIndexDataFunctionTest extends FunctionTest {

    private static final String PROJECT_PREFIX = "test-java-sdk-index-data-";

    private static String project;
    private final int startTime = getNowTimestamp();
    private static final String logStore = "test-logstore-"+ getNowTimestamp();
    private final String topic_prefix = "sls_java_topic_" + startTime + "_";

    @BeforeClass
    public static void SetupOnce() {
        project = PROJECT_PREFIX + randomInt();
        safeCreateProject(project, "");
        try {
            client.DeleteIndex(project, logStore);
            waitForSeconds(5);
        } catch (LogException e) {
        }

        LogStore logStoreRes = new LogStore(logStore, 1, 1);

        try {
            client.CreateLogStore(project, logStoreRes);
            waitForSeconds(60);
        } catch (LogException e) {
            fail(e.GetErrorMessage());
        }
    }

    @AfterClass
    public static void CleanUpOnce() {
        safeDeleteLogStore(project, logStore);
        safeDeleteProject(project);
    }

    public void testCreateIndex() {
        Index index = new Index();
        index.SetTtl(7);

        IndexLine line = new IndexLine();
        line.SetCaseSensitive(false);
        List<String> token = new ArrayList<String>();
        token.add(";");
        line.SetToken(token);

        List<String> includeKeys = new ArrayList<String>();
        includeKeys.add("ID");
        line.SetIncludeKeys(includeKeys);

        index.SetLine(line);

        try {
            client.CreateIndex(project, logStore, index);

            Index res = client.GetIndex(project, logStore).GetIndex();
//            assertEquals(index.GetTtl(), res.GetTtl());

            assertEquals(index.GetLine().GetCaseSensitive(), res.GetLine().GetCaseSensitive());
            assertEquals(index.GetLine().GetToken().size(), res.GetLine().GetToken().size());
            for (int i = 0; i < index.GetLine().GetToken().size(); i++) {
                assertEquals(index.GetLine().GetToken().get(i), res.GetLine().GetToken().get(i));
            }

            assertEquals(index.GetLine().GetIncludeKeys().size(), res.GetLine().GetIncludeKeys().size());
            for (int i = 0; i < index.GetLine().GetIncludeKeys().size(); i++) {
                assertEquals(index.GetLine().GetIncludeKeys().get(i), res.GetLine().GetIncludeKeys().get(i));
            }

        } catch (LogException e) {
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
    }

    public void testDeleteIndex() {
        try {
            client.DeleteIndex(project, logStore);
        } catch (LogException e) {
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
        try {
            client.GetIndex(project, logStore);
            fail("should get index error");
        } catch (LogException e) {
            assertNotSame("should get index error" + e.GetErrorCode(), "IndexConfigNotExist", e.GetErrorCode());
        }
    }

    public void testUpdateIndex() {
        Index index = new Index();
        index.SetTtl(7);
        IndexKeys keys = new IndexKeys();
        IndexKey keyContent = new IndexKey();
        keyContent.SetCaseSensitive(false);
        List<String> token = new ArrayList<String>();
        token.add(";");
        keyContent.SetToken(token);
        String keyName = "test";
        keys.AddKey(keyName, keyContent);
        index.SetKeys(keys);

        try {
            client.UpdateIndex(project, logStore, index);

            Index res = client.GetIndex(project, logStore).GetIndex();
            assertEquals(index.GetTtl(), res.GetTtl());//error
            IndexKeys resKeys = res.GetKeys();
            assertEquals(1, resKeys.GetKeys().size());
            org.junit.Assert.assertTrue(resKeys.GetKeys().containsKey(keyName));
            IndexKey resKey = resKeys.GetKeys().get(keyName);
            assertEquals(keyContent.GetCaseSensitive(), resKey.GetCaseSensitive());
            assertEquals(keyContent.GetToken().size(), resKey.GetToken().size());
            for (int i = 0; i < keyContent.GetToken().size(); i++) {
                assertEquals(keyContent.GetToken().get(i), resKey.GetToken().get(i));
            }

        } catch (LogException e) {
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
    }

    public void testPutData() {
        for (int i = 0; i < 10; i++) {
            Vector<LogItem> logGroup = new Vector<LogItem>();
            for (int j = 0; j < 600; j++) {
                LogItem logItem = new LogItem(
                        (int) (new Date().getTime() / 1000));
                logItem.PushBack("ID", "id_" + (i * 600 + j));
                logGroup.add(logItem);
            }
            String topic = topic_prefix + i;
            try {
                client.PutLogs(project, logStore, topic, logGroup, "");
            } catch (LogException e) {
                fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
            }
        }
        waitForSeconds(10);
    }

    public void testGetLogStore() {
        try {
            ListLogStoresResponse res = client.ListLogStores(project, 0, 500, "");
            org.junit.Assert.assertTrue(res.GetLogStores().contains(logStore));
        } catch (LogException e) {
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
    }

    public void testGetHistogram() {
        try {
            GetHistogramsResponse res = client.GetHistograms(project, logStore,
                    this.startTime, this.startTime + 3600, topic_prefix + 0, "ID");
            assertEquals(res.GetTotalCount(), 600);
            ArrayList<Histogram> histograms = res.GetHistograms();
            int total = 0;
            for (Histogram histogram : histograms) {
                assertTrue(histogram.IsCompleted());
                total += histogram.GetCount();
            }
            assertEquals(total, 600);
        } catch (LogException e) {
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
    }

    public void testGetLogs() {
        int topic_index = 3;
        String topic = topic_prefix + topic_index;
        try {
            GetLogsResponse res = client.GetLogs(project, logStore,
                    this.startTime, this.startTime + 3600, topic, "ID", 20, 0, false);
            assertEquals(res.GetCount(), 20);
            res = client.GetLogs(project, logStore,
                    this.startTime, this.startTime + 3600, topic, "ID", 100, 50, false);
            assertEquals(res.GetCount(), 100);
            assertTrue(res.IsCompleted());
            ArrayList<QueriedLog> queriedLogs = res.GetLogs();
            for (QueriedLog log : queriedLogs) {
                LogItem item = log.GetLogItem();
                assertEquals(topic ,item.GetLogContents().get(0)
                                .GetValue());
            }
        } catch (LogException e) {
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
    }

    @Test
    public void testAll() {
        testCreateIndex();
        testPutData();
        waitForSeconds(45);
        testGetLogStore();
        testGetHistogram();
        testGetLogs();
        testUpdateIndex();
        testDeleteIndex();
    }
}
