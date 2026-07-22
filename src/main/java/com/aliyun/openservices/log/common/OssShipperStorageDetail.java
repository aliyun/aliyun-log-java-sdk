package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public abstract class OssShipperStorageDetail {
	private String storageFormat = "";
	
	public String getStorageFormat() {
		return storageFormat;
	}
	public void setStorageFormat(String storageFormat) {
		this.storageFormat = storageFormat;
	}
	@InternalApi
	public abstract JSONObject ToJsonObject();
	@InternalApi
	public abstract void FromJsonObject(JSONObject inputDetail) throws LogException;
}