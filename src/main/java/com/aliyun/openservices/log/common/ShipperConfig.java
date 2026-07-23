package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public interface ShipperConfig {

	String GetShipperType();

	@InternalApi
	JSONObject GetJsonObj();

	@InternalApi
	void FromJsonObj(JSONObject obj) throws LogException;

}
