package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public interface ShipperConfig extends JsonSerializable, JsonDeserializable {

	@InternalApi
	static ShipperConfig fromJsonObject(String targetType, JSONObject value) throws LogException {
		ShipperConfig config;
		if ("odps".equals(targetType)) {
			config = new OdpsShipperConfig();
		} else if ("oss".equals(targetType)) {
			config = new OssShipperConfig();
		} else {
			throw new LogException("InvalidShipperType", "Unsupported shipper type: " + targetType, "");
		}
		config.fromJsonObject(value);
		return config;
	}

	String getShipperType();

	@InternalApi
	JSONObject toJsonObject() throws LogException;

	default void fromJsonString(String shipperConfigString) throws LogException {
		try {
			fromJsonObject(JSONObject.parseObject(shipperConfigString));
		} catch (JSONException e) {
			throw new LogException("FailToParseShipperConfig", e.getMessage(), e, "");
		}
	}

}
