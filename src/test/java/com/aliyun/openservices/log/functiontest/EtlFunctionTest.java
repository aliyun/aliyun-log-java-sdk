package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.*;
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
    private static String logstore = "from";
    private static String roleArn = "";
    private static Client logClient = null;
    private static String fcEndpoint = "";
    private static String fcRegion = "";
    private static String fcAccountId = "";
    private static String fcService = "java-fc-demo";
    private static String fcFunction = "test";
    private static String etlJobName = "";

    @BeforeClass
    public static void setup() {
        logClient = new Client(endpoint, accessKeyId, accessKeySecret);
    }

    @AfterClass
    public static void cleanup() {

    }

    @Test
    public void testCreateEtlJob() {
        EtlSourceConfig sourceConfig = new EtlSourceConfig("xxxxxfrom");
        EtlTriggerConfig triggerConfig = new EtlTriggerConfig(roleArn, 300, 1);
        EtlFunctionFcConfig fcConfig = new EtlFunctionFcConfig(Consts.FUNCTION_PROVIDER_FC, fcEndpoint, fcAccountId, fcRegion, fcService, fcFunction);
        EtlLogConfig logConfig = new EtlLogConfig(this.endpoint, this.project, "etl-log");
        String functionParameter = "{\"source\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\"}, \"target\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\", \"projectName\":\"etl-test\", \"logstoreName\":\"etl-1\"}}";
        EtlJob job = new EtlJob(this.etlJobName, sourceConfig, triggerConfig, fcConfig, functionParameter, logConfig, true);
        try {
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
        }
        try {
            sourceConfig.setLogstoreName(logstore);
            triggerConfig.setMaxRetryTime(-1);
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
        }
        try {
            triggerConfig.setMaxRetryTime(1);
            triggerConfig.setTriggerInterval(0);
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
        }
        try {
            triggerConfig.setTriggerInterval(10);
            triggerConfig.setRoleArn("x");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            Assert.assertEquals(e.GetErrorCode(), "Unauthorized");
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
        }
        try {
            triggerConfig.setRoleArn(roleArn);
            fcConfig.setAccountId("");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
        }
        try {
            fcConfig.setAccountId(fcAccountId);
            logConfig.setLogstoreName("");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
        }
        try {
            logConfig.setLogstoreName("etl-log");
            job.setFunctionParameter("xxxxx");
            CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
            CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
            Assert.assertTrue(false);
        } catch (LogException e) {
            Assert.assertEquals(e.GetErrorCode(), "PostBodyInvalid");
            System.out.println(e.GetErrorCode());
            System.out.println(e.GetErrorMessage());
        }
        try {
            job.setFunctionParameter(functionParameter);
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
    public void testUpdateEtlJob() {
        EtlTriggerConfig triggerConfig = new EtlTriggerConfig(roleArn, 600, 5);
        EtlFunctionFcConfig fcConfig = new EtlFunctionFcConfig(Consts.FUNCTION_PROVIDER_FC, fcEndpoint, fcAccountId, fcRegion, fcService, fcFunction);
        EtlLogConfig logConfig = new EtlLogConfig(this.endpoint, this.project, "etl-log");
        String functionParameter = "{}";
        EtlJob job = new EtlJob(this.etlJobName, triggerConfig, fcConfig, functionParameter, logConfig, true);
        UpdateEtlJobRequest req = new UpdateEtlJobRequest(this.project, job);
        try {
            UpdateEtlJobResponse resp = this.logClient.updateEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            Assert.assertTrue(true);
        } catch (LogException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
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
            Assert.assertTrue(false);
            e.printStackTrace();
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
            Assert.assertTrue(false);
            e.printStackTrace();
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
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        try {
            DeleteEtlJobRequest req = new DeleteEtlJobRequest(this.project, this.etlJobName + "_1");
            DeleteEtlJobResponse resp = this.logClient.deleteEtlJob(req);
            System.out.println(resp.GetAllHeaders());
            Assert.assertTrue(true);
        } catch (LogException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        try {
            ListEtlJobRequest req = new ListEtlJobRequest(this.project, 0, 10);
            ListEtlJobResponse resp = this.logClient.listEtlJob(req);
            Assert.assertEquals(resp.getCount(), 0);
            Assert.assertEquals(resp.getTotal(), 0);
        } catch (LogException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

}
