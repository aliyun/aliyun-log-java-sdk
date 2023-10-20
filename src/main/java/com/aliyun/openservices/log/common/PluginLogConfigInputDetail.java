package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;


public class PluginLogConfigInputDetail extends CommonConfigInputDetail {

    private String pluginDetail = "";
    private Advanced advanced;

    public String getPluginDetail() {
        return pluginDetail;
    }

    public void setPluginDetail(String pluginDetail) {
        this.pluginDetail = pluginDetail;
    }

    public Advanced getAdvanced() {
        return advanced;
    }

    public void setAdvanced(Advanced advanced) {
        this.advanced = advanced;
    }

    @Override
    public JSONObject ToJsonObject() {
        JSONObject jsonObj = new JSONObject();
        CommonConfigToJsonObject(jsonObj);
        JSONObject pluginObject = JSONObject.parseObject(pluginDetail);
        jsonObj.put("plugin", pluginObject);
        if (advanced != null) {
            jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED, advanced.toJsonObject());
        }
        return jsonObj;
    }

    @Override
    public void FromJsonObject(JSONObject inputDetail) throws LogException {
        if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED)) {
            this.advanced = Advanced.fromJsonObject(inputDetail.getJSONObject(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED));
        }
        CommonConfigFromJsonObject(inputDetail);
        this.pluginDetail = inputDetail.getJSONObject("plugin").toString();
    }
}
