package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.EtlMeta;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ListEtlMetaNameResponse;
import com.aliyun.openservices.log.response.ListEtlMetaResponse;
import com.aliyun.openservices.log.response.UpdateEtlMetaResponse;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class EtlMetaFunctionTest extends FunctionTest {
    private static String project = "26";
    private static String etlMetaName_1 = "test-meta-1";
    private static String etlMetaName_2 = "test-meta-2";
    private static String etlMetaKeyPrefxi_1 = UUID.randomUUID().toString() + "\"'";
    private static String etlMetaKeyPrefxi_2 = UUID.randomUUID().toString();
    private static int etlMetaCount_1 = 205;
    private static int etlMetaCount_2 = 5;
    private String userAliuid = "1654218965343050";
    private String userRegion = "cn-chengdu";
    private String userProject = "flowlog-test";

    @Test
    public void testCreateEtlMeta() {

        System.out.println("testCreateEtlMeta");
        for (int i = 0; i < etlMetaCount_1; ++i) {
            String metaKey = etlMetaKeyPrefxi_1 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(etlMetaName_1, metaKey, etlMetaKeyPrefxi_1);
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "slb-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            try {
                client.createEtlMeta(project, meta);
            } catch (LogException e) {
                System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
                fail();
            }
        }
        for (int i = 0; i < etlMetaCount_2; ++i) {
            String metaKey = etlMetaKeyPrefxi_2 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(etlMetaName_2, metaKey, "");
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "apigateway-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            try {
                client.createEtlMeta(project, meta);
            } catch (LogException e) {
                System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
                fail();
            }
            if (i % 10 == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testListEtlMetaReverse() {
        long curSecond = System.currentTimeMillis() / 1000;
        int offset = 0;
        ArrayList<EtlMeta> slbMetas = new ArrayList<EtlMeta>();
        try {
            while (true) {
                ListEtlMetaResponse metas = client.listEtlMeta(project, etlMetaName_1, userProject, "slb-log", offset, 100);
                offset += 100;
                assertEquals(metas.getTotal(), etlMetaCount_1);
                for (EtlMeta m : metas.getEtlMetaList()) {
                    slbMetas.add(m);
                }
                if (metas.getCount() < 100) {
                    break;
                }
            }
        } catch (LogException e) {
            e.printStackTrace();
        }
        assertEquals(slbMetas.size(), etlMetaCount_1);
        int i = 0;
        for (EtlMeta m : slbMetas) {
            assertEquals(m.getMetaKey(), etlMetaKeyPrefxi_1 + "_" + String.valueOf(i++));
            assertTrue(m.getCreateTime() > curSecond - 300 && m.getCreateTime() < curSecond + 5);
            assertTrue(m.getCreateTime() <= m.getLastModifyTime());
        }

        try {
            ListEtlMetaResponse metas = client.listEtlMeta(project, etlMetaName_2, userProject,"apigateway-log", 0, 100);
            assertEquals(metas.getTotal(), etlMetaCount_2);
            assertEquals(metas.getCount(), etlMetaCount_2);
        } catch (LogException e) {
            e.printStackTrace();
        }

        try {
            ListEtlMetaResponse metas = client.listEtlMeta(project, etlMetaName_1, userProject, "apigateway-log", 0, 100);
            assertEquals(metas.getTotal(), 0);
            assertEquals(metas.getCount(), 0);
        } catch (LogException e) {
            e.printStackTrace();
        }

        try {
            ListEtlMetaResponse metas = client.listEtlMeta(project, etlMetaName_2 + "xxx", userProject, "apigateway-log", 0, 100);
            assertEquals(metas.getTotal(), 0);
            assertEquals(metas.getCount(), 0);
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateEtlMetaFail() {

        System.out.println("testCreateEtlMetaFail");
        String metaKey = etlMetaKeyPrefxi_1 + "_" + String.valueOf(0);
        EtlMeta meta = new EtlMeta(etlMetaName_1, metaKey, etlMetaKeyPrefxi_1);
        JSONObject metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", userAliuid);
        metaValueObj.put("region", userRegion);
        metaValueObj.put("project", userProject);
        metaValueObj.put("logstore", "slb-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            client.createEtlMeta(project, meta);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "EtlMetaAlreadyExist");
        }

        metaKey = etlMetaKeyPrefxi_1 + "_" + String.valueOf(500);
        meta = new EtlMeta(etlMetaName_1, metaKey, Consts.CONST_ETLMETA_ALL_TAG_MATCH);
        metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", userAliuid);
        metaValueObj.put("region", userRegion);
        metaValueObj.put("project", userProject);
        metaValueObj.put("logstore", "slb-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            client.createEtlMeta(project, meta);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
        }

        meta = new EtlMeta(etlMetaName_2, "", etlMetaKeyPrefxi_2);
        metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", userAliuid);
        metaValueObj.put("region", userRegion);
        metaValueObj.put("project", userProject);
        metaValueObj.put("logstore", "apigateway-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            client.createEtlMeta(project, meta);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
        }

        meta = new EtlMeta("x", metaKey, "");
        metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", userAliuid);
        metaValueObj.put("region", userRegion);
        metaValueObj.put("project", userProject);
        metaValueObj.put("logstore", "apigateway-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            client.createEtlMeta(project, meta);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
        }
    }

    @Test
    public void testListEtlMetaName() {
        System.out.println("testListEtlMetaName");
        Set<String> etlNameSet = new HashSet<String>();
        etlNameSet.add(etlMetaName_1);
        etlNameSet.add(etlMetaName_2);
        try {
            ListEtlMetaNameResponse resp = client.listEtlMetaName(project, 0, 200);
            assertEquals(2, resp.getCount());
            assertEquals(2, resp.getTotal());
            for (String etlMetaName : resp.getEtlMetaNameList()) {
                if (etlNameSet.contains(etlMetaName))
                    etlNameSet.remove(etlMetaName);
            }
            assertEquals(0, etlNameSet.size());
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testUpdateEtlMeta() {
        System.out.println("testUpdateEtlMeta");
        String metaKey = etlMetaKeyPrefxi_1 + "_" + String.valueOf(0);
        EtlMeta meta = new EtlMeta(etlMetaName_1, metaKey, "");
        JSONObject metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", userAliuid);
        metaValueObj.put("region", userRegion);
        metaValueObj.put("project", userProject);
        metaValueObj.put("logstore", "slb-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            UpdateEtlMetaResponse resp = client.updateEtlMeta(project, meta);
            Assert.assertTrue(true);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            fail();
        }

        try {
            ListEtlMetaResponse resp = client.getEtlMeta(project, etlMetaName_1, metaKey);
            EtlMeta etlMeta = resp.getHeadEtlMeta();
            assertNotNull(etlMeta);
            assertEquals(resp.getCount(), 1);
            assertEquals(etlMeta.getMetaTag(), "");
            assertEquals(etlMeta.getMetaKey(), metaKey);
            assertEquals(etlMeta.getMetaName(), etlMetaName_1);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            fail();
        }

        meta = new EtlMeta(etlMetaName_2, "notexitkey", etlMetaKeyPrefxi_2);
        metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", userAliuid);
        metaValueObj.put("region", userRegion);
        metaValueObj.put("project", userProject);
        metaValueObj.put("logstore", "apigateway-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            client.updateEtlMeta(project, meta);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "EtlMetaNotExist");
        }
    }

    @Test
    public void testListAndDeleteEtlMeta() {
        System.out.println("testListAndDeleteEtlMeta");
        try {
            ListEtlMetaResponse resp = client.listEtlMeta(project, etlMetaName_1, "", 0, 200);
            assertEquals(resp.getCount(), 1);
            assertEquals(resp.getTotal(), 1);
            resp = client.listEtlMeta(project, etlMetaName_1, etlMetaKeyPrefxi_1, 0, 200);
            assertEquals(resp.getCount(), 200);
            assertEquals(resp.getTotal(), etlMetaCount_1 - 1);
            resp = client.listEtlMeta(project, etlMetaName_1, etlMetaKeyPrefxi_1, 200, 200);
            assertEquals(resp.getCount(), 4);
            assertEquals(resp.getTotal(), etlMetaCount_1 - 1);
            resp = client.listEtlMeta(project, etlMetaName_2, etlMetaKeyPrefxi_2, 0, 200);
            assertEquals(resp.getCount(), 0);
            assertEquals(resp.getTotal(), 0);
            resp = client.listEtlMeta(project, etlMetaName_2, "", 0, 200);
            assertEquals(resp.getCount(), etlMetaCount_2);
            assertEquals(resp.getTotal(), etlMetaCount_2);

            ListEtlMetaNameResponse lsNameResp = client.listEtlMetaName(project, 0, 100);
            assertEquals(lsNameResp.getCount(), 2);
            assertEquals(lsNameResp.getTotal(), 2);

            ArrayList<EtlMeta> toDeleted = new ArrayList<EtlMeta>();
            resp = client.listEtlMeta(project, etlMetaName_1, 0, 200);
            for (EtlMeta meta : resp.getEtlMetaList()) {
                toDeleted.add(meta);
            }
            resp = client.listEtlMeta(project, etlMetaName_1, 200, 200);
            for (EtlMeta meta : resp.getEtlMetaList()) {
                toDeleted.add(meta);
            }
            assertEquals(toDeleted.size(), etlMetaCount_1);
            for (int i = 0; i < 10; ++i) {
                EtlMeta meta = toDeleted.get(i);
                try {
                    client.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), etlMetaKeyPrefxi_2);
                    fail();
                } catch (LogException e) {
                    assertEquals(e.GetErrorCode(), "EtlMetaNotExist");
                }
            }
            for (int i = 10; i < 20; ++i) {
                EtlMeta meta = toDeleted.get(i);
                try {
                    client.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), "");
                    fail();
                } catch (LogException e) {
                    assertEquals(e.GetErrorCode(), "EtlMetaNotExist");
                }
            }
            int i = 0;
            for (EtlMeta meta : toDeleted) {
                if (i < (etlMetaCount_1 / 3)) {
                    client.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey());
                } else if (i < (etlMetaCount_1 * 2 / 3)) {
                    client.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), etlMetaKeyPrefxi_1);
                } else {
                    client.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), Consts.CONST_ETLMETA_ALL_TAG_MATCH);
                }
                ++i;
            }

            toDeleted.clear();
            resp = client.listEtlMeta(project, etlMetaName_2, 0, 200);
            for (EtlMeta meta : resp.getEtlMetaList()) {
                toDeleted.add(meta);
            }

            for (EtlMeta meta : toDeleted) {
                try {
                    client.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), etlMetaKeyPrefxi_1);
                    fail();
                } catch (LogException e) {
                    assertEquals(e.GetErrorCode(), "EtlMetaNotExist");
                }
            }
            i = 0;
            for (EtlMeta meta : toDeleted) {
                if (i < etlMetaCount_2 / 2) {
                    client.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey());
                } else {
                    client.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), "");
                }
                ++i;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lsNameResp = client.listEtlMetaName(project, 0, 100);
            assertEquals(lsNameResp.getCount(), 0);
            assertEquals(lsNameResp.getTotal(), 0);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testBatchEtlMetaApi() {

        final String BATCH_META_NAME = "batch_meta";
        final int TAG_1_COUNT = 50;
        final int TAG_2_COUNT = 20;
        System.out.println("testBatchCreateEtlMeta");
        ArrayList<EtlMeta> failMetaList = new ArrayList<EtlMeta>();
        try {
            client.batchCreateEtlMeta(project, failMetaList);
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        try {
            client.batchUpdateEtlMeta(project, failMetaList);
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }

        for (int i = 0; i < 101; ++i) {
            String metaKey = etlMetaKeyPrefxi_1 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(BATCH_META_NAME, metaKey, "");
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "slb-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            failMetaList.add(meta);
        }
        try {
            client.batchCreateEtlMeta(project, failMetaList);
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        try {
            client.batchUpdateEtlMeta(project, failMetaList);
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }

        ArrayList<EtlMeta> successMetaList = new ArrayList<EtlMeta>();
        /////////////////////////////////////
        for (int i = 0; i < TAG_1_COUNT; ++i) {
            String metaKey = etlMetaKeyPrefxi_1 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(BATCH_META_NAME, metaKey, "1");
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "slb-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            successMetaList.add(meta);
        }
        try {
            client.batchCreateEtlMeta(project, successMetaList);
        } catch (LogException e) {
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            fail();
        }

        failMetaList.clear();
        //have a meta alreay existed
        for (int i = TAG_1_COUNT - 1; i < TAG_1_COUNT + 10; ++i) {
            String metaKey = etlMetaKeyPrefxi_1 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(BATCH_META_NAME, metaKey, etlMetaKeyPrefxi_1);
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "slb-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            failMetaList.add(meta);
        }
        try {
            client.batchCreateEtlMeta(project, failMetaList);
            fail();
        } catch (LogException e) {
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            assertEquals(e.GetErrorCode(), "EtlMetaAlreadyExist");
        }

        successMetaList.clear();
        /////////////////////////////////////
        for (int i = 0; i < TAG_2_COUNT; ++i) {
            String metaKey = etlMetaKeyPrefxi_2 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(BATCH_META_NAME, metaKey, "2");
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "apigateway-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            successMetaList.add(meta);
        }
        try {
            client.batchCreateEtlMeta(project, successMetaList);
        } catch (LogException e) {
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            fail();
        }

        System.out.println("testBatchUpdateEtlMeta");
        failMetaList.clear();
        //have a meta not exist
        for (int i = 0; i < TAG_2_COUNT + 1; ++i) {
            String metaKey = etlMetaKeyPrefxi_2 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(BATCH_META_NAME, metaKey, "");
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "apigateway-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            failMetaList.add(meta);
        }
        try {
            client.batchUpdateEtlMeta(project, failMetaList);
            fail();
        } catch (LogException e) {
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            assertEquals(e.GetErrorCode(), "EtlMetaNotExist");
        }

        successMetaList.clear();
        /////////////////////////////////////
        for (int i = 10; i < 20; ++i) {
            String metaKey = etlMetaKeyPrefxi_2 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(BATCH_META_NAME, metaKey, "");
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "apigateway-log-update");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            successMetaList.add(meta);
        }
        try {
            client.batchUpdateEtlMeta(project, successMetaList);
        } catch (LogException e) {
            System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            fail();
        }

        ListEtlMetaResponse resp = null;
        try {
            resp = client.listEtlMeta(project, BATCH_META_NAME, "", 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), 10);
        try {
            resp = client.listEtlMeta(project, BATCH_META_NAME, "1", 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), TAG_1_COUNT);
        try {
            resp = client.listEtlMeta(project, BATCH_META_NAME, "2", 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), TAG_2_COUNT - 10);

        try {
            client.batchDeleteEtlMeta(project, BATCH_META_NAME, "");
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        try {
            resp = client.listEtlMeta(project, BATCH_META_NAME, "", 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), 0);

        ArrayList<String> delKeyList = new ArrayList<String>();
        for (int i = 0; i < TAG_2_COUNT; ++i) {
            delKeyList.add(etlMetaKeyPrefxi_2 + String.valueOf(i));
        }
        try {
            client.batchDeleteEtlMeta(project, BATCH_META_NAME, "");
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        try {
            resp = client.listEtlMeta(project, BATCH_META_NAME, 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), TAG_1_COUNT + TAG_2_COUNT - 10);

        try {
            client.batchDeleteEtlMeta(project, BATCH_META_NAME, "2");
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        try {
            resp = client.listEtlMeta(project, BATCH_META_NAME, 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), TAG_1_COUNT);

        delKeyList.clear();
        try {
            client.batchDeleteEtlMeta(project, BATCH_META_NAME, delKeyList);
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        for (int i = 0; i < 1000; ++i) {
            delKeyList.add(etlMetaKeyPrefxi_1 + "_" + String.valueOf(i));
        }
        try {
            client.batchDeleteEtlMeta(project, BATCH_META_NAME, delKeyList);
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }

        delKeyList.clear();
        for (int i = 0; i < 2 * TAG_1_COUNT; ++i) {
            delKeyList.add(etlMetaKeyPrefxi_1 + "_" + String.valueOf(i));
        }
        try {
            client.batchDeleteEtlMeta(project, BATCH_META_NAME, delKeyList);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        try {
            resp = client.listEtlMeta(project, BATCH_META_NAME, 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), 0);
        assertEquals(resp.getTotal(), 0);

        System.out.println("testBatchDeleteEtlMeta");
    }

    @Test
    public void testEnableDisableEtlMeta() {

        final String META_NAME = "enable_disable_meta";
        final int ENABLE_COUNT = 10;
        final int DISABLE_COUNT = 10;
        System.out.println("testEnableDisableEtlMeta");
        ArrayList<String> totalMetaKeyList = new ArrayList<String>();
        ArrayList<EtlMeta> enableMetaList = new ArrayList<EtlMeta>();
        for (int i = 0; i < ENABLE_COUNT; ++i) {
            String metaKey = "enable_" + String.valueOf(i);
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "slb-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            EtlMeta meta = new EtlMeta(META_NAME, metaKey, "", metaValueObj, true);
            if (i == 0) {
                try {
                    client.createEtlMeta(project, meta);
                } catch (LogException e) {
                    e.printStackTrace();
                    fail();
                }
            } else {
                enableMetaList.add(meta);
            }
            totalMetaKeyList.add(metaKey);
        }
        try {
            client.batchCreateEtlMeta(project, enableMetaList);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }

        ArrayList<EtlMeta> disableMetaList = new ArrayList<EtlMeta>();
        for (int i = 0; i < DISABLE_COUNT; ++i) {
            String metaKey = "disable_" + String.valueOf(i);
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", userAliuid);
            metaValueObj.put("region", userRegion);
            metaValueObj.put("project", userProject);
            metaValueObj.put("logstore", "slb-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            EtlMeta meta = new EtlMeta(META_NAME, metaKey, "", metaValueObj, false);
            if (i == 0) {
                try {
                    client.createEtlMeta(project, meta);
                } catch (LogException e) {
                    e.printStackTrace();
                    fail();
                }
            } else {
                disableMetaList.add(meta);
            }
            totalMetaKeyList.add(metaKey);
        }
        try {
            client.batchCreateEtlMeta(project, disableMetaList);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }

        ListEtlMetaResponse resp = null;
        try {
            resp = client.listEtlMeta(project, META_NAME, 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), ENABLE_COUNT + DISABLE_COUNT);
        assertEquals(resp.getTotal(), ENABLE_COUNT + DISABLE_COUNT);
        for (EtlMeta meta : resp.getEtlMetaList()) {
            if (meta.getMetaKey().startsWith("enable_")) {
                assertTrue(meta.isEnable());
            } else if (meta.getMetaKey().startsWith("disable_")) {
                assertTrue(!meta.isEnable());
            } else {
                fail();
            }
            assertEquals(meta.getMetaTag(), "");
            assertTrue(meta.getMetaValue().toString().contains("aliyunlogwriteonlyrole"));
        }

        EtlMeta tmpMeta_1 = enableMetaList.get(0);
        tmpMeta_1.setEnable(false);
        try {
            client.updateEtlMeta(project, tmpMeta_1);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        EtlMeta tmpMeta_2 = enableMetaList.get(1);
        tmpMeta_2.setEnable(true);
        try {
            client.updateEtlMeta(project, tmpMeta_2);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        ArrayList<EtlMeta> tmpMetaList = new ArrayList<EtlMeta>();
        tmpMetaList.add(new EtlMeta(disableMetaList.get(0).getMetaName(), disableMetaList.get(0).getMetaKey(), true));
        tmpMetaList.add(new EtlMeta(disableMetaList.get(1).getMetaName(), disableMetaList.get(1).getMetaKey(), false));
        try {
            client.batchUpdateEtlMeta(project, tmpMetaList);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }

        try {
            resp = client.listEtlMeta(project, META_NAME, 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), ENABLE_COUNT + DISABLE_COUNT);
        assertEquals(resp.getTotal(), ENABLE_COUNT + DISABLE_COUNT);
        for (EtlMeta meta : resp.getEtlMetaList()) {
            if (meta.getMetaKey().startsWith("enable_")) {
                if (meta.getMetaKey().endsWith("_1")) {
                    assertTrue(!meta.isEnable());
                } else {
                    assertTrue(meta.isEnable());
                }
            } else if (meta.getMetaKey().startsWith("disable_")) {
                if (meta.getMetaKey().endsWith("_1")) {
                    assertTrue(meta.isEnable());
                } else {
                    assertTrue(!meta.isEnable());
                }
            } else {
                fail();
            }
            assertEquals(meta.getMetaTag(), "");
            assertTrue(meta.getMetaValue().toString().contains("aliyunlogwriteonlyrole"));
        }

        try {
            client.batchDeleteEtlMeta(project, META_NAME, totalMetaKeyList);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        try {
            resp = client.listEtlMeta(project, META_NAME, 0, 200);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(resp.getCount(), 0);
        assertEquals(resp.getTotal(), 0);

        System.out.println("testEnableDisableEtlMeta");
    }
}
