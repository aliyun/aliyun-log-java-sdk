package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.EtlFunctionFcConfig;
import com.aliyun.openservices.log.common.EtlJob;
import com.aliyun.openservices.log.common.EtlLogConfig;
import com.aliyun.openservices.log.common.EtlSourceConfig;
import com.aliyun.openservices.log.common.EtlTriggerConfig;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateEtlJobRequest;
import com.aliyun.openservices.log.request.DeleteEtlJobRequest;
import com.aliyun.openservices.log.request.GetEtlJobRequest;
import com.aliyun.openservices.log.request.ListEtlJobRequest;
import com.aliyun.openservices.log.request.UpdateEtlJobRequest;
import com.aliyun.openservices.log.response.DeleteEtlJobResponse;
import com.aliyun.openservices.log.response.GetEtlJobResponse;
import com.aliyun.openservices.log.response.ListEtlJobResponse;
import com.aliyun.openservices.log.response.UpdateEtlJobResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EtlFunctionTest extends FunctionTest {

    private static String project = "ali-slstest-trigger";
    private static String roleArn = "";
    private static String fcEndpoint = "http://fc.cn-shanghai.aliyuncs.com";
    private static String fcRegion = "cn-shanghai";
    private static String fcAccountId = "";
    private static String fcService = "log_etl";
    private static String fcFunction = "logstore-replication";
    private static String etlJobName = "v2-test";
    private static String logstore = "from";
    private static String logLogstore = "etl-log";

    @BeforeClass
    public static void setup() {
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(project, etlJobName);
            client.deleteEtlJob(req);
        } catch (LogException e) {
        }
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(project, etlJobName + "_1");
            client.deleteEtlJob(req);
        } catch (LogException e) {
        }
        waitForSeconds(1);
    }


    @AfterClass
    public static void cleanup() {

    }

    @Test
    public void testCreateEtlJob() {

        EtlSourceConfig sourceConfig = new EtlSourceConfig(logstore);
        EtlTriggerConfig triggerConfig = new EtlTriggerConfig(roleArn, 300, 1);
        EtlFunctionFcConfig fcConfig = new EtlFunctionFcConfig(Consts.FUNCTION_PROVIDER_FC, fcEndpoint, fcAccountId, fcRegion, fcService, fcFunction);
        EtlLogConfig logConfig = new EtlLogConfig(credentials.getEndpoint(), project, logLogstore);
        String functionParameter = "{\"source\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\"}, \"target\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\", \"projectName\":\"etl-test\", \"logstoreName\":\"etl-1\"}}";
        EtlJob job = new EtlJob(etlJobName, sourceConfig, triggerConfig, fcConfig, functionParameter, logConfig, true);
        try {
            sourceConfig.setLogstoreName("x");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
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
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
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
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            triggerConfig.setTriggerInterval(100);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        /*
        try {
            triggerConfig.setRoleArn(roleArn + "x");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            triggerConfig.setRoleArn(roleArn);
            Assert.assertEquals(e.GetErrorCode(), "Unauthorized");
            Assert.assertTrue(true);
        }
        */
        try {
            triggerConfig.setRoleArn(" ");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
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
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
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
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            fcConfig.setAccountId(fcAccountId);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            logConfig.setLogstoreName("");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            logConfig.setLogstoreName(logLogstore);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            logConfig.setLogstoreName(logstore);
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
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
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
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
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
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
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            client.createEtlJob(req);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            job.setJobName(etlJobName);
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
        try {
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            client.createEtlJob(req);
            assertTrue(true);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
            fail();
        }
        try {
            job.setJobName(etlJobName + "_1");
            job.setEnable(false);
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            client.createEtlJob(req);
            assertTrue(true);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
            fail();
        }
    }

    @Test
    public void testGetEtlJob() {
        GetEtlJobRequest req = new GetEtlJobRequest(project, etlJobName);
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

    @Test
    public void testUpdateEtlJob() {
        EtlSourceConfig sourceConfig = new EtlSourceConfig(logstore);
        EtlTriggerConfig triggerConfig = new EtlTriggerConfig(roleArn, 5, 1);
        EtlFunctionFcConfig fcConfig = new EtlFunctionFcConfig(Consts.FUNCTION_PROVIDER_FC, fcEndpoint, fcAccountId, fcRegion, fcService, fcFunction);
        EtlLogConfig logConfig = new EtlLogConfig(credentials.getEndpoint(), project, logLogstore);
        String functionParameter = "{\"source\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\"}, " +
                "\"target\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\", \"projectName\":\"etl-test\", \"logstoreName\":\"stg-etl-log\"}}";
        EtlJob job = new EtlJob(etlJobName, sourceConfig, triggerConfig, fcConfig, functionParameter, logConfig, true);
        try {
            UpdateEtlJobRequest req = new UpdateEtlJobRequest(project, job);
            UpdateEtlJobResponse resp = client.updateEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            assertTrue(true);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            fail();
        }
        /*
        try {
            triggerConfig.setRoleArn("xx");
            UpdateEtlJobRequest req = new UpdateEtlJobRequest(this.project, job);
            UpdateEtlJobResponse resp = this.logClient.updateEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.print(e.GetErrorMessage());
            Assert.assertEquals(e.GetErrorCode(), "Unauthorized");
            Assert.assertTrue(true);
            triggerConfig.setRoleArn(this.roleArn);
        }
        */
        try {
            logConfig.setLogstoreName(logstore);
            UpdateEtlJobRequest req = new UpdateEtlJobRequest(project, job);
            UpdateEtlJobResponse resp = client.updateEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            fail();
        } catch (LogException e) {
            System.out.print(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            assertTrue(true);
        }
    }

    @Test
    public void testListEtlJob() {
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(project, 0, 1);
            ListEtlJobResponse resp = client.listEtlJob(req);
            assertEquals(resp.getCount(), 1);
            assertEquals(resp.getTotal(), 2);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            assertTrue(false);
        }
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(project, 0, 10);
            ListEtlJobResponse resp = client.listEtlJob(req);
            assertEquals(resp.getCount(), 2);
            assertEquals(resp.getTotal(), 2);
            int hit = 0;
            for (String jobName : resp.getEtlJobNameList()) {
                if (jobName.equalsIgnoreCase(etlJobName)) {
                    GetEtlJobResponse getresp = client.getEtlJob(new GetEtlJobRequest(project, etlJobName));
                    assertEquals(getresp.getEtljob().getJobName(), etlJobName);
                    assertTrue(getresp.getEtljob().getEnable());
                    ++hit;
                } else if (jobName.equalsIgnoreCase(etlJobName + "_1")) {
                    GetEtlJobResponse getresp = client.getEtlJob(new GetEtlJobRequest(project, etlJobName + "_1"));
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

    @Test
    public void testDeleteEtlJob() {
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(project, etlJobName);
            DeleteEtlJobResponse resp = client.deleteEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            assertTrue(true);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            fail();
        }
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(project, etlJobName + "_1");
            DeleteEtlJobResponse resp = client.deleteEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            assertTrue(true);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            fail();
        }
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(project, 0, 10);
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
