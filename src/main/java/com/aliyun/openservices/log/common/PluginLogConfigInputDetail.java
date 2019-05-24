package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;


public class PluginLogConfigInputDetail extends CommonConfigInputDetail {

	private String pluginDetail = "";
	
	public String getPluginDetail() {
		return pluginDetail;
	}

	public void setPluginDetail(String pluginDetail) {
		this.pluginDetail = pluginDetail;
	}
	
	@Override
	public JSONObject ToJsonObject() {
		JSONObject jsonObj = new JSONObject();
		CommonConfigToJsonObject(jsonObj);
		JSONObject pluginObject = JSONObject.parseObject(pluginDetail);
		jsonObj.put("plugin", pluginObject);
		return jsonObj;
	}

	@Override
	public void FromJsonObject(JSONObject inputDetail) throws LogException {
		CommonConfigFromJsonObject(inputDetail);
		this.pluginDetail = inputDetail.getJSONObject("plugin").toString();
	}
}
