package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.AuditJob;
import com.aliyun.openservices.log.common.AuditJobConfiguration;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.*;
import com.alibaba.fastjson.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

public class AuditJobFunctionTest extends FunctionTest {

    String project = "project-to-test-alert";
    String jobName = "test_audit_job";

    @Ignore
    @Test
    public void testCreate() throws Exception {
        AuditJob auditJob = new AuditJob();
        auditJob.setName(jobName);
        auditJob.setDisplayName("test audit job");
        auditJob.setDescription("...");
        AuditJobConfiguration configuration = new AuditJobConfiguration("{\"test\":\"abc\"}");
        auditJob.setConfiguration(configuration);
        client.createAuditJob(new CreateAuditJobRequest(project, auditJob));
    }

    @Ignore
    @Test
    public void testRead() throws Exception {
        ListAuditJobResponse listResp = client.listAuditJob(new ListAuditJobRequest(project));
        for (AuditJob aj : listResp.getResults()) {
            System.out.println("list audit_job, name: " + aj.getName() + "status: " +  aj.getStatus());
        }

        GetAuditJobResponse getResp = client.getAuditJob(new GetAuditJobRequest(project, jobName));
        AuditJob aj = getResp.getAuditJob();
        System.out.println("get audit_job, name: " + aj.getName() + ", status: " +  aj.getStatus());

        ((AuditJobConfiguration)aj.getConfiguration()).setDetail("{\"code\":\"200\",\"data\":{\"requestData\":\"build_attributes\"}}");
        UpdateAuditJobResponse upResp = client.updateAuditJob(new UpdateAuditJobRequest(project, aj));
    }
    @Ignore
    @Test
    public void testStop() throws Exception {
        StopAuditJobResponse response = client.stopAuditJob(new StopAuditJobRequest(project, jobName));
        System.out.println(response.GetAllHeaders());
    }
    @Ignore
    @Test
    public void testStart() throws Exception {
        StartAuditJobResponse response = client.startAuditJob(new StartAuditJobRequest(project, jobName));
        System.out.println(response.GetAllHeaders());
    }

    @Ignore
    @Test
    public void testDelete() throws Exception {
        DeleteAuditJobResponse response = client.deleteAuditJob(new DeleteAuditJobRequest(project, jobName));
        System.out.println(response.GetAllHeaders());
    }
}
