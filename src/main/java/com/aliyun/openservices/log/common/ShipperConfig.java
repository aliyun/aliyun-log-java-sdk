package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public interface ShipperConfig {

	String GetShipperType();
	
	JSONObject GetJsonObj();
	
	void FromJsonObj(JSONObject obj) throws LogException;

}
