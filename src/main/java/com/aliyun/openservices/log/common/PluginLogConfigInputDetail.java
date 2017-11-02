package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONObject;

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
		JSONObject pluginObject = JSONObject.fromObject(pluginDetail);
		jsonObj.put("plugin", pluginObject);
		return jsonObj;
	}

	@Override
	public void FromJsonObject(JSONObject inputDetail) throws LogException {
		CommonConfigToJsonObject(inputDetail);
		this.pluginDetail = inputDetail.getJSONObject("plugin").toString();
	}
}
