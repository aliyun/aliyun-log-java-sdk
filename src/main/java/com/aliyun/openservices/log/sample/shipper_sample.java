package com.aliyun.openservices.log.sample;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.OdpsShipperConfig;
import com.aliyun.openservices.log.common.ShipperConfig;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetShipperResponse;

public class shipper_sample {
	public static void main(String args[]) throws LogException {
		String accessId = "";
		String accessKey = "=";

		String project = "";
		String host = "";
		String logStore = "";

		/*
		 * 鏋勫缓涓�涓猚lient
		 */
		Client client = new Client(host, accessId, accessKey);

		String shipperName = "shipper_/odps";

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

	}
}
