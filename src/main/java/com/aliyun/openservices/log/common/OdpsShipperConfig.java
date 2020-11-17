package com.aliyun.openservices.log.common;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public class OdpsShipperConfig implements ShipperConfig {

	private String odpsEndPoint;
	private String odpsProject;
	private String odpsTable;
	private List<String> logFieldsList;
	private List<String> partitionColumn;
	private String partitionTimeFormat;
	private int bufferInterval;

	/**
	 * Create a odps shipper config
	 * 
	 * @param odpsEndPoint
	 *            odps endpoint
	 * @param odpsProject
	 *            odps project name
	 * @param odpsTable
	 *            odps table name
	 * @param logFieldsList
	 *            the log key lists, used to mapping to the odps table column
	 * @param partitionColumn
	 *            the log key lists, used to mapping to odps partition column
	 * @param partitionTimeFormat
	 *            the odps partition time format , e.g "yyyy_MM_dd_HH"
	 */
	public OdpsShipperConfig(String odpsEndPoint, String odpsProject,
			String odpsTable, List<String> logFieldsList,
			List<String> partitionColumn, String partitionTimeFormat) {
		this.odpsEndPoint = odpsEndPoint;
		this.odpsProject = odpsProject;
		this.odpsTable = odpsTable;
		this.logFieldsList = logFieldsList;
		this.partitionColumn = partitionColumn;
		this.partitionTimeFormat = partitionTimeFormat;
		this.bufferInterval = 1800;
	}

	public OdpsShipperConfig() {

	}

	public String GetOdpsEndPoint() {
		return odpsEndPoint;
	}

	public String GetOdpsProject() {
		return odpsProject;
	}

	public String GetOdpsTable() {
		return odpsTable;
	}

	public List<String> GetLogFieldsList() {
		return new ArrayList<String>(logFieldsList);
	}

	public List<String> GetPartitionColumn() {
		return new ArrayList<String>(partitionColumn);
	}

	public String GetPartitionTimeFromat() {
		return partitionTimeFormat;
	}

	public String GetShipperType() {
		return "odps";
	}

	public void setOdpsEndPoint(String odpsEndPoint) {
		this.odpsEndPoint = odpsEndPoint;
	}

	public void setOdpsProject(String odpsProject) {
		this.odpsProject = odpsProject;
	}

	public void setOdpsTable(String odpsTable) {
		this.odpsTable = odpsTable;
	}

	public void setLogFieldsList(List<String> logFieldsList) {
		this.logFieldsList = logFieldsList;
	}

	public void setPartitionColumn(List<String> partitionColumn) {
		this.partitionColumn = partitionColumn;
	}

	public void setPartitionTimeFormat(String partitionTimeFormat) {
		this.partitionTimeFormat = partitionTimeFormat;
	}

	public void setBufferInterval(int bufferInterval) {
		this.bufferInterval = bufferInterval;
	}

	public JSONObject GetJsonObj() {
		JSONObject obj = new JSONObject();
		obj.put("odpsEndpoint", this.odpsEndPoint);
		obj.put("odpsProject", this.odpsProject);
		obj.put("odpsTable", this.odpsTable);
		obj.put("fields", this.logFieldsList);
		obj.put("partitionColumn", this.partitionColumn);
		obj.put("partitionTimeFormat", this.partitionTimeFormat);
		obj.put("bufferInterval", this.bufferInterval);
		return obj;
	}

	private List<String> FromJsonArray(JSONArray jsonArray) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(jsonArray.getString(i));
		}
		return list;
	}

	public void FromJsonObj(JSONObject obj) throws LogException {
		try {
			this.odpsEndPoint = obj.getString("odpsEndpoint");
			this.odpsProject = obj.getString("odpsProject");
			this.odpsTable = obj.getString("odpsTable");
			this.logFieldsList = FromJsonArray(obj.getJSONArray("fields"));
			this.partitionColumn = FromJsonArray(obj.getJSONArray("partitionColumn"));
			this.partitionTimeFormat = obj.getString("partitionTimeFormat");
			this.bufferInterval = obj.getIntValue("bufferInterval");
		} catch (JSONException e) {
			throw new LogException("FailToParseOssShipperConfig",
					e.getMessage(), e, "");
		}
	}

}
