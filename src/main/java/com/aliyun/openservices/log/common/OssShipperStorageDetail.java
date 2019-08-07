package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONObject;

public abstract class OssShipperStorageDetail {
	private String storageFormat = "";
	
	public String getStorageFormat() {
		return storageFormat;
	}
	public void setStorageFormat(String storageFormat) {
		this.storageFormat = storageFormat;
	}
	public abstract JSONObject ToJsonObject();
	public abstract void FromJsonObject(JSONObject inputDetail) throws LogException;
}