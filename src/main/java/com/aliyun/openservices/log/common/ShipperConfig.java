package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.annotation.ProtectedApi;
import com.aliyun.openservices.log.internal.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public interface ShipperConfig {

	String GetShipperType();

	@ProtectedApi
	JSONObject GetJsonObj();

	@ProtectedApi
	void FromJsonObj(JSONObject obj) throws LogException;

}
