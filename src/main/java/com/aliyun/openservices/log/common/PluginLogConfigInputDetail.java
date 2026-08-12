package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.annotation.InternalApi;


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
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        commonConfigToJsonObject(jsonObj);
        JSONObject pluginObject = JSONObject.parseObject(pluginDetail);
        jsonObj.put("plugin", pluginObject);
        if (advanced != null) {
            jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED, advanced.toJsonObject());
        }
        return jsonObj;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject inputDetail) throws LogException {
        if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED)) {
            this.advanced = Advanced.fromJsonObject(inputDetail.getJSONObject(Consts.CONST_CONFIG_INPUTDETAIL_ADVANCED));
        }
        commonConfigFromJsonObject(inputDetail);
        this.pluginDetail = inputDetail.getJSONObject("plugin").toString();
    }
}
