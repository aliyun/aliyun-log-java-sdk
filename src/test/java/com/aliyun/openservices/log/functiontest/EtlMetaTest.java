package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.EtlMeta;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ListEtlMetaResponse;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EtlMetaTest extends JobIntgTest{
    private static String etlMetaName_1 = "java_sdk_test_1";
    private static String etlMetaKeyPrefxi_1 = "etlmeta-prefix_1\"'";
    private String userAliuid = credentials.getAliuid();
    private String userRegion = "cn-hangzhou";
    private String userProject = "flowlog-test";

    private void cleanUp(){
        try {
            waitForSeconds(5);
            ArrayList<EtlMeta> toDeleted = new ArrayList<EtlMeta>();
            ListEtlMetaResponse listEtlMetaResponse = client.listEtlMeta(TEST_PROJECT, etlMetaName_1, 0, 200);
            for (EtlMeta meta : listEtlMetaResponse.getEtlMetaList()) {
                toDeleted.add(meta);
            }
            for (EtlMeta meta : toDeleted) {
                client.deleteEtlMeta(TEST_PROJECT, meta.getMetaName(), meta.getMetaKey());
            }
        }catch (LogException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testCrud(){
        cleanUp();
        String metaKey = etlMetaKeyPrefxi_1+"_1" ;
        EtlMeta meta = new EtlMeta(etlMetaName_1, metaKey, etlMetaKeyPrefxi_1);
        JSONObject metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", userAliuid);
        metaValueObj.put("region", userRegion);
        metaValueObj.put("project", userProject);
        metaValueObj.put("logstore", "slb-log");
        metaValueObj.put("roleArn", "acs:ram::"+userAliuid+":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            client.createEtlMeta(TEST_PROJECT, meta);
        } catch (LogException e) {
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            fail();
        }

        try{
            ListEtlMetaResponse listEtlMetaResponse = client.getEtlMeta(TEST_PROJECT,etlMetaName_1,metaKey);
            assertEquals(1,listEtlMetaResponse.getCount());
            assertEquals(1,listEtlMetaResponse.getTotal());
        }catch (LogException e){
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            fail();
        }

        try{
            metaValueObj.put("logstore", "test-log");
            meta.setMetaValue(metaValueObj);
            client.updateEtlMeta(TEST_PROJECT,meta);
        }catch (LogException e){
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            fail();
        }

        try{
            ListEtlMetaResponse listEtlMetaResponse = client.getEtlMeta(TEST_PROJECT,etlMetaName_1,metaKey);
            assertEquals("test-log",listEtlMetaResponse.getEtlMetaList().get(0).getMetaValue().get("logstore"));
        }catch (LogException e){
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            fail();
        }

        try {
            ListEtlMetaResponse listEtlMetaResponse = client.listEtlMeta(TEST_PROJECT,etlMetaName_1,0,200);
            assertEquals(1,listEtlMetaResponse.getCount());
        }catch (LogException e){
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            fail();
        }

        try{
            client.deleteEtlMeta(TEST_PROJECT,etlMetaName_1,metaKey);
        }catch (LogException e){
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            fail();
        }

        try {
            client.getEtlMeta(TEST_PROJECT,etlMetaName_1,metaKey);
        }catch (LogException e){
            assertEquals("EtlMetaNotExist",e.GetErrorCode());
        }
    }

}
