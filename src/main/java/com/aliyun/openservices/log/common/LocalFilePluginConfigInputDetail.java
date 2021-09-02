package com.aliyun.openservices.log.common;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public class LocalFilePluginConfigInputDetail extends ConfigInputDetail {
    public LocalFilePluginConfigInputDetail() {
        super();
    }

    public LocalFilePluginConfigInputDetail(String pluginDetail) {
        super();
        SetPluginDetail(pluginDetail);
    }

    public LocalFilePluginConfigInputDetail(String logPath, String filePattern, String logType,
                                            String logBeginRegex, String regex, ArrayList<String> key,
                                            String timeFormat, boolean localStorage, String pluginDetail) {
        super(logPath, filePattern, logType, logBeginRegex, regex, key, timeFormat, localStorage);
        SetPluginDetail(pluginDetail);
    }

    public LocalFilePluginConfigInputDetail(String logPath, String filePattern, String logType,
                                            String logBeginRegex, String regex, ArrayList<String> key,
                                            String timeFormat, boolean localStorage, String customizedFields,
                                            String pluginDetail) {
        super(logPath, filePattern, logType, logBeginRegex, regex, key, timeFormat, localStorage, customizedFields);
        SetPluginDetail(pluginDetail);
    }

    public LocalFilePluginConfigInputDetail(ConfigInputDetail inputDetail, String pluginDetail) {
        super(inputDetail);
        SetPluginDetail(pluginDetail);
    }
}
