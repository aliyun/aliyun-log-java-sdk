package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.EtlMeta;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.CreateEtlMetaResponse;
import com.aliyun.openservices.log.response.DeleteEtlMetaResponse;
import com.aliyun.openservices.log.response.ListEtlMetaNameResponse;
import com.aliyun.openservices.log.response.ListEtlMetaResponse;
import com.aliyun.openservices.log.response.UpdateEtlMetaResponse;
import net.sf.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class EtlMetaFunctionTest {

    private static String accessKeyId = "";
    private static String accessKeySecret = "";
    private static String endpoint = "http://cn-hangzhou-staging-intranet.sls.aliyuncs.com";
    private static String project = "ali-slstest-trigger";
    private static Client logClient = null;
    private static String etlMetaName_1 = "slb-user-logging-rule";
    private static String etlMetaName_2 = "apigateway-user-logging-rule";
    private static String etlMetaKeyPrefxi_1 = UUID.randomUUID().toString();
    private static String etlMetaKeyPrefxi_2 = UUID.randomUUID().toString();
    private static int etlMetaCount_1 = 205;
    private static int etlMetaCount_2 = 5;

    @BeforeClass
    public static void setup() {
        logClient = new Client(endpoint, accessKeyId, accessKeySecret);
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
    public void testCreateEtlMeta() {

        System.out.println("testCreateEtlMeta");
        for (int i = 0; i < etlMetaCount_1; ++i) {
            String metaKey = etlMetaKeyPrefxi_1 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(etlMetaName_1, metaKey, etlMetaKeyPrefxi_1);
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", String.valueOf(i));
            metaValueObj.put("region", "cn-hangzhou");
            metaValueObj.put("project", "ali-log-test");
            metaValueObj.put("logstore", "slb-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            try {
                CreateEtlMetaResponse resp = logClient.createEtlMeta(project, meta);
                Assert.assertTrue(true);
            } catch (LogException e) {
                System.err.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
                fail();
            }
        }
        for (int i = 0; i < etlMetaCount_2; ++i) {
            String metaKey = etlMetaKeyPrefxi_2 + "_" + String.valueOf(i);
            EtlMeta meta = new EtlMeta(etlMetaName_2, metaKey, "");
            JSONObject metaValueObj = new JSONObject();
            metaValueObj.put("aliuid", String.valueOf(i));
            metaValueObj.put("region", "cn-hangzhou");
            metaValueObj.put("project", "ali-log-test");
            metaValueObj.put("logstore", "apigateway-log");
            metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(i) + ":role/aliyunlogwriteonlyrole");
            meta.setMetaValue(metaValueObj);
            try {
                CreateEtlMetaResponse resp = logClient.createEtlMeta(project, meta);
                Assert.assertTrue(true);
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
        int offset = 0;
        ArrayList<EtlMeta> slbMetas = new ArrayList<EtlMeta>();
        try {
            while (true) {
                ListEtlMetaResponse metas = logClient.listEtlMeta(project, etlMetaName_1,"ali-log-test", "slb-log", offset, 100);
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
        }

        try {
            ListEtlMetaResponse metas = logClient.listEtlMeta(project, etlMetaName_2,"ali-log-test", "apigateway-log", 0, 100);
            assertEquals(metas.getTotal(), etlMetaCount_2);
            assertEquals(metas.getCount(), etlMetaCount_2);
        } catch (LogException e) {
            e.printStackTrace();
        }

        try {
            ListEtlMetaResponse metas = logClient.listEtlMeta(project, etlMetaName_1,"ali-log-test", "apigateway-log", 0, 100);
            assertEquals(metas.getTotal(), 0);
            assertEquals(metas.getCount(), 0);
        } catch (LogException e) {
            e.printStackTrace();
        }

        try {
            ListEtlMetaResponse metas = logClient.listEtlMeta(project, etlMetaName_2 + "xxx","ali-log-test", "apigateway-log", 0, 100);
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
        metaValueObj.put("aliuid", String.valueOf(0));
        metaValueObj.put("region", "cn-hangzhou");
        metaValueObj.put("project", "ali-log-test");
        metaValueObj.put("logstore", "slb-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            CreateEtlMetaResponse resp = logClient.createEtlMeta(project, meta);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "EtlMetaAlreadyExist");
        }

        metaKey = etlMetaKeyPrefxi_1 + "_" + String.valueOf(500);
        meta = new EtlMeta(etlMetaName_1, metaKey, Consts.CONST_ETLMETA_ALL_TAG_MATCH);
        metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", String.valueOf(0));
        metaValueObj.put("region", "cn-hangzhou");
        metaValueObj.put("project", "ali-log-test");
        metaValueObj.put("logstore", "slb-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            CreateEtlMetaResponse resp = logClient.createEtlMeta(project, meta);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
        }

        meta = new EtlMeta(etlMetaName_2, "n", etlMetaKeyPrefxi_2);
        metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", String.valueOf(0));
        metaValueObj.put("region", "cn-hangzhou");
        metaValueObj.put("project", "ali-log-test");
        metaValueObj.put("logstore", "apigateway-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            CreateEtlMetaResponse resp = logClient.createEtlMeta(project, meta);
            fail();
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            assertEquals(e.GetErrorCode(), "PostBodyInvalid");
        }

        meta = new EtlMeta("x", metaKey, "");
        metaValueObj = new JSONObject();
        metaValueObj.put("aliuid", String.valueOf(0));
        metaValueObj.put("region", "cn-hangzhou");
        metaValueObj.put("project", "ali-log-test");
        metaValueObj.put("logstore", "apigateway-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            CreateEtlMetaResponse resp = logClient.createEtlMeta(project, meta);
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
            ListEtlMetaNameResponse resp = logClient.listEtlMetaName(project, 0, 200);
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
        metaValueObj.put("aliuid", String.valueOf(0));
        metaValueObj.put("region", "cn-hangzhou");
        metaValueObj.put("project", "ali-log-test");
        metaValueObj.put("logstore", "slb-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            UpdateEtlMetaResponse resp = logClient.updateEtlMeta(project, meta);
            Assert.assertTrue(true);
        } catch (LogException e) {
            System.out.println(e.GetErrorMessage());
            fail();
        }

        try {
            ListEtlMetaResponse resp = logClient.getEtlMeta(project, etlMetaName_1, metaKey);
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
        metaValueObj.put("aliuid", String.valueOf(0));
        metaValueObj.put("region", "cn-hangzhou");
        metaValueObj.put("project", "ali-log-test");
        metaValueObj.put("logstore", "apigateway-log");
        metaValueObj.put("roleArn", "acs:ram::" + String.valueOf(0) + ":role/aliyunlogwriteonlyrole");
        meta.setMetaValue(metaValueObj);
        try {
            UpdateEtlMetaResponse resp = logClient.updateEtlMeta(project, meta);
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
            ListEtlMetaResponse resp = logClient.listEtlMeta(project, etlMetaName_1, "", 0, 200);
            assertEquals(resp.getCount(), 1);
            assertEquals(resp.getTotal(), 1);
            resp = logClient.listEtlMeta(project, etlMetaName_1, etlMetaKeyPrefxi_1, 0, 200);
            assertEquals(resp.getCount(), 200);
            assertEquals(resp.getTotal(), etlMetaCount_1 - 1);
            resp = logClient.listEtlMeta(project, etlMetaName_1, etlMetaKeyPrefxi_1, 200, 200);
            assertEquals(resp.getCount(), 4);
            assertEquals(resp.getTotal(), etlMetaCount_1 - 1);
            resp = logClient.listEtlMeta(project, etlMetaName_2, etlMetaKeyPrefxi_2, 0, 200);
            assertEquals(resp.getCount(), 0);
            assertEquals(resp.getTotal(), 0);
            resp = logClient.listEtlMeta(project, etlMetaName_2, "", 0, 200);
            assertEquals(resp.getCount(), etlMetaCount_2);
            assertEquals(resp.getTotal(), etlMetaCount_2);

            ListEtlMetaNameResponse lsNameResp = logClient.listEtlMetaName(project, 0, 100);
            assertEquals(lsNameResp.getCount(), 2);
            assertEquals(lsNameResp.getTotal(), 2);

            ArrayList<EtlMeta> toDeleted = new ArrayList<EtlMeta>();
            resp = logClient.listEtlMeta(project, etlMetaName_1, 0, 200);
            for (EtlMeta meta : resp.getEtlMetaList()) {
                toDeleted.add(meta);
            }
            resp = logClient.listEtlMeta(project, etlMetaName_1, 200, 200);
            for (EtlMeta meta : resp.getEtlMetaList()) {
                toDeleted.add(meta);
            }
            assertEquals(toDeleted.size(), etlMetaCount_1);
            for (int i = 0; i < 10; ++i) {
                EtlMeta meta = toDeleted.get(i);
                try {
                    DeleteEtlMetaResponse delResp = logClient.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), etlMetaKeyPrefxi_2);
                    fail();
                } catch (LogException e) {
                    assertEquals(e.GetErrorCode(), "EtlMetaNotExist");
                }
            }
            for (int i = 10; i < 20; ++i) {
                EtlMeta meta = toDeleted.get(i);
                try {
                    DeleteEtlMetaResponse delResp = logClient.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), "");
                    fail();
                } catch (LogException e) {
                    assertEquals(e.GetErrorCode(), "EtlMetaNotExist");
                }
            }
            int i = 0;
            for (EtlMeta meta : toDeleted) {
                if (i < (etlMetaCount_1 / 3)) {
                    DeleteEtlMetaResponse delResp = logClient.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey());
                    Assert.assertTrue(true);
                } else if (i < (etlMetaCount_1 * 2 / 3)) {
                    DeleteEtlMetaResponse delResp = logClient.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), etlMetaKeyPrefxi_1);
                    Assert.assertTrue(true);
                } else {
                    DeleteEtlMetaResponse delResp = logClient.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), Consts.CONST_ETLMETA_ALL_TAG_MATCH);
                    Assert.assertTrue(true);
                }
                ++i;
            }

            toDeleted.clear();
            resp = logClient.listEtlMeta(project, etlMetaName_2, 0, 200);
            for (EtlMeta meta : resp.getEtlMetaList()) {
                toDeleted.add(meta);
            }

            for (EtlMeta meta : toDeleted) {
                try {
                    DeleteEtlMetaResponse delResp = logClient.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), etlMetaKeyPrefxi_1);
                    fail();
                } catch (LogException e) {
                    assertEquals(e.GetErrorCode(), "EtlMetaNotExist");
                }
            }
            i = 0;
            for (EtlMeta meta : toDeleted) {
                if (i < etlMetaCount_2 / 2) {
                    DeleteEtlMetaResponse delResp = logClient.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey());
                    Assert.assertTrue(true);
                } else {
                    DeleteEtlMetaResponse delResp = logClient.deleteEtlMeta(project, meta.getMetaName(), meta.getMetaKey(), "");
                    Assert.assertTrue(true);
                }
                ++i;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lsNameResp = logClient.listEtlMetaName(project, 0, 100);
            assertEquals(lsNameResp.getCount(), 0);
            assertEquals(lsNameResp.getTotal(), 0);
        } catch (LogException e) {
            e.printStackTrace();
            fail();
        }
    }

}
