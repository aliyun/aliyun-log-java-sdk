package com.aliyun.openservices.log.common;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public class OdpsShipperConfig implements ShipperConfig {

	private String mOdpsEndPoint;
	private String mOdpsProject;
	private String mOdpsTable;
	private List<String> mLogFieldsList;
	private List<String> mPartitionColumn;
	private String mPartitionTimeFormat;
	private int mBufferInterval;

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
		mOdpsEndPoint = odpsEndPoint;
		mOdpsProject = odpsProject;
		mOdpsTable = odpsTable;
		mLogFieldsList = logFieldsList;
		mPartitionColumn = partitionColumn;
		mPartitionTimeFormat = partitionTimeFormat;
		mBufferInterval = 1800;
	}

	public OdpsShipperConfig() {

	}

	public String GetOdpsEndPoint() {
		return mOdpsEndPoint;
	}

	public String GetOdpsProject() {
		return mOdpsProject;
	}

	public String GetOdpsTable() {
		return mOdpsTable;
	}

	public List<String> GetLogFieldsList() {
		return new ArrayList<String>(mLogFieldsList);
	}

	public List<String> GetPartitionColumn() {
		return new ArrayList<String>(mPartitionColumn);
	}

	public String GetPartitionTimeFromat() {
		return mPartitionTimeFormat;
	}

	public String GetShipperType() {
		return "odps";
	}

	public void setOdpsEndPoint(String odpsEndPoint) {
		this.mOdpsEndPoint = odpsEndPoint;
	}

	public void setOdpsProject(String odpsProject) {
		this.mOdpsProject = odpsProject;
	}

	public void setOdpsTable(String odpsTable) {
		this.mOdpsTable = odpsTable;
	}

	public void setLogFieldsList(List<String> logFieldsList) {
		this.mLogFieldsList = logFieldsList;
	}

	public void setPartitionColumn(List<String> partitionColumn) {
		this.mPartitionColumn = partitionColumn;
	}

	public void setPartitionTimeFormat(String partitionTimeFormat) {
		this.mPartitionTimeFormat = partitionTimeFormat;
	}

	public void setBufferInterval(int mBufferInterval) {
		this.mBufferInterval = mBufferInterval;
	}

	public JSONObject GetJsonObj() {
		JSONObject obj = new JSONObject();
		obj.put("odpsEndpoint", this.mOdpsEndPoint);
		obj.put("odpsProject", this.mOdpsProject);
		obj.put("odpsTable", this.mOdpsTable);
		obj.put("fields", this.mLogFieldsList);
		obj.put("partitionColumn", this.mPartitionColumn);
		obj.put("partitionTimeFormat", this.mPartitionTimeFormat);
		obj.put("bufferInterval", mBufferInterval);
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
			this.mOdpsEndPoint = obj.getString("odpsEndpoint");
			this.mOdpsProject = obj.getString("odpsProject");
			this.mOdpsTable = obj.getString("odpsTable");
			this.mLogFieldsList = FromJsonArray(obj.getJSONArray("fields"));
			this.mPartitionColumn = FromJsonArray(obj.getJSONArray("partitionColumn"));
			this.mPartitionTimeFormat = obj.getString("partitionTimeFormat");
			this.mBufferInterval = obj.getInt("bufferInterval");
		} catch (JSONException e) {
			throw new LogException("FailToParseOssShipperConfig",
					e.getMessage(), e, "");
		}
	}

}
