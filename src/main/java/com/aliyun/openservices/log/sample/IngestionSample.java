package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.AliyunCloudMonitorSource;
import com.aliyun.openservices.log.common.AliyunOSSSource;
import com.aliyun.openservices.log.common.DelimitedTextFormat;
import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.common.IngestionConfiguration;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateIngestionRequest;

import java.util.Arrays;

public class IngestionSample {
    public static void main(String[] args) {
        OSSIngestionSample oss = new OSSIngestionSample();
        oss.testOSSIngestion();

        CloudMonitorSample cms = new CloudMonitorSample();
        cms.createIngestTask();
    }
}

class CloudMonitorSample {

    private final String endPoint = "your_endpoint";
    private final String accessId = "your_access_id";
    private final String accessKey = "your_access_key";
    private final Client client = new Client(endPoint, accessId, accessKey);
    private final String project = "your_project_name";
    private final String logStore = "your_log_store";
    private final String displayName = "your_display_name";
    private final String interval = "1h";           //Your ingestion interval, default 1h

    public void createIngestTask() {
        Ingestion ingestion = new Ingestion();
        ingestion.setName("cloud-monitor-data");
        ingestion.setDisplayName(displayName);
        IngestionConfiguration configuration = new IngestionConfiguration();
        configuration.setLogstore(logStore);
        AliyunCloudMonitorSource source = new AliyunCloudMonitorSource();
        source.setAccessKeyID(accessId);
        source.setAccessKeySecret(accessKey);
        // see http://metricmeta.oss-cn-hangzhou.aliyuncs.com/listMetricMeta_zh.html
        source.setNamespaces(Arrays.asList("ecs", "xxx"));
        source.setOutputType("SLSMetric");
        configuration.setSource(source);
        ingestion.setConfiguration(configuration);
        JobSchedule schedule = new JobSchedule();
        schedule.setInterval(interval);
        schedule.setType(JobScheduleType.FIXED_RATE);
        ingestion.setSchedule(schedule);
        try {
            client.createIngestion(new CreateIngestionRequest(project, ingestion));
        } catch (LogException e) {
            System.err.println("Create cloud monitor ingestion Error ! " + e.GetErrorMessage());
        }
    }
}

class OSSIngestionSample {

    private final String endPoint = "your_endpoint";
    private final String accessId = "your_access_id";
    private final String accessKey = "your_access_key";
    private final String roleARN = "your_roleARN";
    private final Client client = new Client(endPoint, accessId, accessKey);
    private final String project = "your_project_name";
    private final String logStore = "your_log_store";
    private final String bucket = "your_bucket_name";
    private final String displayName = "your_display_name";
    private final String encoding = "UTF-8";        //Your encoding format, default UTF-8
    private final String interval = "1h";           //Your ingestion interval, default 1h
    private final String escapeChar = "\\";         //Your escape character, default \
    private final String quoteChar = "\"";          //Your quote character, default "
    private final String fieldDelimiter = "\"";     //Your field delimiter, default ,
    private final boolean firstRowAsHeader = true;  //First line as header, default true
    private final boolean enable = true;  //Your restore Object enabled, default true
    private final int skipLeadingRows = 0; //Skip Leading Rows, default 0

    public OSSIngestionSample() {
    }

    public void testOSSIngestion() {
        Ingestion ingestion = createOSSIngestion();
        try {
            client.createIngestion(new CreateIngestionRequest(project, ingestion));
        } catch (LogException e) {
            System.err.println("Create OSS Ingestion Error ! " + e.GetErrorMessage());
        }
    }

    private Ingestion createOSSIngestion() {
        Ingestion ingestion = new Ingestion();
        String jobName = getIngestionName();
        ingestion.setName(jobName);
        ingestion.setDisplayName(displayName);
        IngestionConfiguration configuration = new IngestionConfiguration();
        configuration.setLogstore(logStore);
        AliyunOSSSource source = new AliyunOSSSource();
        source.setBucket(bucket);
        source.setEncoding(encoding);
        source.setEndpoint(endPoint);
        source.setRoleARN(roleARN);
        DelimitedTextFormat format = new DelimitedTextFormat();//There are also JSONFormat， ParquetFormat
        format.setEscapeChar(escapeChar);
        format.setFirstRowAsHeader(firstRowAsHeader);
        format.setSkipLeadingRows(skipLeadingRows);
        format.setQuoteChar(quoteChar);
        format.setFieldDelimiter(fieldDelimiter);
        source.setFormat(format);
        source.setRestoreObjectEnabled(enable);
        source.setCompressionCodec("Gzip");
        configuration.setSource(source);
        ingestion.setConfiguration(configuration);
        JobSchedule schedule = new JobSchedule();
        schedule.setInterval(interval);
        schedule.setType(JobScheduleType.FIXED_RATE);
        ingestion.setSchedule(schedule);
        return ingestion;
    }

    private static String getIngestionName() {
        return "ingestion-" + (int) (System.currentTimeMillis() / 1000);
    }
}