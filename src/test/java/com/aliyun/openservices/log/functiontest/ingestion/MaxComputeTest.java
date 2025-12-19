package com.aliyun.openservices.log.functiontest.ingestion;

import com.aliyun.openservices.log.common.AliyunMaxComputeSource;
import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.common.IngestionConfiguration;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.common.JobState;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.etl.JobIntgTest;
import com.aliyun.openservices.log.request.CreateIngestionRequest;
import com.aliyun.openservices.log.request.GetIngestionRequest;
import com.aliyun.openservices.log.request.StartIngestionRequest;
import com.aliyun.openservices.log.request.StopIngestionRequest;
import com.aliyun.openservices.log.request.UpdateIngestionRequest;
import com.aliyun.openservices.log.response.GetIngestionResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;


public class MaxComputeTest extends JobIntgTest {
    private static String getIngestionName() {
        return "ingestion-" + getNowTimestamp();
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
        try {
            client.CreateLogStore(TEST_PROJECT, new LogStore("test-logstore2", 1, 1));
        } catch (LogException e) {
            assertEquals("LogStoreAlreadyExist", e.GetErrorCode());
        }
    }

    private Ingestion createIngestion() {
        Ingestion ingestion = new Ingestion();
        String jobName = getIngestionName();
        ingestion.setName(jobName);
        ingestion.setState(JobState.ENABLED);
        ingestion.setDisplayName("BSS-test");
        IngestionConfiguration configuration = new IngestionConfiguration();
        configuration.setLogstore("test-logstore2");
        AliyunMaxComputeSource source = new AliyunMaxComputeSource();
        source.setAccessKeyID("accessKeyId");
        source.setAccessKeySecret("accessKeySecret");
        source.setEndpoint("endpoint");
        source.setProject("project");
        source.setPartitionSpec("partitionSpec");
        source.setTable("table");
        source.setTunnelEndpoint("tunnelEndpoint");
        configuration.setSource(source);
        ingestion.setConfiguration(configuration);
        JobSchedule schedule = new JobSchedule();
        schedule.setInterval("1h");
        schedule.setType(JobScheduleType.FIXED_RATE);
        ingestion.setSchedule(schedule);
        return ingestion;
    }

    @Test
    public void testCrud() throws Exception {
        Ingestion ingestion = createIngestion();
        String jobName = ingestion.getName();
        String project = TEST_PROJECT;
        client.createIngestion(new CreateIngestionRequest(project, ingestion));
        client.updateIngestion(new UpdateIngestionRequest(project, ingestion));
        GetIngestionResponse response = client.getIngestion(new GetIngestionRequest(project, jobName));
        Ingestion ingestion1 = response.getIngestion();
        assertEquals(jobName, ingestion1.getName());
        assertEquals(JobState.ENABLED, ingestion1.getState());
        assertEquals("ENABLED", ingestion1.getStatus());
        client.stopIngestion(new StopIngestionRequest(project, jobName));
        response = client.getIngestion(new GetIngestionRequest(project, jobName));
        Ingestion ingestion2 = response.getIngestion();
        assertEquals(jobName, ingestion2.getName());
        assertEquals(JobState.DISABLED, ingestion2.getState());
        assertEquals("DISABLED", ingestion2.getStatus());
        try {
            client.stopIngestion(new StopIngestionRequest(project, jobName));
            fail();
        } catch (LogException ex) {
            assertEquals("The job to disable has already disabled", ex.GetErrorMessage());
            assertEquals("ParameterInvalid", ex.GetErrorCode());
        }
        client.startIngestion(new StartIngestionRequest(project, jobName));
        response = client.getIngestion(new GetIngestionRequest(project, jobName));
        Ingestion ingestion3 = response.getIngestion();
        assertEquals(JobState.ENABLED, ingestion3.getState());
        assertEquals("ENABLED", ingestion3.getStatus());
        try {
            client.startIngestion(new StartIngestionRequest(project, jobName));
            fail();
        } catch (LogException ex) {
            assertEquals("The job to enable has already enabled", ex.GetErrorMessage());
            assertEquals("ParameterInvalid", ex.GetErrorCode());
        }
        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.FIXED_RATE); // The schedule type change not allowed
        schedule.setInterval("1h");
        ingestion.setSchedule(schedule);
        client.updateIngestion(new UpdateIngestionRequest(project, ingestion));
    }
}
