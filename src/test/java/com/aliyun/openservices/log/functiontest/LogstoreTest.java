package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetIndexResponse;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class LogstoreTest extends FunctionTest {


    private final String PROJECT_PREFIX = "test-java-sdk-logstore-";


    private final static String INDEX_STRING = "{\"log_reduce\":false,\"line\":{\"caseSensitive\":false,\"chn\":false,\"token\":" +
            "[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]}," +
            "\"keys\":{\"key1\":{\"doc_value\":true,\"caseSensitive\":false,\"chn\":false,\"alias\":\"\",\"type\":\"text\"," +
            "\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]}," +
            "\"key2\":{\"doc_value\":true,\"alias\":\"\",\"type\":\"long\"}},\"ttl\":1}";

    @Before
    public void setUp() throws Exception {
        deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        deleteAll();
    }

    private void deleteAll() throws Exception {
        ListProjectResponse response = client.ListProject(PROJECT_PREFIX, 0, 100);
        if (response != null) {
            for (Project project : response.getProjects()) {
                deleteProject(project.getProjectName());
            }
        }
    }

    private void deleteProject(String project) {
        try {
            ListLogStoresResponse response = client.ListLogStores(project, 0, 100, "");
            for (String logstore : response.GetLogStores()) {
                client.DeleteLogStore(project, logstore);
            }
        } catch (LogException ex) {
            ex.printStackTrace();
        }
        try {
            client.DeleteProject(project);
            waitForSeconds(30);
        } catch (LogException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testGetIndex() throws Exception {
        String project = PROJECT_PREFIX + randomInt();
        Index index = new Index();
        index.FromJsonString(INDEX_STRING);
        while (true) {
            try {
                client.CreateProject(project, "");
                break;
            } catch (LogException ex) {
                if (ex.GetErrorCode().equalsIgnoreCase("ProjectAlreadyExist")) {
                    waitForSeconds(10);
                } else {
                    fail(ex.GetErrorMessage());
                }
            }
        }
        int numberOfLogstore = randomInt(100);
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
            client.DeleteLogStore(project, "logstore-" + i);
            int total = 0;
            String query = "logstore-" + i;
            ListLogStoresResponse response2 = client.ListLogStores(project, 0, 10, query);
            for (int j = i + 1; j < numberOfLogstore; j++) {
                String x = "logstore-" + j;
                if (x.contains(query)) {
                    total++;
                }
            }
            int count = Math.min(10, total);
            assertEquals(count, response2.GetCount());
            assertEquals(total, response2.GetTotal());
        }
    }

    private void crudLogstore() throws Exception {
        String project = PROJECT_PREFIX + randomInt();
        while (true) {
            try {
                client.CreateProject(project, "");
                break;
            } catch (LogException ex) {
                if (ex.GetErrorCode().equalsIgnoreCase("ProjectAlreadyExist")) {
                    waitForSeconds(10);
                } else {
                    throw ex;
                }
            }
        }
        int numberOfLogstore = randomInt(100);
        for (int i = 0; i < numberOfLogstore; i++) {
            LogStore logStore = new LogStore();
            logStore.SetLogStoreName("logstore-" + i);
            logStore.SetShardCount(2);
            logStore.SetTtl(2);
            try {
                client.CreateLogStore(project, logStore);
            } catch (LogException ex) {
                ex.printStackTrace();
                throw ex;
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
                throw ex;
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
        int size = numberOfLogstore > 0 ? randomInt(numberOfLogstore) + 1 : 1;

        response = client.ListLogStores(project, 0, size, "");
        assertEquals(numberOfLogstore, response.GetTotal());
        assertEquals(size, response.GetCount());

        for (int i = 0; i < numberOfLogstore; i++) {
            client.DeleteLogStore(project, "logstore-" + i);
            int total = 0;
            String query = "logstore-" + i;
            ListLogStoresResponse response2 = client.ListLogStores(project, 0, 10, query);
            for (int j = i + 1; j < numberOfLogstore; j++) {
                String x = "logstore-" + j;
                if (x.contains(query)) {
                    total++;
                }
            }
            int count = Math.min(10, total);
            assertEquals(count, response2.GetCount());
            assertEquals(total, response2.GetTotal());
        }
    }

    @Test
    public void testCrud() throws Exception {
        String project = PROJECT_PREFIX + randomInt();
        while (true) {
            try {
                client.CreateProject(project, "");
                break;
            } catch (LogException ex) {
                if (ex.GetErrorCode().equalsIgnoreCase("ProjectAlreadyExist")) {
                    waitForSeconds(10);
                } else {
                    fail(ex.GetErrorMessage());
                }
            }
        }
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

        waitForSeconds(120);

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
                } else {
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
            client.DeleteLogStore(project, "logstore-" + i);
            int total = 0;
            String query = "logstore-" + i;
            ListLogStoresResponse response2 = client.ListLogStores(project, 0, 10, query);
            for (int j = i + 1; j < numberOfLogstore; j++) {
                String x = "logstore-" + j;
                if (x.contains(query)) {
                    total++;
                }
            }
            int count = Math.min(10, total);
            assertEquals(count, response2.GetCount());
            assertEquals(total, response2.GetTotal());
        }
    }

    @Test
    public void testConcurrentModify() throws Exception {
        int max = 4;
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
