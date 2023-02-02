package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.CnameConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListProjectCnameResponse extends Response {

    private List<CnameConfiguration> cnameConfigurations;

    public ListProjectCnameResponse(Map<String, String> headers) {
        super(headers);
    }

    public List<CnameConfiguration> getCnameConfigurations() {
        return cnameConfigurations;
    }

    public void setCnameConfigurations(List<CnameConfiguration> cnameConfigurations) {
        this.cnameConfigurations = cnameConfigurations;
    }

    public void unmarshal(JSONArray marshalled) {
        int n = marshalled.size();
        cnameConfigurations = new ArrayList<CnameConfiguration>(n);
        for (int i = 0; i < n; ++i) {
            JSONObject item = marshalled.getJSONObject(i);
            CnameConfiguration cnameConfiguration = new CnameConfiguration();
            cnameConfiguration.unmarshal(item);
            cnameConfigurations.add(cnameConfiguration);
        }
    }
}
