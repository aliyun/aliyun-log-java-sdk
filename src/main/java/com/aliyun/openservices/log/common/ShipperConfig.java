package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public interface ShipperConfig {

	String GetShipperType();
	
	JSONObject GetJsonObj();
	
	void FromJsonObj(JSONObject obj) throws LogException;

}
