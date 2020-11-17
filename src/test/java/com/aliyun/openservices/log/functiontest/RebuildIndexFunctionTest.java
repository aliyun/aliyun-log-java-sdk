package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.GetRebuildIndexResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import com.aliyun.openservices.log.response.ListRebuildIndexResponse;
import org.junit.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RebuildIndexFunctionTest extends FunctionTest {

    static String project = "test-project-to-alert-" + getNowTimestamp();
    static String logstore = "test_rebuild_index_" + getNowTimestamp();
    static String jobName = "rebuild-index-6";

    @Before
    public void setUp() {
        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(logstore);
        logStore.SetTtl(1);
        logStore.SetShardCount(1);
        createOrUpdateLogStore(project, logStore);
        enableIndex();
    }

    @After
    public void clearData() throws Exception {
        safeDeleteLogStore(project, logstore);
        deleteAll();
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
                safeDeleteProject(project.getProjectName());
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
        Thread.sleep(3000);
        testGet();
    }

    @Ignore
    @Test
    public void testGet() throws Exception {
        GetRebuildIndexResponse response = client.getRebuildIndex(new GetRebuildIndexRequest(project, jobName));
        RebuildIndex ri = response.getRebuildIndex();
        Assert.assertEquals(jobName, ri.getName());
        Assert.assertEquals("test rebuild index", ri.getDisplayName());
    }

    @Ignore
    @Test
    public void testStop() {
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

    @Ignore
    @Test
    public void testInvalidOperation() throws Exception {
        client.enableJob(new EnableJobRequest(project, jobName));
        client.disableJob(new DisableJobRequest(project, jobName));
    }

    private static void enableIndex() {
        Index index = new Index();
        index.SetTtl(7);
        index.setMaxTextLen(0);
        index.setLogReduceEnable(false);
        List<String> list = Arrays.asList(",", " ", "'", "\"", ";", "=", "(", ")", "[", "]", "{", "}", "?", "@", "&", "<", ">", "/", ":", "\n", "\t", "\r");
        IndexKeys indexKeys = new IndexKeys();
        for (int i = 1; i <= 10; i++) {
            indexKeys.AddKey("key-"+i, new IndexKey(list,false, "text", ""));
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
