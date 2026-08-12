package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class LinkStore implements Serializable {
    private static final long serialVersionUID = -4480238526625142008L;

    private String linkStoreName;
    private String sourceProjectName;
    private String sourceLogStoreName;

    public LinkStore(String linkStoreName, String sourceProjectName, String sourceLogStoreName) {
        this.linkStoreName = linkStoreName;
        this.sourceProjectName = sourceProjectName;
        this.sourceLogStoreName = sourceLogStoreName;
    }

    public LinkStore(LinkStore linkStore){
        this.linkStoreName = linkStore.getLinkStoreName();
        this.sourceProjectName = linkStore.getSourceProjectName();
        this.sourceLogStoreName = linkStore.getSourceLogStoreName();
    }

    public String getLinkStoreName() {
        return linkStoreName;
    }

    public void setLinkStoreName(String linkStoreName) {
        this.linkStoreName = linkStoreName;
    }

    public String getSourceProjectName() {
        return sourceProjectName;
    }

    public void setSourceProjectName(String sourceProjectName) {
        this.sourceProjectName = sourceProjectName;
    }

    public String getSourceLogStoreName() {
        return sourceLogStoreName;
    }

    public void setSourceLogStoreName(String sourceLogStoreName) {
        this.sourceLogStoreName = sourceLogStoreName;
    }

    @InternalApi
    public JSONObject toRequestJson()
    {
        JSONObject dict = new JSONObject();
        dict.put("type", "link");
        dict.put("logstoreName", getLinkStoreName());
        dict.put("sourceProject", getSourceProjectName());
        dict.put("sourceLogstore", getSourceLogStoreName());
        return dict;
    }

    public String toRequestString() {
        return toRequestJson().toString();
    }
}
