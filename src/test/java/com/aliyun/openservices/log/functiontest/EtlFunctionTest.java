package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.*;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

public class EtlFunctionTest {

    private static String accessKeyId = "";
    private static String accessKeySecret = "";
    private static String endpoint = "http://cn-hangzhou-staging-intranet.sls.aliyuncs.com";
    private static String project = "ali-slstest-trigger";
    private static String roleArn = "";
    private static Client logClient = null;
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
        logClient = new Client(endpoint, accessKeyId, accessKeySecret);
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(project, etlJobName);
            DeleteEtlJobResponse resp = logClient.deleteEtlJob(req);
        } catch (LogException e) {
        }
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(project, etlJobName + "_1");
            DeleteEtlJobResponse resp = logClient.deleteEtlJob(req);
        } catch (LogException e) {
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @AfterClass
    public static void cleanup() {

    }

    @Test
    public void testCreateEtlJob() {

        EtlSourceConfig sourceConfig = new EtlSourceConfig(logstore);
        EtlTriggerConfig triggerConfig = new EtlTriggerConfig(roleArn, 300, 1);
        EtlFunctionFcConfig fcConfig = new EtlFunctionFcConfig(Consts.FUNCTION_PROVIDER_FC, fcEndpoint, fcAccountId, fcRegion, fcService, fcFunction);
        EtlLogConfig logConfig = new EtlLogConfig(this.endpoint, this.project, this.logLogstore);
        String functionParameter = "{\"source\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\"}, \"target\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\", \"projectName\":\"etl-test\", \"logstoreName\":\"etl-1\"}}";
        EtlJob job = new EtlJob(this.etlJobName, sourceConfig, triggerConfig, fcConfig, functionParameter, logConfig, true);
        try {
            sourceConfig.setLogstoreName("x");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            sourceConfig.setLogstoreName(logstore);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            triggerConfig.setMaxRetryTime(1000);
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            triggerConfig.setMaxRetryTime(1);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            triggerConfig.setTriggerInterval(-1);
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            triggerConfig.setTriggerInterval(100);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
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
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            triggerConfig.setRoleArn(roleArn);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            fcConfig.setFunctionProvider("StreamCompute");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            fcConfig.setFunctionProvider(Consts.FUNCTION_PROVIDER_FC);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            fcConfig.setAccountId("");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            fcConfig.setAccountId(fcAccountId);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            logConfig.setLogstoreName("");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            logConfig.setLogstoreName(logLogstore);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            logConfig.setLogstoreName(logstore);
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            logConfig.setLogstoreName(logLogstore);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            logConfig.setProjectName("");
            logConfig.setLogstoreName(logstore);
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            logConfig.setLogstoreName(logLogstore);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            job.setFunctionParameter("xxxxx");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            job.setFunctionParameter(functionParameter);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            job.setJobName("1");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            job.setJobName(etlJobName);
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
        try {
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(true);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
            Assert.assertTrue(false);
        }
        try {
            job.setJobName(etlJobName + "_1");
            job.setEnable(false);
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(true);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGetEtlJob() {
        GetEtlJobRequest req = new GetEtlJobRequest(project, etlJobName);
        try {
            GetEtlJobResponse resp = logClient.getEtlJob(req);
            System.out.println(resp.getEtljob().toJsonString(true, true));
            Assert.assertTrue(true);
        } catch (LogException e) {
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testUpdateEtlJob() {
        EtlSourceConfig sourceConfig = new EtlSourceConfig(logstore);
        EtlTriggerConfig triggerConfig = new EtlTriggerConfig(roleArn, 5, 1);
        EtlFunctionFcConfig fcConfig = new EtlFunctionFcConfig(Consts.FUNCTION_PROVIDER_FC, fcEndpoint, fcAccountId, fcRegion, fcService, fcFunction);
        EtlLogConfig logConfig = new EtlLogConfig(this.endpoint, this.project, this.logLogstore);
        String functionParameter = "{\"source\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\"}, " +
                "\"target\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\", \"projectName\":\"etl-test\", \"logstoreName\":\"stg-etl-log\"}}";
        EtlJob job = new EtlJob(this.etlJobName, sourceConfig, triggerConfig, fcConfig, functionParameter, logConfig, true);
        try {
            UpdateEtlJobRequest req = new UpdateEtlJobRequest(this.project, job);
            UpdateEtlJobResponse resp = this.logClient.updateEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            Assert.assertTrue(true);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            Assert.assertTrue(false);
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
            logConfig.setLogstoreName(this.logstore);
            UpdateEtlJobRequest req = new UpdateEtlJobRequest(this.project, job);
            UpdateEtlJobResponse resp = this.logClient.updateEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            Assert.assertTrue(false);
        } catch (LogException e) {
            System.out.print(e.GetErrorMessage());
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testListEtlJob() {
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(this.project, 0, 1);
            ListEtlJobResponse resp = this.logClient.listEtlJob(req);
            Assert.assertEquals(resp.getCount(), 1);
            Assert.assertEquals(resp.getTotal(), 2);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            Assert.assertTrue(false);
        }
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(this.project, 0, 10);
            ListEtlJobResponse resp = this.logClient.listEtlJob(req);
            Assert.assertEquals(resp.getCount(), 2);
            Assert.assertEquals(resp.getTotal(), 2);
            int hit = 0;
            for (String jobName : resp.getEtlJobNameList()) {
                if (jobName.equalsIgnoreCase(etlJobName)) {
                    GetEtlJobResponse getresp = this.logClient.getEtlJob(new GetEtlJobRequest(project, etlJobName));
                    Assert.assertEquals(getresp.getEtljob().getJobName(), etlJobName);
                    Assert.assertTrue(getresp.getEtljob().getEnable());
                    ++hit;
                } else if (jobName.equalsIgnoreCase(etlJobName + "_1")) {
                    GetEtlJobResponse getresp = this.logClient.getEtlJob(new GetEtlJobRequest(project, etlJobName + "_1"));
                    Assert.assertEquals(getresp.getEtljob().getJobName(), etlJobName + "_1");
                    Assert.assertTrue(!getresp.getEtljob().getEnable());
                    ++hit;
                }
            }
            Assert.assertEquals(hit, 2);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testDeleteEtlJob() {
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(this.project, this.etlJobName);
            DeleteEtlJobResponse resp = this.logClient.deleteEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            Assert.assertTrue(true);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            Assert.assertTrue(false);
        }
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(this.project, this.etlJobName + "_1");
            DeleteEtlJobResponse resp = this.logClient.deleteEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            Assert.assertTrue(true);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            Assert.assertTrue(false);
        }
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(this.project, 0, 10);
            ListEtlJobResponse resp = this.logClient.listEtlJob(req);
            Assert.assertEquals(resp.getCount(), 0);
            Assert.assertEquals(resp.getTotal(), 0);
        } catch (LogException e) {
            System.out.print(e.GetErrorCode());
            System.out.print(e.GetErrorMessage());
            Assert.assertTrue(false);
        }
    }

}
