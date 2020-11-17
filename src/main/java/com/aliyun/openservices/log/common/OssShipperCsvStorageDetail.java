package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.aliyun.openservices.log.exception.LogException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class OssShipperCsvStorageDetail extends OssShipperStorageDetail implements Serializable{
	private static final long serialVersionUID = -9072422584563361211L;
	private String delimiter = ",";
	private String quote = "";
	private String lineFeed = "\n";
	private String nullIdentifier = "";
	private boolean header = false;
	private ArrayList<String> mStorageColumns = new ArrayList<String>();
	
	OssShipperCsvStorageDetail() {
		setStorageFormat("csv");
	}
	
	public ArrayList<String> getmStorageColumns() {
		return mStorageColumns;
	}

	public void setmStorageColumns(ArrayList<String> mStorageColumns) {
		this.mStorageColumns = new ArrayList<String>(mStorageColumns);
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getNullIdentifier() {
		return nullIdentifier;
	}

	public void setNullIdentifier(String nullIdentifier) {
		this.nullIdentifier = nullIdentifier;
	}

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

	public String getLineFeed() {
		return lineFeed;
	}

	public void setLineFeed(String lineFeed) {
		this.lineFeed = lineFeed;
	}

	@Override
	public JSONObject ToJsonObject() {
		JSONObject obj = new JSONObject();
		JSONArray columns = new JSONArray();
		columns.addAll(this.mStorageColumns);
		JSONObject detail = new JSONObject();
		detail.put("columns", columns);
		detail.put("delimiter", this.delimiter);
		detail.put("quote", this.quote);
		detail.put("lineFeed", this.lineFeed);
		detail.put("nullIdentifier", this.nullIdentifier);
		detail.put("header", this.header);
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
			ArrayList<String> column = new ArrayList<String>();
			for (int index = 0; index < columns.size(); index++) {
				column.add(columns.getString(index));
			}
			setmStorageColumns(column);
			setDelimiter(detail.getString("delimiter"));
			setQuote(detail.getString("quote"));
			setLineFeed(detail.containsKey("lineFeed") ? detail.getString("lineFeed") : "\n");
			setNullIdentifier(detail.getString("nullIdentifier"));
			setHeader(detail.getBoolean("header"));
		} catch (JSONException ex) {
			throw new LogException("FailToParseOssShipperCsvStorageDetail",
					ex.getMessage(), ex, "");
		}
	}
}
