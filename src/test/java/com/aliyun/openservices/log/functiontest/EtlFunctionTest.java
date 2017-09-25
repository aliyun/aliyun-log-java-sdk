package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

public class EtlFunctionTest {

   private static String accessKeyId = "";
   private static String accessKeySecret = "";
   private static String endpoint = "";
   private static String project = "";
   private static String roleArn = "";
   private static Client logClient = null;
   private static String fcEndpoint = "";
   private static String fcRegion = "";
   private static String fcAccountId = "";
   private static String fcService = "";
   private static String fcFunction = "";
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
      EtlSourceConfig sourceConfig = new EtlSourceConfig("from");
      EtlTriggerConfig triggerConfig = new EtlTriggerConfig(roleArn, 300, 1);
      EtlFunctionFcConfig fcConfig = new EtlFunctionFcConfig(Consts.FUNCTION_PROVIDER_FC, fcEndpoint, fcAccountId, fcRegion, fcService, fcFunction);
      EtlLogConfig logConfig = new EtlLogConfig(this.endpoint, this.project, "etl-log");
      String functionParameter = "{\"source\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\"}, \"target\":{\"endpoint\":\"http://cn-shanghai-intranet.log.aliyuncs.com\", \"projectName\":\"etl-test\", \"logstoreName\":\"etl-1\"}}";
      EtlJob job = new EtlJob(this.etlJobName, sourceConfig, triggerConfig, fcConfig, functionParameter, logConfig, true);
      CreateEtlJobRequest req = new CreateEtlJobRequest(project, job);
      try {
         CreateEtlJobResponse resp = this.logClient.createEtlJob(req);
         System.out.println(resp.GetAllHeaders());
      } catch (LogException e) {
         e.printStackTrace();
         System.out.println(e.GetErrorCode());
         System.out.println(e.GetErrorMessage());
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
      } catch (LogException e) {
         e.printStackTrace();
      }

   }

   @Test
   public void testListEtlJob() {
      ListEtlJobRequest req = new ListEtlJobRequest(this.project, 0, 10);
      try {
         ListEtlJobResponse resp = this.logClient.listEtlJob(req);
         System.out.println(resp.GetAllHeaders());
         System.out.println("total: " + resp.getTotal() + ", count: " + resp.getCount());
         System.out.println(resp.getEtlJobNameList());
      } catch (LogException e) {
         e.printStackTrace();
      }
   }

   @Test
   public void testGetEtlJob() {
      GetEtlJobRequest req = new GetEtlJobRequest(this.project,  this.etlJobName);
      try {
         GetEtlJobResponse resp = this.logClient.getEtlJob(req);
         System.out.println(resp.GetAllHeaders());
         System.out.println(resp.getEtljob().toJsonString(true));
      } catch (LogException e) {
         e.printStackTrace();
      }
   }

   @Test
   public void testDeleteEtlJob() {
      DeleteEtlJobRequest req = new DeleteEtlJobRequest(this.project, this.etlJobName);
      try {
         DeleteEtlJobResponse resp = this.logClient.deleteEtlJob(req);
         System.out.println(resp.GetAllHeaders());
      } catch (LogException e) {
         e.printStackTrace();
      }
   }

   @Test
   public void testListEtlTask() {

      System.out.println("status: all");
      testListEtlTaskWithStatus("");
      System.out.println("status: " + Consts.ETL_TASK_STATUS_READY);
      testListEtlTaskWithStatus(Consts.ETL_TASK_STATUS_READY);
      System.out.println("status: " + Consts.ETL_TASK_STATUS_RUNNING);
      testListEtlTaskWithStatus(Consts.ETL_TASK_STATUS_RUNNING);
      System.out.println("status: " + Consts.ETL_TASK_STATUS_SUCCESS);
      testListEtlTaskWithStatus(Consts.ETL_TASK_STATUS_SUCCESS);
      System.out.println("status: " + Consts.ETL_TASK_STATUS_FAILED);
      testListEtlTaskWithStatus(Consts.ETL_TASK_STATUS_FAILED);
   }

   public void testListEtlTaskWithStatus(String status) {
      ListEtlTaskRequest req = new ListEtlTaskRequest(this.project, this.etlJobName, System.currentTimeMillis()/1000 - 43200, System.currentTimeMillis()/1000, 0, 10, status);
      ListEtlTaskResponse resp = null;
      try {
         resp = this.logClient.listEtlTask(req);
         System.out.println(resp.getTotal());
         System.out.println(resp.getCount());
         ArrayList<String> taskIdList = new ArrayList<String>();
         for (EtlTask task : resp.getEtlTaskList()) {
            taskIdList.add(task.getTaskId());
            System.out.println(task.toJsonString());
         }
         if (status.equals(Consts.ETL_TASK_STATUS_SUCCESS)) {
            UpdateEtlTaskStatusRequest upReq = new UpdateEtlTaskStatusRequest(this.project, this.etlJobName, true);
            upReq.setEtlTaskIdList(taskIdList);
            UpdateEtlTaskStatusResponse upResp = this.logClient.updateEtlTaskStatus(upReq);
            System.out.println("updateEtlTaskStatus true: " + upResp.GetAllHeaders());
         } else if (status.equals(Consts.ETL_TASK_STATUS_FAILED)) {
            UpdateEtlTaskStatusRequest upReq = new UpdateEtlTaskStatusRequest(this.project, this.etlJobName, false);
            upReq.setEtlTaskIdList(taskIdList);
            UpdateEtlTaskStatusResponse upResp = this.logClient.updateEtlTaskStatus(upReq);
            System.out.println("updateEtlTaskStatus false: " + upResp.GetAllHeaders());
         }
      } catch (LogException e) {
         e.printStackTrace();
      }
   }
}
