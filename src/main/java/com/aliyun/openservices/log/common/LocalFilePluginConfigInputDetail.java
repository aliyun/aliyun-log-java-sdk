package com.aliyun.openservices.log.common;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

/**
 * Created by 冷倾(qingdao.pqd) on 2019/04/10
 *
 * @author <a href="mailto:qingdao.pqd@alibaba-inc.com">lengqing(kingdompan)</a>
 */
public class LocalFilePluginConfigInputDetail extends ConfigInputDetail {

    private String pluginDetail = "";

    public String getPluginDetail() {
        return pluginDetail;
    }

    public void setPluginDetail() {
        this.pluginDetail = pluginDetail;
    }

    public LocalFilePluginConfigInputDetail() {
        super();
    }

    public LocalFilePluginConfigInputDetail(String pluginDetail) {
        super();
        this.pluginDetail = pluginDetail;
    }

    public LocalFilePluginConfigInputDetail(String logPath, String filePattern, String logType,
                                            String logBeginRegex, String regex, ArrayList<String> key,
                                            String timeFormat, boolean localStorage, String pluginDetail) {
        super(logPath, filePattern, logType, logBeginRegex, regex, key, timeFormat, localStorage);
        this.pluginDetail = pluginDetail;
    }

    public LocalFilePluginConfigInputDetail(String logPath, String filePattern, String logType,
                                            String logBeginRegex, String regex, ArrayList<String> key,
                                            String timeFormat, boolean localStorage, String customizedFields,
                                            String pluginDetail) {
        super(logPath, filePattern, logType, logBeginRegex, regex, key, timeFormat, localStorage, customizedFields);
        this.pluginDetail = pluginDetail;
    }

    public LocalFilePluginConfigInputDetail(ConfigInputDetail inputDetail, String pluginDetail) {
        super(inputDetail);
        this.pluginDetail = pluginDetail;
    }

    @Override
    public JSONObject ToJsonObject() {
        JSONObject jsonObj = super.ToJsonObject();
        JSONObject pluginObject = JSONObject.parseObject(pluginDetail);
        jsonObj.put("plugin", pluginObject);
        return jsonObj;
    }

    @Override
    public void FromJsonObject(JSONObject inputDetail) throws LogException {
        super.FromJsonObject(inputDetail);
        JSONObject exist = inputDetail.getJSONObject("plugin");
        this.pluginDetail = exist == null ? "" : exist.toString();
    }
}
