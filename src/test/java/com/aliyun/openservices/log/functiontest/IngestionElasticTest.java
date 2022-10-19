package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IngestionElasticTest extends JobIntgTest {

    private static final String TEST_PROJECT = "test-ingestion-1";
    private static final String TEST_LOGSTORE = "chuzhi-test-etl20";
    private static final String TEST_JOB = "ingest-es-unittest-" + System.currentTimeMillis();

    @Test
    public void testLifeCycle() throws Exception {
        Ingestion ingestion = createIngestion(false);
        CreateIngestionRequest req1 = new CreateIngestionRequest(TEST_PROJECT, ingestion);
        client.createIngestion(req1);

        try {

            runWithRetry(new TestRunnable() {
                @Override
                public void run() throws  Exception{
                    StopIngestionRequest req = new StopIngestionRequest(TEST_PROJECT, TEST_JOB);
                    client.stopIngestion(req);
                }
            }, "STARTING");

            runWithRetry(new TestRunnable() {
                @Override
                public void run() throws  Exception{
                    StartIngestionRequest req = new StartIngestionRequest(TEST_PROJECT, TEST_JOB);
                    client.startIngestion(req);
                }
            }, "STOPPING");

            runWithRetry(new TestRunnable() {
                @Override
                public void run() throws  Exception{
                    RestartIngestionRequest req = new RestartIngestionRequest(TEST_PROJECT, createIngestion(true));
                    client.restartIngestion(req);
                }
            }, "STARTING");
        } finally {
            DeleteIngestionRequest req6 = new DeleteIngestionRequest(TEST_PROJECT, TEST_JOB);
            client.deleteIngestion(req6);
        }
    }

    private void runWithRetry(TestRunnable run, String retryOnStatus) {
        while(true) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                run.run();
                break;
            } catch (LogException err) {
                if (!err.getMessage().contains(retryOnStatus)) {
                    throw new RuntimeException(err);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Ingestion createIngestion(boolean restart) {
        Ingestion ingestion = new Ingestion();
        ingestion.setName(TEST_JOB);
        ingestion.setDisplayName("unittest-job-" + (restart ? "new" : "restarted"));
        IngestionConfiguration configuration = new IngestionConfiguration();
        configuration.setLogstore(TEST_LOGSTORE);
        configuration.setVersion("v2.0");

        IngestionGeneralSource source = new IngestionGeneralSource();
        JSONObject sourceJson = JSON.parseObject("{\n" +
                "    \"displayName\":\"import-es-test\",\n" +
                "    \"BootstrapServers\":\"http://192.168.26.38:9200/\",\n" +
                "    \"Index\":\"ingestion-test\",\n" +
                "    \"VpcId\":\"vpc-bp1949587myedyj8s1bqw\",\n" +
                "    \"Restart\":" + restart + ",\n" +
                "    \"type\":\"ElasticSearch\"\n" +
                "}");
        for (Map.Entry<String, Object> sourceEntry : sourceJson.entrySet()) {
            source.put(sourceEntry.getKey(), sourceEntry.getValue());
        }
        configuration.setSource(source);

        ingestion.setConfiguration(configuration);

        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.RESIDENT);
        ingestion.setSchedule(schedule);

        return ingestion;
    }

    interface TestRunnable {
        void run() throws Exception;
    }
}
