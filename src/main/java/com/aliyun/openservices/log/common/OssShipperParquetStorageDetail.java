package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.aliyun.openservices.log.exception.LogException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class OssShipperParquetStorageDetail extends OssShipperStorageDetail implements Serializable {
	private static final long serialVersionUID = 1407883828765925579L;
	private ArrayList<OssShipperStorageColumn> storageColumns = new ArrayList<OssShipperStorageColumn>();

	OssShipperParquetStorageDetail() {
		setStorageFormat("parquet");
	}

	public ArrayList<OssShipperStorageColumn> getStorageColumns() {
		return storageColumns;
	}

	public void setStorageColumns(ArrayList<OssShipperStorageColumn> storageColumns) {
		this.storageColumns = new ArrayList<OssShipperStorageColumn>(storageColumns);
	}

	@Override
	public JSONObject ToJsonObject() {
		JSONObject obj = new JSONObject();
		JSONArray columns = new JSONArray();
		for (OssShipperStorageColumn column: this.storageColumns) {
			columns.add(column.ToJsonObject());
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
			ArrayList<OssShipperStorageColumn> storageColumns = new ArrayList<OssShipperStorageColumn>();
			if (columns != null) {
				for (int index = 0; index < columns.size(); index++) {
					JSONObject colAsJson = columns.getJSONObject(index);
					if (colAsJson == null) {
						continue;
					}
					storageColumns.add(new OssShipperStorageColumn(colAsJson.getString("name"), colAsJson.getString("type")));
				}
			}
			setStorageColumns(storageColumns);
		} catch (JSONException ex) {
			throw new LogException("FailToParseOssShipperParquetStorageDetail",
					ex.getMessage(), ex, "");
		}
	}
}
