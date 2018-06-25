package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.OssShipperConfig;
import com.aliyun.openservices.log.common.OssShipperCsvStorageDetail;
import com.aliyun.openservices.log.common.ShipperConfig;
import com.aliyun.openservices.log.common.ShipperTask;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetShipperResponse;
import com.aliyun.openservices.log.response.GetShipperTasksResponse;

import java.util.ArrayList;

public class ShipperSample {

	public static void main(String args[]) throws LogException {
		String accessId = "";
		String accessKey = "";

		//String project = "ali-cn-devcommon-sls-admin";
		//String host = "cn-hangzhou-devcommon-intranet.sls.aliyuncs.com";
		String project = "ali-cn-hangzhou-stg-sls-admin";
		String host = "cn-hangzhou-staging-intranet.sls.aliyuncs.com";
		String logStore = "logtail_alarm";

		/*
		 * 初始化client
		 */
		Client client = new Client(host, accessId, accessKey);

		/*String shipperName = "odpsshipper";

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
		System.out.println(res.GetConfig().GetJsonObj());*/

		String ossShipper = "logtail-alarm-shipper";
		//client.DeleteShipper(project, logStore, ossShipper);

		String ossBucket = "sls-test-oss-shipper";
		String ossPrefix = "logtailalarm";
		String roleArn = "acs:ram::1654218965343050:role/aliyunlogdefaultrole";
		String compressType = "none";
		String pathFormat = "%Y/%m/%d/%H";
		int bufferInterval = 300;
		int bufferSize = 10;
		
		// parquet sample
		/*ShipperConfig ossConfig = new OssShipperConfig(ossBucket, ossPrefix, roleArn, bufferInterval, bufferSize, compressType, pathFormat, "parquet");
		ArrayList<OssShipperStorageColumn> columns = new ArrayList<OssShipperStorageColumn>();
		columns.add(new OssShipperStorageColumn("MachineName", "string"));
		columns.add(new OssShipperStorageColumn("Role", "string"));
		columns.add(new OssShipperStorageColumn("ResValue", "int32"));
		columns.add(new OssShipperStorageColumn("__LINE__", "int64"));
		columns.add(new OssShipperStorageColumn("__THREAD__", "double"));
		OssShipperParquetStorageDetail detail = (OssShipperParquetStorageDetail) ((OssShipperConfig)ossConfig).GetStorageDetail();
		detail.setmStorageColumns(columns);*/
		
		// csv sample
		ShipperConfig ossConfig = new OssShipperConfig(ossBucket, ossPrefix, roleArn, bufferInterval, bufferSize, compressType, pathFormat, "csv");
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("__topic__");
		columns.add("alarm_count");
		columns.add("alarm_message");
		columns.add("alarm_type");
		columns.add("category");
		columns.add("project_name");
		OssShipperCsvStorageDetail detail = (OssShipperCsvStorageDetail) ((OssShipperConfig)ossConfig).GetStorageDetail();
		detail.setDelimiter(",");
		detail.setmStorageColumns(columns);
		detail.setQuote("\"");
		detail.setNullIdentifier("");
		detail.setHeader(false);
		
		// client.CreateShipper(project, logStore, ossShipper, ossConfig);
		
		GetShipperResponse ossRes = client.GetShipperConfig(project, logStore,
				ossShipper);
		System.out.println(ossRes.GetConfig().GetShipperType());
		System.out.println(ossRes.GetConfig().GetJsonObj());
		
		int startTime = (int)(System.currentTimeMillis()/1000.0 - 7200);
		int endTime = (int)(System.currentTimeMillis()/1000.0);
		GetShipperTasksResponse taskRes = client.GetShipperTasks(project, logStore, "logtail_alarm_offline", startTime, endTime, "", 0, 10);
		System.out.println(taskRes.GetTotalTask());
		for (ShipperTask shipperTask: taskRes.GetShipperTasks()) {
			System.out.println(shipperTask.GetTaskCreateTime());
			System.out.println(shipperTask.GetTaskFinishTime());
			System.out.println(shipperTask.GetTaskDataLines());
		}
	}
}
