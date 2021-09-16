package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetIndexResponse;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class LogstoreTest extends FunctionTest {

    private final static String INDEX_STRING = "{\"log_reduce\":false,\"line\":{\"caseSensitive\":false,\"chn\":false,\"token\":" +
            "[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]}," +
            "\"keys\":{\"key1\":{\"doc_value\":true,\"caseSensitive\":false,\"chn\":false,\"alias\":\"\",\"type\":\"text\"," +
            "\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]}," +
            "\"key2\":{\"doc_value\":true,\"alias\":\"\",\"type\":\"long\"}},\"ttl\":1}";

    @BeforeClass
    public static void setUp() throws Exception {
        deleteAll();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        deleteAll();
    }

    private static void deleteAll() throws Exception {
        LogException error = null;
        while (true) {
            ListProjectResponse response = client.ListProject(PROJECT_NAME_PREFIX, 0, 100);
            for (Project project : response.getProjects()) {
                try {
                    deleteProject(project.getProjectName());
                } catch (LogException ex) {
                    ex.printStackTrace();
                    error = ex;
                }
            }
            if (response.getCount() < 100) {
                break;
            }
        }
        if (error != null) {
            throw error;
        }
    }

    private static void deleteProject(String project) throws Exception {
        try {
            ListLogStoresResponse response = client.ListLogStores(project, 0, 100, "");
            for (String logstore : response.GetLogStores()) {
                try {
                    client.DeleteLogStore(project, logstore);
                } catch (LogException ex) {
                    if (!ex.GetErrorCode().equals("LogStoreNotExist")) {
                        throw ex;
                    }
                }
            }
            safeDeleteProjectWithoutSleep(project);
        } catch (LogException ex) {
            if (!ex.GetErrorCode().equals("ProjectNotExist")) {
                throw ex;
            }
        }
    }

    @Test
    public void testGetIndex() throws Exception {
        String project = makeProjectName();
        Index index = new Index();
        index.FromJsonString(INDEX_STRING);
        client.CreateProject(project, "");

        int numberOfLogstore = randomInt(100) + 1;
        for (int i = 0; i < numberOfLogstore; i++) {
            LogStore logStore = new LogStore();
            logStore.SetLogStoreName("logstore-" + i);
            logStore.SetShardCount(2);
            logStore.SetTtl(2);
            client.CreateLogStore(project, logStore);
            try {
                client.GetIndex(project, logStore.GetLogStoreName());
                fail();
            } catch (LogException ex) {
                assertEquals("IndexConfigNotExist", ex.GetErrorCode());
            }
            client.CreateIndex(project, logStore.GetLogStoreName(), index);
        }
        for (int i = 0; i < numberOfLogstore; i++) {
            GetLogStoreResponse response = client.GetLogStore(project, "logstore-" + i);
            LogStore logStore = response.GetLogStore();
            assertEquals(2, logStore.GetTtl());
            assertEquals(2, logStore.GetShardCount());

            GetIndexResponse response1 = client.GetIndex(project, logStore.GetLogStoreName());

            int total = 0;
            String query = "logstore-" + i;
            ListLogStoresResponse response2 = null;
            try {
                response2 = client.ListLogStores(project, 0, 10, query);
            } catch (LogException ex) {
                ex.printStackTrace();
                fail(ex.GetErrorMessage());
            }
            for (int j = 0; j < numberOfLogstore; j++) {
                String x = "logstore-" + j;
                if (x.contains(query)) {
                    total++;
                }
            }
            int count = Math.min(10, total);
            assertEquals(count, response2.GetCount());
            assertEquals(total, response2.GetTotal());
        }
        ListLogStoresResponse response = client.ListLogStores(project, 0, 100, "logstore-");
        assertEquals(numberOfLogstore, response.GetTotal());
        assertEquals(numberOfLogstore, response.GetCount());

        int size = randomInt(numberOfLogstore) + 1;

        response = client.ListLogStores(project, 0, size, "");
        assertEquals(numberOfLogstore, response.GetTotal());
        assertEquals(size, response.GetCount());

        for (int i = 0; i < numberOfLogstore; i++) {
            String logstoreName = "logstore-" + i;
            client.DeleteLogStore(project, logstoreName);
            System.out.println("Delete logstore " + logstoreName);
            int total = 0;
            ListLogStoresResponse response2 = client.ListLogStores(project, 0, 10, logstoreName);
            for (int j = i + 1; j < numberOfLogstore; j++) {
                String x = "logstore-" + j;
                if (x.contains(logstoreName)) {
                    total++;
                }
            }
            List<String> logstores = response2.GetLogStores();
            assertFalse(logstores.contains(logstoreName));
            int count = Math.min(10, total);
            assertEquals(count, response2.GetCount());
            assertEquals(total, response2.GetTotal());
        }
    }

    private void crudLogstore() throws Exception {
        String project = makeProjectName();
        client.CreateProject(project, "");
        int numberOfLogstore = randomInt(100);
        for (int i = 0; i < numberOfLogstore; i++) {
            LogStore logStore = new LogStore();
            logStore.SetLogStoreName("logstore-" + i);
            logStore.SetShardCount(2);
            logStore.SetTtl(2);
            client.CreateLogStore(project, logStore);
        }
        for (int i = 0; i < numberOfLogstore; i++) {
            GetLogStoreResponse response = client.GetLogStore(project, "logstore-" + i);
            LogStore logStore = response.GetLogStore();
            assertEquals(2, logStore.GetTtl());
            assertEquals(2, logStore.GetShardCount());

            int total = 0;
            String query = "logstore-" + i;
            ListLogStoresResponse response2 = client.ListLogStores(project, 0, 10, query);
            for (int j = 0; j < numberOfLogstore; j++) {
                String x = "logstore-" + j;
                if (x.contains(query)) {
                    total++;
                }
            }
            int count = Math.min(10, total);
            assertEquals(count, response2.GetCount());
            assertEquals(total, response2.GetTotal());
        }
        ListLogStoresResponse response = client.ListLogStores(project, 0, 100, "logstore-");
        assertEquals(numberOfLogstore, response.GetTotal());
        assertEquals(numberOfLogstore, response.GetCount());
        int size = numberOfLogstore > 0 ? randomInt(numberOfLogstore) + 1 : 1;

        response = client.ListLogStores(project, 0, size, "");
        assertEquals(numberOfLogstore, response.GetTotal());
        assertEquals(Math.min(size, numberOfLogstore), response.GetCount());

        for (int i = 0; i < numberOfLogstore; i++) {
            String logstoreName = "logstore-" + i;
            client.DeleteLogStore(project, logstoreName);
            int total = 0;
            ListLogStoresResponse response2 = client.ListLogStores(project, 0, 10, logstoreName);
            for (int j = i + 1; j < numberOfLogstore; j++) {
                String x = "logstore-" + j;
                if (x.contains(logstoreName)) {
                    total++;
                }
            }
            int count = Math.min(10, total);
            assertEquals(count, response2.GetCount());
            assertEquals(total, response2.GetTotal());

            ListLogStoresResponse response3 = client.ListLogStores(project, 0, 100);
            int left = numberOfLogstore - i - 1;
            assertEquals(left, response3.GetTotal());
            assertEquals(left, response3.GetCount());
            assertFalse(response3.GetLogStores().contains(logstoreName));
        }
    }

    @Test
    public void testCrud() throws Exception {
        String project = makeProjectName();
        client.CreateProject(project, "");
        int numberOfLogstore = randomBetween(1, 100);

        for (int i = 0; i < numberOfLogstore; i++) {
            LogStore logStore = new LogStore();
            logStore.SetLogStoreName("logstore-" + i);
            logStore.SetShardCount(2);
            logStore.SetTtl(2);
            try {
                client.CreateLogStore(project, logStore);
            } catch (LogException ex) {
                ex.printStackTrace();
                fail();
            }
        }

        for (int i = 0; i < numberOfLogstore; i++) {
            GetLogStoreResponse response = client.GetLogStore(project, "logstore-" + i);
            LogStore logStore = response.GetLogStore();
            assertEquals(2, logStore.GetTtl());
            assertEquals(2, logStore.GetShardCount());

            int total = 0;
            String query = "logstore-" + i;
            ListLogStoresResponse response2 = null;
            try {
                response2 = client.ListLogStores(project, 0, 10, query);
            } catch (LogException ex) {
                ex.printStackTrace();
                fail(ex.GetErrorMessage());
            }
            for (int j = 0; j < numberOfLogstore; j++) {
                String x = "logstore-" + j;
                if (x.contains(query)) {
                    total++;
                }
            }
            int count = Math.min(10, total);
            assertEquals(count, response2.GetCount());
            assertEquals(total, response2.GetTotal());
        }
        ListLogStoresResponse response = client.ListLogStores(project, 0, 100, "logstore-");
        assertEquals(numberOfLogstore, response.GetTotal());
        assertEquals(numberOfLogstore, response.GetCount());

        int size = randomInt(numberOfLogstore) + 1;

        response = client.ListLogStores(project, 0, size, "");
        assertEquals(numberOfLogstore, response.GetTotal());
        assertEquals(size, response.GetCount());

        for (int i = 0; i < numberOfLogstore; i++) {
            String logstoreName = "logstore-" + i;
            client.DeleteLogStore(project, logstoreName);
            System.out.println("Delete logstore " + logstoreName);
            int total = 0;
            ListLogStoresResponse response2 = client.ListLogStores(project, 0, 10, logstoreName);
            for (int j = i + 1; j < numberOfLogstore; j++) {
                String x = "logstore-" + j;
                if (x.contains(logstoreName)) {
                    total++;
                }
            }
            List<String> logstores = response2.GetLogStores();
            assertFalse(logstores.contains(logstoreName));
            int count = Math.min(10, total);
            assertEquals(count, response2.GetCount());
            assertEquals(total, response2.GetTotal());
        }
    }

    @Test
    public void testConcurrentModify() throws Exception {
        int max = randomInt(10);
        Thread[] threads = new Thread[max];
        final AtomicReference<Exception> error = new AtomicReference<Exception>();
        for (int i = 0; i < max; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        crudLogstore();
                    } catch (Exception ex) {
                        error.set(ex);
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < max; i++) {
            threads[i].join();
        }
        final Exception err = error.get();
        if (err != null) {
            err.printStackTrace();
            fail(err.getMessage());
        }
    }
}
