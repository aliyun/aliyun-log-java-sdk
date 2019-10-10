package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

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
	public JSONObject ToJsonObject() {
		JSONObject jsonObj = new JSONObject();
		CommonConfigToJsonObject(jsonObj);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_TAG, tag);
		return jsonObj;
	}

	@Override
	public void FromJsonObject(JSONObject inputDetail) throws LogException {
		try {
			CommonConfigFromJsonObject(inputDetail);
			this.tag = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_TAG);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(),
					e, "");
		}
	}
	
}
