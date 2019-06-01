package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.DataSink;
import com.aliyun.openservices.log.common.ETL;
import com.aliyun.openservices.log.common.ETLConfiguration;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateETLRequest;
import com.aliyun.openservices.log.request.CreateJobScheduleRequest;
import com.aliyun.openservices.log.request.DeleteETLRequest;
import com.aliyun.openservices.log.request.GetETLRequest;
import com.aliyun.openservices.log.request.GetJobScheduleRequest;
import com.aliyun.openservices.log.request.ListETLRequest;
import com.aliyun.openservices.log.request.UpdateETLRequest;
import com.aliyun.openservices.log.request.UpdateJobScheduleRequest;
import com.aliyun.openservices.log.response.CreateJobScheduleResponse;
import com.aliyun.openservices.log.response.GetETLResponse;
import com.aliyun.openservices.log.response.GetJobScheduleResponse;
import com.aliyun.openservices.log.response.ListETLResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ETLIntgTest extends JobIntgTest {

    private static String getJobName() {
        return "etl-" + getNowTimestamp();
    }

    private ETL createETL() {
        ETL etl = new ETL();
        String jobName = getJobName();
        etl.setName(jobName);
        etl.setDisplayName("ETL-test");
        etl.setDescription("Initial description");
        ETLConfiguration configuration = new ETLConfiguration();
        configuration.setLogstore("test-logstore");
        configuration.setScript("hello world");
        configuration.setVersion(1);
        configuration.setContainerImage("test-image");
        configuration.setInstanceType("Standard");
        configuration.setAccessKeyId("dummy");
        configuration.setAccessKeySecret("dummy");
        configuration.setParameters(Collections.<String, String>emptyMap());
        List<DataSink> sinks = new ArrayList<DataSink>();
        DataSink sink = new DataSink("test", "project", "logstore-test");
        sink.setAccessKeyId("1111");
        sink.setAccessKeySecret("111111");
        sinks.add(sink);
        configuration.setSinks(sinks);
        etl.setConfiguration(configuration);
        return etl;
    }

    @BeforeClass
    public static void prepareLogstore() {
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(1);
        logStore.SetLogStoreName("test-logstore");
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(true);
        createOrUpdateLogStore(TEST_PROJECT, logStore);
    }

    @Before
    public void cleanUp() throws Exception {
        ListETLRequest listETLRequest = new ListETLRequest(TEST_PROJECT);
        listETLRequest.setOffset(0);
        listETLRequest.setSize(100);
        ListETLResponse listETLResponse = client.listETL(listETLRequest);
        for (ETL item : listETLResponse.getResults()) {
            client.deleteETL(new DeleteETLRequest(TEST_PROJECT, item.getName()));
        }
    }

    @Test
    public void testETLCrud() throws Exception {
        ETL etl = createETL();
        client.createETL(new CreateETLRequest(TEST_PROJECT, etl));
        GetETLResponse response = client.getETL(new GetETLRequest(TEST_PROJECT, etl.getName()));
        ETL result = response.getEtl();
        assertEquals(etl.getName(), result.getName());
        assertEquals(etl.getDisplayName(), result.getDisplayName());

        System.out.println(etl.getConfiguration());
        System.out.println(result.getConfiguration());

        assertEquals(etl.getConfiguration(), result.getConfiguration());

        etl.setDisplayName("New display name");
        etl.setDescription("New description");
        client.updateETL(new UpdateETLRequest(TEST_PROJECT, etl));
        response = client.getETL(new GetETLRequest(TEST_PROJECT, etl.getName()));
        result = response.getEtl();
        assertEquals(etl.getName(), result.getName());
        assertEquals(etl.getDisplayName(), result.getDisplayName());
        assertEquals(etl.getConfiguration(), result.getConfiguration());

        ListETLRequest listETLRequest = new ListETLRequest(TEST_PROJECT);
        listETLRequest.setOffset(0);
        listETLRequest.setSize(100);
        ListETLResponse listETLResponse = client.listETL(listETLRequest);
        assertEquals(1, (int) listETLResponse.getCount());
        assertEquals(1, (int) listETLResponse.getTotal());

        client.deleteETL(new DeleteETLRequest(TEST_PROJECT, etl.getName()));
        try {
            client.getETL(new GetETLRequest(TEST_PROJECT, etl.getName()));
            fail();
        } catch (LogException ex) {
            assertEquals("Job " + etl.getName() + " does not exist", ex.GetErrorMessage());
        }
    }

    @Test
    public void testJobScheduleCrud() throws Exception {
        ETL etl = createETL();
        client.createETL(new CreateETLRequest(TEST_PROJECT, etl));

        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.DRY_RUN);
        schedule.setJobName(etl.getName());
        CreateJobScheduleResponse createJobScheduleResponse = client.createJobSchedule(new CreateJobScheduleRequest(TEST_PROJECT, schedule));

        String id = createJobScheduleResponse.getId();
        assertNotNull(id);

        GetJobScheduleResponse response = client.getJobSchedule(new GetJobScheduleRequest(TEST_PROJECT, id));
        JobSchedule jobSchedule = response.getJobSchedule();
        assertEquals(schedule.getType(), jobSchedule.getType());
        assertEquals(schedule.getJobName(), jobSchedule.getJobName());
        assertEquals(id, jobSchedule.getId());

        client.updateJobSchedule(new UpdateJobScheduleRequest(TEST_PROJECT, jobSchedule));

        response = client.getJobSchedule(new GetJobScheduleRequest(TEST_PROJECT, id));
        JobSchedule jobSchedule2 = response.getJobSchedule();
        assertEquals(schedule.getType(), jobSchedule2.getType());
        assertEquals(schedule.getJobName(), jobSchedule2.getJobName());
        assertEquals(id, jobSchedule2.getId());

        client.deleteETL(new DeleteETLRequest(TEST_PROJECT, etl.getName()));
    }
}
