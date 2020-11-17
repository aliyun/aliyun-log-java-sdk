package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateEtlJobRequest;
import com.aliyun.openservices.log.request.DeleteEtlJobRequest;
import com.aliyun.openservices.log.request.GetEtlJobRequest;
import com.aliyun.openservices.log.request.ListEtlJobRequest;
import com.aliyun.openservices.log.request.UpdateEtlJobRequest;
import com.aliyun.openservices.log.response.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class FcTriggerFunctionTest extends JobIntgTest {
    private static String fcRegion = "cn-hangzhou";
    private static String fcAccountId = credentials.getAliuid();
    private static String roleArn = "acs:ram::" + fcAccountId + ":role/aliyunlogetlrole";
    private static String fcService = "log_etl";
    private static String fcFunction = "logstore-replication";
    private static String etlJobName = "v2-test";
    private static String logstore = "from";
    private static String logLogstore = "etl-log";

    @BeforeClass
    public static void setup() {
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(1);
        logStore.SetLogStoreName(logstore);
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(true);
        createOrUpdateLogStore(TEST_PROJECT, logStore);
        waitForSeconds(1);
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(TEST_PROJECT, etlJobName);
            client.deleteEtlJob(req);
        } catch (LogException e) {
        }
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(TEST_PROJECT, etlJobName + "_1");
            client.deleteEtlJob(req);
        } catch (LogException e) {
        }
        waitForSeconds(1);
    }


    @AfterClass
    public static void cleanup() {

    }

    @Test
    public void testCrud(){
        testCreateEtlJob();
        testGetEtlJob();
        testUpdateEtlJob();
        testListEtlJob();
        testDeleteEtlJob();
    }

    private void testCreateEtlJob() {

        EtlSourceConfig sourceConfig = new EtlSourceConfig(logstore);
        EtlTriggerConfig triggerConfig = new EtlTriggerConfig(roleArn, 300, 1);
        EtlFunctionFcConfig fcConfig = new EtlFunctionFcConfig(Consts.FUNCTION_PROVIDER_FC, credentials.getEndpoint(), fcAccountId, fcRegion, fcService, fcFunction);
        EtlLogConfig logConfig = new EtlLogConfig(credentials.getEndpoint(), TEST_PROJECT, logLogstore);
        String functionParameter = "{\"source\":{\"endpoint\":\"http://cn-hangzhou-intranet.log.aliyuncs.com\"}, \"target\":{\"endpoint\":\"http://cn-hangzhou-intranet.log.aliyuncs.com\", \"projectName\":\"etl-test\", \"logstoreName\":\"etl-1\"}}";
        EtlJob job = new EtlJob(etlJobName, sourceConfig, triggerConfig, fcConfig, functionParameter, logConfig, true);
        try {
            sourceConfig.setLogstoreName("x");
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            sourceConfig.setLogstoreName(logstore);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            triggerConfig.setMaxRetryTime(1000);
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            triggerConfig.setMaxRetryTime(1);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            triggerConfig.setTriggerInterval(-1);
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            triggerConfig.setTriggerInterval(100);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            triggerConfig.setStartFromUnixtime(10000);
            triggerConfig.setStartingUnixtime(-1);
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            triggerConfig.setStartFromLastest();
            assertTrue(true);
        }
        try {
            triggerConfig.setRoleArn(" ");
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            triggerConfig.setRoleArn(roleArn);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            fcConfig.setFunctionProvider("StreamCompute");
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            fcConfig.setFunctionProvider(Consts.FUNCTION_PROVIDER_FC);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            fcConfig.setAccountId("");
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            fcConfig.setAccountId(fcAccountId);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            logConfig.setLogstoreName(logstore);
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            logConfig.setLogstoreName(logLogstore);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            logConfig.setProjectName("");
            logConfig.setLogstoreName(logstore);
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            logConfig.setLogstoreName(logLogstore);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            job.setFunctionParameter("xxxxx");
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            job.setFunctionParameter(functionParameter);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            job.setJobName("1");
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            job.setJobName(etlJobName);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            CreateEtlJobRequest req = new CreateEtlJobRequest(TEST_PROJECT, job);
            CreateEtlJobResponse createEtlJobResponse = client.createEtlJob(req);
            assertTrue(true);
        } catch (LogException e) {
            System.out.println("job: "+e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
            fail();
        }

        try {
            job.setJobName(etlJobName + "_1");
            job.setEnable(false);
            CreateEtlJobRequest req2 = new CreateEtlJobRequest(TEST_PROJECT, job);
            CreateEtlJobResponse createEtlJobResponse2 = client.createEtlJob(req2);
            assertTrue(true);
        } catch (LogException e) {
            System.out.println("job_1: "+e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
            fail();
        }
    }

    private void testGetEtlJob() {
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        GetEtlJobRequest req = new GetEtlJobRequest(TEST_PROJECT, etlJobName);
        try {
            GetEtlJobResponse resp = client.getEtlJob(req);
            System.out.println(resp.getEtljob().toJsonString(true, true));
            assertTrue(true);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
            fail();
        }
    }

    private void testUpdateEtlJob() {
        EtlSourceConfig sourceConfig = new EtlSourceConfig(logstore);
        EtlTriggerConfig triggerConfig = new EtlTriggerConfig(roleArn, 5, 1);
        EtlFunctionFcConfig fcConfig = new EtlFunctionFcConfig(Consts.FUNCTION_PROVIDER_FC, credentials.getEndpoint(), fcAccountId, fcRegion, fcService, fcFunction);
        EtlLogConfig logConfig = new EtlLogConfig(credentials.getEndpoint(), TEST_PROJECT, logLogstore);
        String functionParameter = "{\"source\":{\"endpoint\":\"http://cn-hangzhou-intranet.log.aliyuncs.com\"}, " +
                "\"target\":{\"endpoint\":\"http://cn-hangzhou-intranet.log.aliyuncs.com\", \"projectName\":\"etl-test\", \"logstoreName\":\"stg-etl-log\"}}";
        EtlJob job = new EtlJob(etlJobName, sourceConfig, triggerConfig, fcConfig, functionParameter, logConfig, true);
        try {
            UpdateEtlJobRequest req = new UpdateEtlJobRequest(TEST_PROJECT, job);
            UpdateEtlJobResponse resp = client.updateEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            assertTrue(true);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            fail();
        }
        try {
            triggerConfig.setStartFromUnixtime(10000);
            UpdateEtlJobRequest req = new UpdateEtlJobRequest(TEST_PROJECT, job);
            UpdateEtlJobResponse resp = client.updateEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            assertTrue(false);
        } catch (LogException e) {
            System.out.print(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
            triggerConfig.setStartFromLastest();
        }
        try {
            logConfig.setLogstoreName(logstore);
            UpdateEtlJobRequest req = new UpdateEtlJobRequest(TEST_PROJECT, job);
            UpdateEtlJobResponse resp = client.updateEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            fail();
        } catch (LogException e) {
            System.out.print(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
    }

    private void testListEtlJob() {
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(TEST_PROJECT, 0, 1);
            ListEtlJobResponse resp = client.listEtlJob(req);
            assertEquals(resp.getCount(), 1);
            assertEquals(resp.getTotal(), 2);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            assertTrue(false);
        }
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(TEST_PROJECT, 0, 10);
            ListEtlJobResponse resp = client.listEtlJob(req);
            assertEquals(resp.getCount(), 2);
            assertEquals(resp.getTotal(), 2);
            int hit = 0;
            for (String jobName : resp.getEtlJobNameList()) {
                if (jobName.equalsIgnoreCase(etlJobName)) {
                    GetEtlJobResponse getresp = client.getEtlJob(new GetEtlJobRequest(TEST_PROJECT, etlJobName));
                    assertEquals(getresp.getEtljob().getJobName(), etlJobName);
                    assertTrue(getresp.getEtljob().getEnable());
                    ++hit;
                } else if (jobName.equalsIgnoreCase(etlJobName + "_1")) {
                    GetEtlJobResponse getresp = client.getEtlJob(new GetEtlJobRequest(TEST_PROJECT, etlJobName + "_1"));
                    assertEquals(getresp.getEtljob().getJobName(), etlJobName + "_1");
                    assertTrue(!getresp.getEtljob().getEnable());
                    ++hit;
                }
            }
            assertEquals(hit, 2);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            fail();
        }
    }

    private void testDeleteEtlJob() {
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(TEST_PROJECT, etlJobName);
            DeleteEtlJobResponse resp = client.deleteEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            assertTrue(true);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            fail();
        }
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(TEST_PROJECT, etlJobName + "_1");
            DeleteEtlJobResponse resp = client.deleteEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            assertTrue(true);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            fail();
        }
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(TEST_PROJECT, 0, 10);
            ListEtlJobResponse resp = client.listEtlJob(req);
            assertEquals(resp.getCount(), 0);
            assertEquals(resp.getTotal(), 0);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            fail();
        }
    }

}
