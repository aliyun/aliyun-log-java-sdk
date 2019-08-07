package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetShipperResponse;
import com.aliyun.openservices.log.response.GetShipperTasksResponse;

import java.util.ArrayList;
import java.util.List;

public class ShipperSample {

    private static final String accessKeyId = "";
    private static final String accessKeySecret = "";
    private static final String host = "cn-shanghai-staging-share.sls.aliyuncs.com";
    private static final String project = "project-for-test-1";
    private static final String logStore = "access_log";
    private static final Client client = new Client(host, accessKeyId, accessKeySecret);
    private static final String ossBucket = "sls-test-oss-shipper";
    private static final String ossPrefix = "logtailalarm";
    private static final String roleArn = "acs:ram::1654218965343050:role/aliyunlogdefaultrole";
    private static final String compressType = "none";
    private static final String pathFormat = "%Y/%m/%d/%H";
    private static final int bufferInterval = 300;
    private static final int bufferSize = 10;

    public static void createOdpsShipper() throws LogException {
        String shipperName = "odpsshipper";
        String odpsEndPoint = "";
        String odpsProject = "odps_project";
        String odpsTable = "odps_table";
        List<String> logFieldsList = new ArrayList<String>();
        logFieldsList.add("__topic__");
        logFieldsList.add("__time__");
        logFieldsList.add("a");
        logFieldsList.add("b");
        logFieldsList.add("b");

        List<String> partitionColumn = new ArrayList<String>();
        partitionColumn.add("p_a");
        partitionColumn.add("__PARTITION_TIME__");
        String partitionTimeFormat = "yyyy_MM_dd_HH_mm";

        ShipperConfig shipConfig = new OdpsShipperConfig(odpsEndPoint,
                odpsProject, odpsTable, logFieldsList, partitionColumn,
                partitionTimeFormat);

        client.CreateShipper(project, logStore, shipperName, shipConfig);

        GetShipperResponse res = client.GetShipperConfig(project, logStore,
                shipperName);
        System.out.println(res.GetConfig().GetShipperType());
        System.out.println(res.GetConfig().GetJsonObj());

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int startTime = (int) (System.currentTimeMillis() / 1000.0 - 7200);
        int endTime = (int) (System.currentTimeMillis() / 1000.0);
        GetShipperTasksResponse taskRes = client.GetShipperTasks(project, logStore, shipperName, startTime, endTime, "", 0, 10);
        System.out.println(taskRes.GetTotalTask());
        for (ShipperTask shipperTask : taskRes.GetShipperTasks()) {
            System.out.println(shipperTask.GetTaskCreateTime());
            System.out.println(shipperTask.GetTaskFinishTime());
            System.out.println(shipperTask.GetTaskDataLines());
        }

        client.DeleteShipper(project, logStore, shipperName);
    }

    public static void createJsonOssShipper() throws LogException {
        String shipperName = "ossjsonshipper";
        ShipperConfig ossConfig = new OssShipperConfig(ossBucket, ossPrefix, roleArn, bufferInterval, bufferSize, compressType, pathFormat, "json");
		OssShipperJsonStorageDetail detail = (OssShipperJsonStorageDetail) ((OssShipperConfig) ossConfig).GetStorageDetail();
		detail.setEnableTag(false);
		client.CreateShipper(project, logStore, shipperName, ossConfig);

		GetShipperResponse ossRes = client.GetShipperConfig(project, logStore, shipperName);
        System.out.println(ossRes.GetConfig().GetShipperType());
        System.out.println(ossRes.GetConfig().GetJsonObj());

        client.DeleteShipper(project, logStore, shipperName);
    }

    public static void createParquetOssShipper() throws LogException {
        String shipperName = "ossparquetshipper";
        ShipperConfig ossConfig = new OssShipperConfig(ossBucket, ossPrefix, roleArn, bufferInterval, bufferSize, compressType, pathFormat, "parquet");
        ArrayList<OssShipperStorageColumn> columns = new ArrayList<OssShipperStorageColumn>();
        columns.add(new OssShipperStorageColumn("MachineName", "string"));
        columns.add(new OssShipperStorageColumn("Role", "string"));
        columns.add(new OssShipperStorageColumn("ResValue", "int32"));
        columns.add(new OssShipperStorageColumn("__LINE__", "int64"));
        columns.add(new OssShipperStorageColumn("__THREAD__", "double"));
        OssShipperParquetStorageDetail detail = (OssShipperParquetStorageDetail) ((OssShipperConfig) ossConfig).GetStorageDetail();
        detail.setmStorageColumns(columns);
        client.CreateShipper(project, logStore, shipperName, ossConfig);

        GetShipperResponse ossRes = client.GetShipperConfig(project, logStore, shipperName);
        System.out.println(ossRes.GetConfig().GetShipperType());
        System.out.println(ossRes.GetConfig().GetJsonObj());

        client.DeleteShipper(project, logStore, shipperName);
    }

    public static void createCsvOssShipper() throws LogException {
        String shipperName = "osscsvshipper";
        ShipperConfig ossConfig = new OssShipperConfig(ossBucket, ossPrefix, roleArn, bufferInterval, bufferSize, compressType, pathFormat, "csv");
        ArrayList<String> columns = new ArrayList<String>();
        columns.add("__topic__");
        columns.add("alarm_count");
        columns.add("alarm_message");
        columns.add("alarm_type");
        columns.add("category");
        columns.add("project_name");
        OssShipperCsvStorageDetail detail = (OssShipperCsvStorageDetail) ((OssShipperConfig) ossConfig).GetStorageDetail();
        detail.setDelimiter(",");
        detail.setmStorageColumns(columns);
        detail.setQuote("\"");
        detail.setNullIdentifier("");
        detail.setHeader(false);
        client.CreateShipper(project, logStore, shipperName, ossConfig);

        GetShipperResponse ossRes = client.GetShipperConfig(project, logStore, shipperName);
        System.out.println(ossRes.GetConfig().GetShipperType());
        System.out.println(ossRes.GetConfig().GetJsonObj());

        client.DeleteShipper(project, logStore, shipperName);
    }

    public static void main(String args[]) throws LogException {

        createOdpsShipper();
        createCsvOssShipper();
        createParquetOssShipper();
        createJsonOssShipper();
    }
}
