package com.aliyun.openservices.log.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ResourceAlertPolicy implements Serializable {
    @SerializedName("policy_id")
    private String policyId;
    @SerializedName("policy_name")
    private String policyName;
    @SerializedName("is_default")
    private Boolean isDefault;
    @SerializedName("parent_id")
    private String parentId;
    @SerializedName("group_script")
    private String groupScript;
    @SerializedName("inhibit_script")
    private String inhibitScript;
    @SerializedName("silence_script")
    private String SilenceScript;

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getGroupScript() {
        return groupScript;
    }

    public void setGroupScript(String groupScript) {
        this.groupScript = groupScript;
    }

    public String getInhibitScript() {
        return inhibitScript;
    }

    public void setInhibitScript(String inhibitScript) {
        this.inhibitScript = inhibitScript;
    }

    public String getSilenceScript() {
        return SilenceScript;
    }

    public void setSilenceScript(String silenceScript) {
        SilenceScript = silenceScript;
    }
}
