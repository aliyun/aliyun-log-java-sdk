package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.RebuildIndex;
import com.aliyun.openservices.log.common.RebuildIndexConfiguration;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateRebuildIndexRequest;
import com.aliyun.openservices.log.request.DisableJobRequest;
import com.aliyun.openservices.log.request.GetRebuildIndexRequest;
import com.aliyun.openservices.log.request.ListRebuildIndexRequest;
import com.aliyun.openservices.log.request.StopRebuildIndexRequest;
import com.aliyun.openservices.log.response.GetRebuildIndexResponse;
import com.aliyun.openservices.log.response.ListRebuildIndexResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RebuildIndexFunctionTest extends JobIntgTest {

    private static String getRebuildIndexName() {
        return "rebuild-index-" + getNowTimestamp();
    }

    private RebuildIndex createRebuildIndex() {
        RebuildIndex RebuildIndex = new RebuildIndex();
        String jobName = getRebuildIndexName();
        RebuildIndex.setName(jobName);
        RebuildIndex.setDisplayName("OSS-test");
        RebuildIndexConfiguration configuration = new RebuildIndexConfiguration();
        configuration.setLogstore("test-logstore2");
        configuration.setStartTime(getNowTimestamp() - 1000);
        configuration.setEndTime(getNowTimestamp());
        RebuildIndex.setConfiguration(configuration);
        return RebuildIndex;
    }

    @Test
    public void testCrud() throws Exception {
        RebuildIndex job = createRebuildIndex();
        String jobName = job.getName();
        String project = "ali-sls-etl-staging";
        client.createRebuildIndex(new CreateRebuildIndexRequest(project, job));
        GetRebuildIndexResponse response = client.getRebuildIndex(new GetRebuildIndexRequest(project, jobName));
        RebuildIndex rebuildIndex = response.getRebuildIndex();
        assertEquals("STARTING", rebuildIndex.getStatus());
        assertEquals(jobName, rebuildIndex.getName());
        client.stopRebuildIndex(new StopRebuildIndexRequest(project, jobName));
        response = client.getRebuildIndex(new GetRebuildIndexRequest(project, jobName));
        RebuildIndex rebuildIndex1 = response.getRebuildIndex();
        assertEquals(jobName, rebuildIndex1.getName());
        try {
            client.stopRebuildIndex(new StopRebuildIndexRequest(project, jobName));
            fail();
        } catch (LogException ex) {
            assertEquals("The job to stop has already stopped", ex.GetErrorMessage());
            assertEquals("ParameterInvalid", ex.GetErrorCode());
        }
//        client.startRebuildIndex(new StartRebuildIndexRequest(project, jobName));
        response = client.getRebuildIndex(new GetRebuildIndexRequest(project, jobName));

//        RebuildIndex RebuildIndex3 = response.getRebuildIndex();
////        assertEquals(JobState.ENABLED, RebuildIndex3.getState());
////        assertEquals("ENABLED", RebuildIndex3.getStatus());
//        try {
//            client.startRebuildIndex(new StartRebuildIndexRequest(project, jobName));
//            fail();
//        } catch (LogException ex) {
//            assertEquals("The job to start has already started", ex.GetErrorMessage());
//            assertEquals("ParameterInvalid", ex.GetErrorCode());
//        }
//        // Long live
//        // Can we forbid this?
//        JobSchedule schedule = new JobSchedule();
//        schedule.setType(JobScheduleType.RESIDENT);
//        RebuildIndex.setSchedule(schedule);
//        client.updateRebuildIndex(new UpdateRebuildIndexRequest(project, RebuildIndex));
//        // TODO fixme
    }

    @Test
    public void testGet() throws Exception {
        GetRebuildIndexResponse response = client.getRebuildIndex(new GetRebuildIndexRequest("ali-sls-etl-staging", "RebuildIndex-1563030173"));
        RebuildIndex RebuildIndex = response.getRebuildIndex();
        System.out.println(RebuildIndex.getName());
    }

    @Test
    public void testInvalidOperation() throws Exception {
//        client.enableJob(new EnableJobRequest("ali-sls-etl-staging", "RebuildIndex-1562994887"));
        client.disableJob(new DisableJobRequest("ali-sls-etl-staging", "rebuild-index-1563030173"));
    }

    @Test
    public void testDelete() throws Exception {
        client.getRebuildIndex(new GetRebuildIndexRequest("ali-sls-etl-staging", "rebuild-index-1563261253"));
        //   client.stopRebuildIndex(new StopRebuildIndexRequest("ali-sls-etl-staging", "RebuildIndex-1562994571"));
        client.stopRebuildIndex(new StopRebuildIndexRequest("ali-sls-etl-staging", "rebuild-index-1563261253"));
//        client.startRebuildIndex(new StartRebuildIndexRequest("ali-sls-etl-staging", "RebuildIndex-1562994571"));
//        client.startRebuildIndex(new StartRebuildIndexRequest("ali-sls-etl-staging", "RebuildIndex-1562994571"));
        //  client.startRebuildIndex(new StartRebuildIndexRequest("ali-sls-etl-staging", "RebuildIndex-1563261253"));
        //  client.deleteRebuildIndex(new DeleteRebuildIndexRequest("ali-sls-etl-staging", "RebuildIndex-1563261253"));
    }

    @Test
    public void testList() throws Exception {
        ListRebuildIndexResponse response = client.listRebuildIndex(new ListRebuildIndexRequest("ali-sls-etl-staging"));
        System.out.println(response.getCount());
        System.out.println(response.getTotal());
        for (RebuildIndex rebuildIndex : response.getResults()) {
            System.out.println(rebuildIndex.getName());
        }
    }
}
