package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.OdpsShipperConfig;
import com.aliyun.openservices.log.common.OssShipperConfig;
import com.aliyun.openservices.log.common.OssShipperCsvStorageDetail;
import com.aliyun.openservices.log.common.OssShipperJsonStorageDetail;
import com.aliyun.openservices.log.common.OssShipperParquetStorageDetail;
import com.aliyun.openservices.log.common.OssShipperStorageColumn;
import com.aliyun.openservices.log.common.ShipperConfig;
import com.aliyun.openservices.log.common.ShipperTask;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetShipperResponse;
import com.aliyun.openservices.log.response.GetShipperTasksResponse;
import com.aliyun.openservices.log.response.ListShipperResponse;
import com.aliyun.openservices.log.response.RetryShipperTasksResponse;
import com.aliyun.openservices.log.response.UpdateShipperResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ShipperTest extends JobIntgTest {
    private static final String TEST_LOGSTORE = "test-logstore";
    private static final String ossBucket = "audit-zyf-hangzhou";
    private static final String ossPrefix = "logtailalarm";
    private static final String aliUid = credentials.getAliuid();
    private static final String roleArn = "acs:ram::" + aliUid + ":role/aliyunlogdefaultrole";
    private static final String compressType = "none";
    private static final String pathFormat = "%Y/%m/%d/%H";
    private static final int bufferInterval = 300;
    private static final int bufferSize = 10;

    @BeforeClass
    public static void prepareLogstore() throws Exception {
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(1);
        logStore.SetLogStoreName(TEST_LOGSTORE);
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(true);
        createOrUpdateLogStoreSimple(TEST_PROJECT, logStore);
    }

    @Test
    public void testGetNotExistShipper() {
        Random random = new Random(1024);
        for (int i = 0; i < 5; i++) {
            String shipperName = "shipper-" + random.nextInt(10000);
            try {
                client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
                fail();
            } catch (LogException ex) {
                assertEquals(ex.GetHttpCode(), 404);
                assertEquals(ex.GetErrorCode(), "ShipperNotExist");
                assertEquals(ex.GetErrorMessage(), "The shipper " + shipperName + " does not exist");
            }
        }
    }

    @Test
    public void testCreateOdpsShipper() throws LogException {
        String shipperName = "odpsshipper";
        String odpsEndPoint = "http://odps-ext.aliyun-inc.com/api";
        String odpsProject = "dpdefault_925366";
        String odpsTable = "sls_odps_test";
        List<String> logFieldsList = new ArrayList<String>();
        logFieldsList.add("__time__");
        logFieldsList.add("uuid");

        List<String> partitionColumn = new ArrayList<String>();
        partitionColumn.add("__PARTITION_TIME__");
        String partitionTimeFormat = "yyyy_MM_dd_HH_mm";

        ShipperConfig shipConfig = new OdpsShipperConfig(odpsEndPoint,
                odpsProject, odpsTable, logFieldsList, partitionColumn,
                partitionTimeFormat);

        try {
            client.CreateShipper(TEST_PROJECT, TEST_LOGSTORE, "invalid-shipper-name-$@#", shipConfig);
            fail("Invalid shipper name should fail");
        } catch (LogException ex) {
            assertEquals("PostBodyInvalid", ex.GetErrorCode());
            assertEquals("invalid shipper name format invalid-shipper-name-$@#", ex.GetErrorMessage());
        }

        client.CreateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, shipConfig);

        GetShipperResponse res = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE,
                shipperName);
        assertEquals("odps", res.GetConfig().GetShipperType());

        logFieldsList.add("id");
        shipConfig = new OdpsShipperConfig(odpsEndPoint,
                odpsProject, odpsTable, logFieldsList, partitionColumn,
                partitionTimeFormat);
        client.UpdateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, shipConfig);
        GetShipperResponse resUpdate = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE,
                shipperName);
        JSONObject odpsJson = resUpdate.GetConfig().GetJsonObj();
        // test fields
        List<String> fieldupdate = (List<String>) odpsJson.get("fields");
        assertEquals(3, fieldupdate.size());
        assertTrue(fieldupdate.contains("__time__"));
        assertTrue(fieldupdate.contains("uuid"));
        assertTrue(fieldupdate.contains("id"));
        // test bufferInterval
        String bufferInterval = odpsJson.getString("bufferInterval");
        assertEquals("1800", bufferInterval);
        // test partitionTimeFormatString
        String partitionTimeFormatString = odpsJson.getString("partitionTimeFormat");
        assertEquals("yyyy_MM_dd_HH_mm", partitionTimeFormatString);
        // test odpsEndpoint
        String odpsEndpointString = odpsJson.getString("odpsEndpoint");
        assertEquals("http://odps-ext.aliyun-inc.com/api", odpsEndpointString);
        // test partitionColumn
        List<String> partitionColumnList = (List<String>) odpsJson.get("partitionColumn");
        assertEquals("__PARTITION_TIME__", partitionColumnList.get(0));
        // test odpsProject
        String odpsProjectString = odpsJson.getString("odpsProject");
        assertEquals("dpdefault_925366", odpsProjectString);

        int startTime = (int) (System.currentTimeMillis() / 1000.0 - 7200);
        int endTime = (int) (System.currentTimeMillis() / 1000.0);
        List<String> shipperTask = new ArrayList<String>();
        GetShipperTasksResponse taskRes = client.GetShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, startTime, endTime, "", 0, 10);
        assertEquals(0, taskRes.GetTotalTask());
        for (ShipperTask shipperTaskString : taskRes.GetShipperTasks()) {
            if ("fail".equals(shipperTaskString.GetTaskStatus())) {
                shipperTask.add(shipperTaskString.GetTaskId());
            }
        }
        if (shipperTask.size() != 0) {
            RetryShipperTasksResponse retryShipperTasksResponse = client.RetryShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, shipperTask);
        }

        ListShipperResponse listShipperResponse = client.ListShipper(TEST_PROJECT, TEST_LOGSTORE);
        assertEquals(1, listShipperResponse.GetTotal());

        client.DeleteShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName);
    }

    @Test
    public void testCreateJsonOssShipper() throws LogException {
        String shipperName = "ossjsonshipper";
        try {
            client.DeleteShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "ShipperNotExist", ex.GetErrorCode());
        }

        OssShipperConfig ossConfig = new OssShipperConfig(ossBucket, ossPrefix, roleArn, bufferInterval, bufferSize, compressType, pathFormat, "json", "");
        OssShipperJsonStorageDetail detail = (OssShipperJsonStorageDetail) ossConfig.GetStorageDetail();
        detail.setEnableTag(false);
        client.CreateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, ossConfig);

        GetShipperResponse ossRes = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        assertEquals("oss", ossRes.GetConfig().GetShipperType());
        assertEquals(ossPrefix, ossRes.GetConfig().GetJsonObj().get("ossPrefix"));

        ossConfig = new OssShipperConfig(ossBucket, "updatePrefix", roleArn, bufferInterval, bufferSize, compressType, pathFormat, "json", "");
        UpdateShipperResponse updateShipperResponse = client.UpdateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, ossConfig);
        GetShipperResponse ossResUpdate = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        JSONObject ossJson = ossResUpdate.GetConfig().GetJsonObj();
        assertEquals("updatePrefix", ossJson.get("ossPrefix"));
        assertEquals("audit-zyf-hangzhou", ossJson.get("ossBucket"));
        assertEquals(10, ossJson.get("bufferSize"));
        assertEquals(300, ossJson.get("bufferInterval"));
        assertEquals("%Y/%m/%d/%H", ossJson.get("pathFormat"));
        JSONObject storageObj = ossJson.getJSONObject("storage");
        assertEquals("json", storageObj.get("format"));
        JSONObject detailObj = storageObj.getJSONObject("detail");
        assertFalse(Boolean.parseBoolean(detailObj.get("enableTag").toString()));

        int startTime = (int) (System.currentTimeMillis() / 1000.0 - 7200);
        int endTime = (int) (System.currentTimeMillis() / 1000.0);
        List<String> shipperTask = new ArrayList<String>();
        GetShipperTasksResponse taskRes = client.GetShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, startTime, endTime, "", 0, 10);
        assertEquals(0, taskRes.GetTotalTask());
        for (ShipperTask shipperTaskString : taskRes.GetShipperTasks()) {
            if ("fail".equals(shipperTaskString.GetTaskStatus())) {
                shipperTask.add(shipperTaskString.GetTaskId());
            }
        }
        if (shipperTask.size() != 0) {
            RetryShipperTasksResponse retryShipperTasksResponse = client.RetryShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, shipperTask);
        }
        ListShipperResponse listShipperResponse = client.ListShipper(TEST_PROJECT, TEST_LOGSTORE);
        assertEquals(1, listShipperResponse.GetTotal());

        client.DeleteShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName);
    }

    @Test
    public void testCreateParquetOssShipper() throws LogException {
        String shipperName = "ossparquetshipper";
        OssShipperConfig ossConfig = new OssShipperConfig(ossBucket, ossPrefix, roleArn, bufferInterval, bufferSize, compressType, pathFormat, "parquet", "+1000");
        ArrayList<OssShipperStorageColumn> columns = new ArrayList<OssShipperStorageColumn>();
        columns.add(new OssShipperStorageColumn("MachineName", "string"));
        columns.add(new OssShipperStorageColumn("Role", "string"));
        columns.add(new OssShipperStorageColumn("ResValue", "int32"));
        columns.add(new OssShipperStorageColumn("__LINE__", "int64"));
        columns.add(new OssShipperStorageColumn("__THREAD__", "double"));
        OssShipperParquetStorageDetail detail = (OssShipperParquetStorageDetail) ossConfig.GetStorageDetail();
        detail.setStorageColumns(columns);
        client.CreateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, ossConfig);

        GetShipperResponse ossRes = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        assertEquals("oss", ossRes.GetConfig().GetShipperType());
        assertEquals(ossPrefix, ossRes.GetConfig().GetJsonObj().get("ossPrefix"));

        ossConfig = new OssShipperConfig(ossBucket, "updatePrefix", roleArn, bufferInterval, bufferSize, compressType, pathFormat, "parquet", "");
        detail = (OssShipperParquetStorageDetail) ossConfig.GetStorageDetail();
        detail.setStorageColumns(columns);
        UpdateShipperResponse updateShipperResponse = client.UpdateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, ossConfig);
        GetShipperResponse ossResUpdate = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        JSONObject ossJson = ossResUpdate.GetConfig().GetJsonObj();
        assertEquals("updatePrefix", ossJson.get("ossPrefix"));
        assertEquals("audit-zyf-hangzhou", ossJson.get("ossBucket"));
        assertEquals(10, ossJson.get("bufferSize"));
        assertEquals(300, ossJson.get("bufferInterval"));
        assertEquals("%Y/%m/%d/%H", ossJson.get("pathFormat"));
        JSONObject storageObj = ossJson.getJSONObject("storage");
        assertEquals("parquet", storageObj.get("format"));
        JSONObject detailObj = storageObj.getJSONObject("detail");
        JSONArray columnsArray = detailObj.getJSONArray("columns");
        assertEquals(5, columnsArray.size());

        int startTime = (int) (System.currentTimeMillis() / 1000.0 - 7200);
        int endTime = (int) (System.currentTimeMillis() / 1000.0);
        List<String> shipperTask = new ArrayList<String>();
        GetShipperTasksResponse taskRes = client.GetShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, startTime, endTime, "", 0, 10);
        assertEquals(0, taskRes.GetTotalTask());
        for (ShipperTask shipperTaskString : taskRes.GetShipperTasks()) {
            if ("fail".equals(shipperTaskString.GetTaskStatus())) {
                shipperTask.add(shipperTaskString.GetTaskId());
            }
        }
        if (shipperTask.size() != 0) {
            RetryShipperTasksResponse retryShipperTasksResponse = client.RetryShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, shipperTask);
        }
        ListShipperResponse listShipperResponse = client.ListShipper(TEST_PROJECT, TEST_LOGSTORE);
        assertEquals(1, listShipperResponse.GetTotal());

        client.DeleteShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName);
    }

    @Test
    public void testCreateCsvOssShipper() throws LogException {
        String shipperName = "osscsvshipper";
        OssShipperConfig ossConfig = new OssShipperConfig(ossBucket, ossPrefix, roleArn, bufferInterval, bufferSize, compressType, pathFormat, "csv", "+0800");
        ArrayList<String> columns = new ArrayList<String>();
        columns.add("__topic__");
        columns.add("alarm_count");
        columns.add("alarm_message");
        columns.add("alarm_type");
        columns.add("category");
        columns.add("project_name");
        OssShipperCsvStorageDetail detail = (OssShipperCsvStorageDetail) ossConfig.GetStorageDetail();
        detail.setDelimiter(",");
        detail.setmStorageColumns(columns);
        detail.setQuote("\"");
        detail.setNullIdentifier("");
        detail.setHeader(false);
        client.CreateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, ossConfig);

        GetShipperResponse ossRes = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        assertEquals("oss", ossRes.GetConfig().GetShipperType());
        assertEquals(ossPrefix, ossRes.GetConfig().GetJsonObj().get("ossPrefix"));

        ossConfig = new OssShipperConfig(ossBucket, "updatePrefix", roleArn, bufferInterval, bufferSize, compressType, pathFormat, "csv", "");
        detail = (OssShipperCsvStorageDetail) ossConfig.GetStorageDetail();
        columns.add("logstore");
        detail.setDelimiter(",");
        detail.setmStorageColumns(columns);
        detail.setQuote("\"");
        detail.setNullIdentifier("");
        detail.setHeader(false);
        UpdateShipperResponse updateShipperResponse = client.UpdateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, ossConfig);
        detail.setDelimiter(",");
        detail.setmStorageColumns(columns);
        detail.setQuote("\"");
        detail.setNullIdentifier("");
        detail.setHeader(false);
        GetShipperResponse ossResUpdate = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        JSONObject ossJson = ossResUpdate.GetConfig().GetJsonObj();
        assertEquals("updatePrefix", ossJson.get("ossPrefix"));
        assertEquals("audit-zyf-hangzhou", ossJson.get("ossBucket"));
        assertEquals(10, ossJson.get("bufferSize"));
        assertEquals(300, ossJson.get("bufferInterval"));
        assertEquals("%Y/%m/%d/%H", ossJson.get("pathFormat"));
        JSONObject storageObj = ossJson.getJSONObject("storage");
        assertEquals("csv", storageObj.get("format"));
        JSONObject detailObj = storageObj.getJSONObject("detail");
        assertEquals("\n", detailObj.get("lineFeed"));
        assertEquals("\"", detailObj.get("quote"));
        List<String> columnsList = (List<String>) detailObj.get("columns");
        assertTrue(columnsList.contains("__topic__"));
        assertTrue(columnsList.contains("alarm_count"));
        assertTrue(columnsList.contains("alarm_message"));
        assertTrue(columnsList.contains("alarm_type"));
        assertTrue(columnsList.contains("category"));
        assertTrue(columnsList.contains("project_name"));
        assertTrue(columnsList.contains("logstore"));

        int startTime = (int) (System.currentTimeMillis() / 1000.0 - 7200);
        int endTime = (int) (System.currentTimeMillis() / 1000.0);
        List<String> shipperTask = new ArrayList<String>();
        GetShipperTasksResponse taskRes = client.GetShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, startTime, endTime, "", 0, 10);
        assertEquals(0, taskRes.GetTotalTask());
        for (ShipperTask shipperTaskString : taskRes.GetShipperTasks()) {
            if ("fail".equals(shipperTaskString.GetTaskStatus())) {
                shipperTask.add(shipperTaskString.GetTaskId());
            }
        }
        if (shipperTask.size() != 0) {
            RetryShipperTasksResponse retryShipperTasksResponse = client.RetryShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, shipperTask);
        }
        ListShipperResponse listShipperResponse = client.ListShipper(TEST_PROJECT, TEST_LOGSTORE);
        assertEquals(1, listShipperResponse.GetTotal());

        client.DeleteShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName);
    }

}
