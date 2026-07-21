package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public interface ShipperConfig {

	String GetShipperType();
	
	JSONObject GetJsonObj();
	
	void FromJsonObj(JSONObject obj) throws LogException;

}
