package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public interface ShipperConfig {

	public String GetShipperType();
	
	public JSONObject GetJsonObj();
	
	public void FromJsonObj(JSONObject obj) throws LogException;

}
