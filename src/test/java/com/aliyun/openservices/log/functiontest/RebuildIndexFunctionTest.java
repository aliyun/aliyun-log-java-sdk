package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.IndexKey;
import com.aliyun.openservices.log.common.IndexKeys;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.common.RebuildIndex;
import com.aliyun.openservices.log.common.RebuildIndexConfiguration;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateIndexRequest;
import com.aliyun.openservices.log.request.CreateRebuildIndexRequest;
import com.aliyun.openservices.log.request.DeleteRebuildIndexRequest;
import com.aliyun.openservices.log.request.DisableJobRequest;
import com.aliyun.openservices.log.request.EnableJobRequest;
import com.aliyun.openservices.log.request.GetRebuildIndexRequest;
import com.aliyun.openservices.log.request.ListRebuildIndexRequest;
import com.aliyun.openservices.log.request.StopRebuildIndexRequest;
import com.aliyun.openservices.log.response.GetRebuildIndexResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import com.aliyun.openservices.log.response.ListRebuildIndexResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RebuildIndexFunctionTest extends FunctionTest {

    private static final String project = "test-project-to-alert-" + getNowTimestamp();
    private static final String logstore = "test_rebuild_index_" + getNowTimestamp();
    private static final String jobName = "rebuild-index-6";

    @Before
    public void setUp() {
        safeCreateProject(project, "RebuildIndexFunctionTest");
        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(logstore);
        logStore.SetTtl(1);
        logStore.SetShardCount(1);
        createOrUpdateLogStoreNoWait(project, logStore);
        enableIndex();
    }

    @After
    public void clearData() throws Exception {
        safeDeleteLogStore(project, logstore);
        deleteAll();
        safeDeleteProjectWithoutSleep(project);
    }

    private final static String INDEX_STRING = "{\"log_reduce\":false,\"line\":{\"caseSensitive\":false,\"chn\":false,\"token\":" +
            "[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]}," +
            "\"keys\":{\"key1\":{\"doc_value\":true,\"caseSensitive\":false,\"chn\":false,\"alias\":\"\",\"type\":\"text\"," +
            "\"token\":[\",\",\" \",\"'\",\"\\\"\",\";\",\"=\",\"(\",\")\",\"[\",\"]\",\"{\",\"}\",\"?\",\"@\",\"&\",\"<\",\">\",\"/\",\":\",\"\\n\",\"\\t\",\"\\r\"]}," +
            "\"key2\":{\"doc_value\":true,\"alias\":\"\",\"type\":\"long\"}},\"ttl\":1}";


    private void deleteAll() throws Exception {
        ListProjectResponse response = client.ListProject(project, 0, 100);
        if (response != null) {
            for (Project project : response.getProjects()) {
                safeDeleteProjectWithoutSleep(project.getProjectName());
            }
        }
    }

    private RebuildIndex createRebuildIndex() {
        RebuildIndex job = new RebuildIndex();
        job.setName(jobName);
        job.setDisplayName("test rebuild index");
        RebuildIndexConfiguration configuration = new RebuildIndexConfiguration();
        configuration.setLogstore(logstore);
        configuration.setFromTime((int) ((System.currentTimeMillis() / (long) 1000) - 864000));
        configuration.setToTime((int) ((System.currentTimeMillis() / (long) 1000) - 900));
        job.setConfiguration(configuration);
        return job;
    }

    @Test
    public void testCreate() throws Exception {
        RebuildIndex job = createRebuildIndex();
        client.createRebuildIndex(new CreateRebuildIndexRequest(project, job));//index config doesn't exist
        waitForSeconds(3);
        testGet();
        testInvalidOperation();
    }

    private void testGet() throws Exception {
        GetRebuildIndexResponse response = client.getRebuildIndex(new GetRebuildIndexRequest(project, jobName));
        RebuildIndex ri = response.getRebuildIndex();
        assertEquals(jobName, ri.getName());
        assertEquals("test rebuild index", ri.getDisplayName());
    }

    private void testStop() {
        try {
            client.stopRebuildIndex(new StopRebuildIndexRequest(project, jobName));
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
//            assertEquals("The job to stop has already stopped", e.GetErrorMessage()); //there are two kinds of error messages
            //  {The job to stop has already stopped/Previous operation has not finished}
        }
    }

    @Test
    public void testDelete() throws Exception {
        testCreate();
        testStop();
        client.deleteRebuildIndex(new DeleteRebuildIndexRequest(project, jobName));
    }

    @Test
    public void testList() throws Exception {
        testCreate();
        ListRebuildIndexResponse response = client.listRebuildIndex(new ListRebuildIndexRequest(project));
        assertEquals(1, response.getCount().intValue());
        assertEquals(1, response.getTotal().intValue());
        assertEquals("rebuild-index-6", response.getResults().get(0).getName());
    }

    private void testInvalidOperation() {
        try {
            client.enableJob(new EnableJobRequest(project, jobName));
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
        }
        try {
            client.disableJob(new DisableJobRequest(project, jobName));
        } catch (LogException e) {
            assertEquals("ParameterInvalid", e.GetErrorCode());
        }
    }

    private static void enableIndex() {
        Index index = new Index();
        index.SetTtl(7);
        index.setMaxTextLen(0);
        index.setLogReduceEnable(false);
        List<String> list = Arrays.asList(",", " ", "'", "\"", ";", "=", "(", ")", "[", "]", "{", "}", "?", "@", "&", "<", ">", "/", ":", "\n", "\t", "\r");
        IndexKeys indexKeys = new IndexKeys();
        for (int i = 1; i <= 10; i++) {
            indexKeys.AddKey("key-" + i, new IndexKey(list, false, "text", ""));
        }
        index.SetKeys(indexKeys);
        try {
            client.CreateIndex(new CreateIndexRequest(project, logstore, index));
        } catch (LogException e) {
            fail("Enable Index Failed!");
        }
        waitOneMinutes();
    }
}
