package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class OssShipperParquetStorageDetail extends OssShipperStorageDetail implements Serializable {
	private static final long serialVersionUID = 1407883828765925579L;
	private ArrayList<OssShipperStorageColumn> mStorageColumns = new ArrayList<OssShipperStorageColumn>();

	OssShipperParquetStorageDetail() {
		setStorageFormat("parquet");
	}
	
	public ArrayList<OssShipperStorageColumn> getmStorageColumns() {
		return mStorageColumns;
	}

	public void setmStorageColumns(ArrayList<OssShipperStorageColumn> mStorageColumns) {
		this.mStorageColumns = new ArrayList<OssShipperStorageColumn>(mStorageColumns);
	}

	@Override
	public JSONObject ToJsonObject() {
		JSONObject obj = new JSONObject();
		JSONArray columns = new JSONArray();
		for (int index = 0; index < this.mStorageColumns.size(); index++) {
			JSONObject column = new JSONObject();
			column.put("name", this.mStorageColumns.get(index).getName());
			column.put("type", this.mStorageColumns.get(index).getType());
			columns.add(column);
		}
		JSONObject detail = new JSONObject();
		detail.put("columns", columns);
		JSONObject storage = new JSONObject();
		storage.put("detail", detail);
		storage.put("format", getStorageFormat());
		obj.put("storage", storage);
		return obj;
	}
	
	@Override
	public void FromJsonObject(JSONObject storageDetail) throws LogException {
		try {
			JSONObject storage = storageDetail.getJSONObject("storage");
			setStorageFormat(storage.getString("format"));
			JSONObject detail = storage.getJSONObject("detail");
			JSONArray columns = detail.getJSONArray("columns");
			ArrayList<OssShipperStorageColumn> column = new ArrayList<OssShipperStorageColumn>();
			for (int index = 0; index < columns.size(); index++) {
				column.add(new OssShipperStorageColumn(columns.getJSONObject(index).getString("name"), columns.getJSONObject(index).getString("type")));
			}
			setmStorageColumns(column);
		} catch (JSONException ex) {
			throw new LogException("FailToParseOssShipperParquetStorageDetail",
					ex.getMessage(), ex, "");
		}
	}
}
