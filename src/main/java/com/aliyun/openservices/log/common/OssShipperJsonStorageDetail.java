package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class OssShipperJsonStorageDetail extends OssShipperStorageDetail implements Serializable {
	private static final long serialVersionUID = -7191203366698052140L;

	OssShipperJsonStorageDetail() {
		setmStorageFormat("json");
	}
	
	@Override
	public JSONObject ToJsonObject() {
		JSONObject obj = new JSONObject();
		return obj;
	}

	@Override
	public void FromJsonObject(JSONObject storageDetail) throws LogException {
	}
}
