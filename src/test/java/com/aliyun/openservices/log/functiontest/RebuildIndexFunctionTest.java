package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.RebuildIndex;
import com.aliyun.openservices.log.common.RebuildIndexConfiguration;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.DeleteRebuildIndexResponse;
import com.aliyun.openservices.log.response.GetRebuildIndexResponse;
import com.aliyun.openservices.log.response.ListRebuildIndexResponse;
import com.aliyun.openservices.log.response.StopRebuildIndexResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RebuildIndexFunctionTest extends JobIntgTest {

    String project = "project-to-test-alert";
    String logstore = "test_rebuild_index";
    String jobName = "rebuild-index-6";

    private RebuildIndex createRebuildIndex() {
        RebuildIndex RebuildIndex = new RebuildIndex();
        RebuildIndex.setName(jobName);
        RebuildIndex.setDisplayName("test rebuild index");
        RebuildIndexConfiguration configuration = new RebuildIndexConfiguration();
        configuration.setLogstore(logstore);
        configuration.setFromTime((int)((System.currentTimeMillis() / (long)1000) - 864000));
        configuration.setToTime((int)((System.currentTimeMillis() / (long)1000) - 900));
        RebuildIndex.setConfiguration(configuration);
        return RebuildIndex;
    }

    @Test
    public void testCreate() throws Exception {
        RebuildIndex job = createRebuildIndex();
        client.createRebuildIndex(new CreateRebuildIndexRequest(project, job));
        Thread.sleep(3000);
        testGet();
    }

    @Test
    public void testGet() throws Exception {
        GetRebuildIndexResponse response = client.getRebuildIndex(new GetRebuildIndexRequest(project, jobName));
        RebuildIndex ri = response.getRebuildIndex();
        System.out.println("job: " + ri.getName() + "\nstatus: " +  ri.getStatus() + "\nexecutionDetails: " + ri.getExecutionDetails());
    }

    @Test
    public void testStop() throws Exception {
        StopRebuildIndexResponse response = client.stopRebuildIndex(new StopRebuildIndexRequest(project, jobName));
        System.out.println(response.GetAllHeaders());
        testGet();
    }

    @Test
    public void testDelete() throws Exception {
        DeleteRebuildIndexResponse response = client.deleteRebuildIndex(new DeleteRebuildIndexRequest(project, jobName));
        System.out.println(response.GetAllHeaders());
    }

    @Test
    public void testList() throws Exception {
        ListRebuildIndexResponse response = client.listRebuildIndex(new ListRebuildIndexRequest(project));
        System.out.println(response.getCount());
        System.out.println(response.getTotal());
        for (RebuildIndex rebuildIndex : response.getResults()) {
            System.out.println(rebuildIndex.getName());
        }
    }

    @Test
    public void testInvalidOperation() throws Exception {
        client.enableJob(new EnableJobRequest(project, jobName));
        client.disableJob(new DisableJobRequest(project, jobName));
    }
}
