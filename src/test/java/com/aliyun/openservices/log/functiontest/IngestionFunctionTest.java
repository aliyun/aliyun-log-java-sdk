package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.AliyunOSSSource;
import com.aliyun.openservices.log.common.DelimitedTextFormat;
import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.common.IngestionConfiguration;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateIngestionRequest;
import com.aliyun.openservices.log.request.DeleteIngestionRequest;
import com.aliyun.openservices.log.request.GetIngestionRequest;
import com.aliyun.openservices.log.request.StartIngestionRequest;
import com.aliyun.openservices.log.request.StopIngestionRequest;
import com.aliyun.openservices.log.request.UpdateIngestionRequest;
import com.aliyun.openservices.log.response.GetIngestionResponse;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
@Ignore
public class IngestionFunctionTest extends JobIntgTest {

    private static String getIngestionName() {
        return "ingestion-" + getNowTimestamp();
    }

    private Ingestion createIngestion() {
        Ingestion ingestion = new Ingestion();
        String jobName = getIngestionName();
        ingestion.setName(jobName);
        ingestion.setDisplayName("OSS-test");
        IngestionConfiguration configuration = new IngestionConfiguration();
        configuration.setLogstore("test-logstore1");
        AliyunOSSSource source = new AliyunOSSSource();
        source.setBucket("yunlei-bill");
        source.setEncoding("UTF-8");
        source.setEndpoint("oss-cn-beijing.aliyuncs.com");
        source.setRoleARN("acs:ram::1654218965343050:role/osstologservicerole");
        DelimitedTextFormat format = new DelimitedTextFormat();
        format.setEscapeChar("\\");
        format.setFirstRowAsHeader(true);
        format.setSkipLeadingRows(0);
        format.setQuoteChar("\"");
        format.setFieldDelimiter(",");
        source.setFormat(format);
        source.setRestoreObjectEnabled(true);
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
        String project = "ali-sls-etl-staging";
        client.createIngestion(new CreateIngestionRequest(project, ingestion));
        client.updateIngestion(new UpdateIngestionRequest(project, ingestion));
        GetIngestionResponse response = client.getIngestion(new GetIngestionRequest(project, jobName));
        Ingestion ingestion1 = response.getIngestion();
        assertEquals(jobName, ingestion1.getName());
        assertEquals("ENABLED", ingestion1.getStatus());
        client.stopIngestion(new StopIngestionRequest(project, jobName));
        response = client.getIngestion(new GetIngestionRequest(project, jobName));
        Ingestion ingestion2 = response.getIngestion();
        assertEquals(jobName, ingestion2.getName());
        assertEquals("DISABLED", ingestion2.getStatus());
        try {
            client.stopIngestion(new StopIngestionRequest(project, jobName));
            fail();
        } catch (LogException ex) {
            assertEquals("The job to stop has already stopped", ex.GetErrorMessage());
            assertEquals("ParameterInvalid", ex.GetErrorCode());
        }
        client.startIngestion(new StartIngestionRequest(project, jobName));
        response = client.getIngestion(new GetIngestionRequest(project, jobName));
        Ingestion ingestion3 = response.getIngestion();
        assertEquals("ENABLED", ingestion3.getStatus());
        try {
            client.startIngestion(new StartIngestionRequest(project, jobName));
            fail();
        } catch (LogException ex) {
            assertEquals("The job to start has already started", ex.GetErrorMessage());
            assertEquals("ParameterInvalid", ex.GetErrorCode());
        }
        // Long live
        // Can we forbid this?
        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.RESIDENT);
        ingestion.setSchedule(schedule);
        client.updateIngestion(new UpdateIngestionRequest(project, ingestion));
        client.deleteIngestion(new DeleteIngestionRequest(project, ingestion.getName()));
    }
}
