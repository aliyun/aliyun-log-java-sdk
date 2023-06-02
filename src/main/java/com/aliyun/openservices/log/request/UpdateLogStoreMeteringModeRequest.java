package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.util.Args;

public class UpdateLogStoreMeteringModeRequest extends Request {

    private String logStore;
    private String meteringMode;

    public UpdateLogStoreMeteringModeRequest(String project, String logStore, String meteringMode) {
        super(project);
        Args.notNullOrEmpty(logStore, "logStore");
        Args.notNullOrEmpty(meteringMode, "meteringMode");
        this.logStore = logStore;
        this.meteringMode = meteringMode;
    }

    public String getLogStore() {
        return logStore;
    }

    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }

    public String getMeteringMode() {
        return meteringMode;
    }

    public void setMeteringMode(String meteringMode) {
        this.meteringMode = meteringMode;
    }

    public String getRequestBody() {
        JSONObject body = new JSONObject();
        body.put("meteringMode", meteringMode);
        return body.toString();
    }
}
