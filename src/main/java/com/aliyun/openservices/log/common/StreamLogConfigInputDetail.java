package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.annotation.InternalApi;

public class StreamLogConfigInputDetail extends CommonConfigInputDetail {
	private String tag = "";
	
	public String GetTag() {
		return tag;
	}
	public void SetTag(String tag) {
		this.tag = tag;
	}
	
	public StreamLogConfigInputDetail() {
	}
	
	public StreamLogConfigInputDetail(final String tag) {
		this.tag = tag;
	}
	
	@Override
	@InternalApi
	public JSONObject toJsonObject() {
		JSONObject jsonObj = new JSONObject();
		commonConfigToJsonObject(jsonObj);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_TAG, tag);
		return jsonObj;
	}

	@Override
	@InternalApi
	public void fromJsonObject(JSONObject inputDetail) throws LogException {
		try {
			commonConfigFromJsonObject(inputDetail);
			this.tag = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_TAG);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(),
					e, "");
		}
	}
	
}
