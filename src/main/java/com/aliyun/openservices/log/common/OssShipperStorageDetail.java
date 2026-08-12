package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public abstract class OssShipperStorageDetail implements JsonDeserializable {
	private String storageFormat = "";
	
	public String getStorageFormat() {
		return storageFormat;
	}
	public void setStorageFormat(String storageFormat) {
		this.storageFormat = storageFormat;
	}
	@InternalApi
	public abstract JSONObject toJsonObject();
	@InternalApi
	public abstract void fromJsonObject(JSONObject inputDetail) throws LogException;
}
