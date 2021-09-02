package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.EtlMeta;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ListEtlMetaResponse;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EtlMetaTest extends JobIntgTest {
    private static final String etlMetaName_1 = "java_sdk_test_1";
    private final String userAliuid = credentials.getAliuid();

    private void cleanUp() throws LogException {
        ListEtlMetaResponse listEtlMetaResponse = client.listEtlMeta(TEST_PROJECT, etlMetaName_1, 0, 200);
        for (EtlMeta meta : listEtlMetaResponse.getEtlMetaList()) {
            client.deleteEtlMeta(TEST_PROJECT, meta.getMetaName(), meta.getMetaKey());
        }
    }

    @Test
    public void testCrud() throws LogException {
        cleanUp();
        String etlMetaKeyPrefxi_1 = "etlmeta-prefix_1\"'";
        String metaKey = etlMetaKeyPrefxi_1 + "_1";
        EtlMeta meta = new EtlMeta(etlMetaName_1, metaKey, etlMetaKeyPrefxi_1);
        JSONObject metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", userAliuid);
        String userRegion = "cn-hangzhou";
        metaValueObj.put("region", userRegion);
        String userProject = "flowlog-test";
        metaValueObj.put("project", userProject);
        metaValueObj.put("logstore", "slb-log");
        String roleArn = "acs:ram::" + userAliuid + ":role/aliyunlogwriteonlyrole";
        metaValueObj.put("roleArn", roleArn);
        meta.setMetaValue(metaValueObj);
        client.createEtlMeta(TEST_PROJECT, meta);

        ListEtlMetaResponse listEtlMetaResponse = client.getEtlMeta(TEST_PROJECT, etlMetaName_1, metaKey);
        assertEquals(1, listEtlMetaResponse.getCount());
        assertEquals(1, listEtlMetaResponse.getTotal());
        List<EtlMeta> etlMetaList = listEtlMetaResponse.getEtlMetaList();
        for (EtlMeta etlMeta : etlMetaList) {
            assertEquals(etlMeta.getMetaKey(), metaKey);
            assertEquals(etlMeta.getMetaName(), etlMetaName_1);
            assertTrue(etlMeta.isEnable());
            assertEquals(etlMeta.getMetaTag(), etlMetaKeyPrefxi_1);
            JSONObject mv = etlMeta.getMetaValue();
            assertEquals(userAliuid, mv.getString("aliuid"));
            assertEquals(userRegion, mv.getString("region"));
            assertEquals(userProject, mv.getString("project"));
            assertEquals("slb-log", mv.getString("logstore"));
            assertEquals(roleArn, mv.getString("roleArn"));
        }

        metaValueObj.put("logstore", "test-log");
        meta.setMetaValue(metaValueObj);
        client.updateEtlMeta(TEST_PROJECT, meta);

        ListEtlMetaResponse listEtlMetaResponse2 = client.getEtlMeta(TEST_PROJECT, etlMetaName_1, metaKey);
        assertEquals(1, listEtlMetaResponse2.getCount());
        assertEquals(1, listEtlMetaResponse2.getTotal());
        List<EtlMeta> etlMetaList2 = listEtlMetaResponse2.getEtlMetaList();
        for (EtlMeta etlMeta : etlMetaList2) {
            assertEquals(etlMeta.getMetaKey(), metaKey);
            assertEquals(etlMeta.getMetaName(), etlMetaName_1);
            assertTrue(etlMeta.isEnable());
            assertEquals(etlMeta.getMetaTag(), etlMetaKeyPrefxi_1);
            JSONObject mv = etlMeta.getMetaValue();
            assertEquals(userAliuid, mv.getString("aliuid"));
            assertEquals(userRegion, mv.getString("region"));
            assertEquals(userProject, mv.getString("project"));
            assertEquals("test-log", mv.getString("logstore"));
            assertEquals(roleArn, mv.getString("roleArn"));
        }

        ListEtlMetaResponse listEtlMetaResponse3 = client.listEtlMeta(TEST_PROJECT, etlMetaName_1, 0, 200);
        assertEquals(1, listEtlMetaResponse3.getCount());
        assertEquals(1, listEtlMetaResponse2.getTotal());

        client.deleteEtlMeta(TEST_PROJECT, etlMetaName_1, metaKey);

        ListEtlMetaResponse response = client.getEtlMeta(TEST_PROJECT, etlMetaName_1, metaKey);
        assertEquals(0, response.getCount());
        assertEquals(0, response.getTotal());
    }

}
