package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ShipperTest extends JobIntgTest{
    private static final String TEST_LOGSTORE = "test-logstore";
    private static final String ossBucket = "audit-zyf-hangzhou";
    private static final String ossPrefix = "logtailalarm";
    private static final String aliUid = credentials.getAliuid();
    private static final String roleArn = "acs:ram::"+aliUid+":role/aliyunlogdefaultrole";
    private static final String compressType = "none";
    private static final String pathFormat = "%Y/%m/%d/%H";
    private static final int bufferInterval = 300;
    private static final int bufferSize = 10;

    @BeforeClass
    public static void prepareLogstore() {
        LogStore logStore = new LogStore();
        logStore.SetTtl(1);
        logStore.SetShardCount(1);
        logStore.SetLogStoreName(TEST_LOGSTORE);
        logStore.setEnableWebTracking(true);
        logStore.setAppendMeta(true);
        createOrUpdateLogStore(TEST_PROJECT, logStore);
    }

    @Test
    public void testCrud() throws LogException {
        createOdpsShipper();
        createJsonOssShipper();
        createCsvOssShipper();
        createParquetOssShipper();
    }

    private void createOdpsShipper() throws LogException {
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

        client.CreateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, shipConfig);

        GetShipperResponse res = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE,
                shipperName);
        assertEquals("odps",res.GetConfig().GetShipperType());

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logFieldsList.add("id");
        shipConfig = new OdpsShipperConfig(odpsEndPoint,
                odpsProject, odpsTable, logFieldsList, partitionColumn,
                partitionTimeFormat);
        client.UpdateShipper(TEST_PROJECT,TEST_LOGSTORE,shipperName,shipConfig);
        GetShipperResponse resUpdate = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE,
                shipperName);
        JSONObject odpsJson = resUpdate.GetConfig().GetJsonObj();
        System.out.println(odpsJson);
        List<String> fieldupdate = (List<String>) odpsJson.get("fields");
        assertEquals(3,fieldupdate.size());

        int startTime = (int) (System.currentTimeMillis() / 1000.0 - 7200);
        int endTime = (int) (System.currentTimeMillis() / 1000.0);
        List<String> shipperTask = new ArrayList<String>();
        GetShipperTasksResponse taskRes = client.GetShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, startTime, endTime, "", 0, 10);
        assertEquals(0,taskRes.GetTotalTask());
        for (ShipperTask shipperTaskString : taskRes.GetShipperTasks()) {
            if ("fail".equals(shipperTaskString.GetTaskStatus())){
                shipperTask.add(shipperTaskString.GetTaskId());
            }
        }
        if (shipperTask.size()!=0){
            RetryShipperTasksResponse retryShipperTasksResponse = client.RetryShipperTasks(TEST_PROJECT,TEST_LOGSTORE,shipperName,shipperTask);
        }

        ListShipperResponse listShipperResponse = client.ListShipper(TEST_PROJECT,TEST_LOGSTORE);
        assertEquals(1,listShipperResponse.GetTotal());

        client.DeleteShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName);
    }

    private static void createJsonOssShipper() throws LogException {
        String shipperName = "ossjsonshipper";
        OssShipperConfig ossConfig = new OssShipperConfig(ossBucket, ossPrefix, roleArn, bufferInterval, bufferSize, compressType, pathFormat, "json", "");
        OssShipperJsonStorageDetail detail = (OssShipperJsonStorageDetail) ossConfig.GetStorageDetail();
        detail.setEnableTag(false);
        client.CreateShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName, ossConfig);

        GetShipperResponse ossRes = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        assertEquals("oss",ossRes.GetConfig().GetShipperType());
        assertEquals(ossPrefix,ossRes.GetConfig().GetJsonObj().get("ossPrefix"));

        ossConfig = new OssShipperConfig(ossBucket,"updatePrefix",roleArn,bufferInterval,bufferSize,compressType,pathFormat,"json","");
        UpdateShipperResponse updateShipperResponse = client.UpdateShipper(TEST_PROJECT, TEST_LOGSTORE,shipperName,ossConfig);
        GetShipperResponse ossResUpdate = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        JSONObject ossJson = ossResUpdate.GetConfig().GetJsonObj();
        assertEquals("updatePrefix",ossJson.get("ossPrefix"));
        int startTime = (int) (System.currentTimeMillis() / 1000.0 - 7200);
        int endTime = (int) (System.currentTimeMillis() / 1000.0);
        List<String> shipperTask = new ArrayList<String>();
        GetShipperTasksResponse taskRes = client.GetShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, startTime, endTime, "", 0, 10);
        assertEquals(0,taskRes.GetTotalTask());
        for (ShipperTask shipperTaskString : taskRes.GetShipperTasks()) {
            if ("fail".equals(shipperTaskString.GetTaskStatus())){
                shipperTask.add(shipperTaskString.GetTaskId());
            }
        }
        if (shipperTask.size()!=0){
            RetryShipperTasksResponse retryShipperTasksResponse = client.RetryShipperTasks(TEST_PROJECT,TEST_LOGSTORE,shipperName,shipperTask);
        }
        ListShipperResponse listShipperResponse = client.ListShipper(TEST_PROJECT,TEST_LOGSTORE);
        assertEquals(1,listShipperResponse.GetTotal());

        client.DeleteShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName);
    }

    private static void createParquetOssShipper() throws LogException {
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
        assertEquals("oss",ossRes.GetConfig().GetShipperType());
        assertEquals(ossPrefix,ossRes.GetConfig().GetJsonObj().get("ossPrefix"));

        ossConfig = new OssShipperConfig(ossBucket,"updatePrefix",roleArn,bufferInterval,bufferSize,compressType,pathFormat,"parquet","");
        detail = (OssShipperParquetStorageDetail) ossConfig.GetStorageDetail();
        detail.setStorageColumns(columns);
        UpdateShipperResponse updateShipperResponse = client.UpdateShipper(TEST_PROJECT, TEST_LOGSTORE,shipperName,ossConfig);
        GetShipperResponse ossResUpdate = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        JSONObject ossJson = ossResUpdate.GetConfig().GetJsonObj();
        assertEquals("updatePrefix",ossJson.get("ossPrefix"));

        int startTime = (int) (System.currentTimeMillis() / 1000.0 - 7200);
        int endTime = (int) (System.currentTimeMillis() / 1000.0);
        List<String> shipperTask = new ArrayList<String>();
        GetShipperTasksResponse taskRes = client.GetShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, startTime, endTime, "", 0, 10);
        assertEquals(0,taskRes.GetTotalTask());
        for (ShipperTask shipperTaskString : taskRes.GetShipperTasks()) {
            if ("fail".equals(shipperTaskString.GetTaskStatus())){
                shipperTask.add(shipperTaskString.GetTaskId());
            }
        }
        if (shipperTask.size()!=0){
            RetryShipperTasksResponse retryShipperTasksResponse = client.RetryShipperTasks(TEST_PROJECT,TEST_LOGSTORE,shipperName,shipperTask);
        }
        ListShipperResponse listShipperResponse = client.ListShipper(TEST_PROJECT,TEST_LOGSTORE);
        assertEquals(1,listShipperResponse.GetTotal());

        client.DeleteShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName);
    }

    private static void createCsvOssShipper() throws LogException {
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
        assertEquals("oss",ossRes.GetConfig().GetShipperType());
        assertEquals(ossPrefix,ossRes.GetConfig().GetJsonObj().get("ossPrefix"));

        ossConfig = new OssShipperConfig(ossBucket,"updatePrefix",roleArn,bufferInterval,bufferSize,compressType,pathFormat,"csv","");
        detail = (OssShipperCsvStorageDetail) ossConfig.GetStorageDetail();
        columns.add("logstore");
        detail.setDelimiter(",");
        detail.setmStorageColumns(columns);
        detail.setQuote("\"");
        detail.setNullIdentifier("");
        detail.setHeader(false);
        UpdateShipperResponse updateShipperResponse = client.UpdateShipper(TEST_PROJECT, TEST_LOGSTORE,shipperName,ossConfig);
        detail.setDelimiter(",");
        detail.setmStorageColumns(columns);
        detail.setQuote("\"");
        detail.setNullIdentifier("");
        detail.setHeader(false);
        GetShipperResponse ossResUpdate = client.GetShipperConfig(TEST_PROJECT, TEST_LOGSTORE, shipperName);
        JSONObject ossJson = ossResUpdate.GetConfig().GetJsonObj();
        assertEquals("updatePrefix",ossJson.get("ossPrefix"));

        int startTime = (int) (System.currentTimeMillis() / 1000.0 - 7200);
        int endTime = (int) (System.currentTimeMillis() / 1000.0);
        List<String> shipperTask = new ArrayList<String>();
        GetShipperTasksResponse taskRes = client.GetShipperTasks(TEST_PROJECT, TEST_LOGSTORE, shipperName, startTime, endTime, "", 0, 10);
        assertEquals(0,taskRes.GetTotalTask());
        for (ShipperTask shipperTaskString : taskRes.GetShipperTasks()) {
            if ("fail".equals(shipperTaskString.GetTaskStatus())){
                shipperTask.add(shipperTaskString.GetTaskId());
            }
        }
        if (shipperTask.size()!=0){
            RetryShipperTasksResponse retryShipperTasksResponse = client.RetryShipperTasks(TEST_PROJECT,TEST_LOGSTORE,shipperName,shipperTask);
        }
        ListShipperResponse listShipperResponse = client.ListShipper(TEST_PROJECT,TEST_LOGSTORE);
        assertEquals(1,listShipperResponse.GetTotal());

        client.DeleteShipper(TEST_PROJECT, TEST_LOGSTORE, shipperName);
    }

}
