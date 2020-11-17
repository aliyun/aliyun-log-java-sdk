package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetAppliedConfigResponse extends Response {

    private static final long serialVersionUID = -9132526915584919104L;

    private List<String> configs;

    public GetAppliedConfigResponse(Map<String, String> headers, List<String> group) {
        super(headers);
        SetConfigs(group);
    }

    public List<String> Getconfigs() {
        return configs;
    }

    public void SetConfigs(List<String> configs) {
        this.configs = new ArrayList<String>(configs);
    }

    public int GetTotal() {
        return configs.size();
    }

}
