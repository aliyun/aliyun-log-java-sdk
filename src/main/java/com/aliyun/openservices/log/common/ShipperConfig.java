package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public interface ShipperConfig {

	String GetShipperType();

	@InternalApi
	JSONObject GetJsonObj();

	@InternalApi
	void FromJsonObj(JSONObject obj) throws LogException;

	default void FromJsonString(String shipperConfigString) throws LogException {
		try {
			FromJsonObj(JSONObject.parseObject(shipperConfigString));
		} catch (JSONException e) {
			throw new LogException("FailToParseShipperConfig", e.getMessage(), e, "");
		}
	}

}
