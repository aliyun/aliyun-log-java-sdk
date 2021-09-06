package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Map;

public class ResourceActionPolicy implements Serializable {
    @JSONField(name = "action_policy_id")
    private String actionPolicyId;
    @JSONField(name = "action_policy_name")
    private String actionPolicyName;
    @JSONField(name = "is_default")
    private boolean isDefault;
    @JSONField(name = "primary_policy_script")
    private String primaryPolicyScript;
    @JSONField(name = "secondary_policy_script")
    private String secondaryPolicyScript;

    @JSONField(name = "escalation_start_enabled")
    private boolean escalationStartEnabled;
    @JSONField(name = "escalation_start_timeout")
    private String escalationStartTimeout;

    @JSONField(name = "escalation_inprogress_enabled")
    private boolean escalationInProgressEnabled;
    @JSONField(name = "escalation_inprogress_timeout")
    private String escalationInProgressTimeout;

    @JSONField(name = "escalation_enabled")
    private boolean escalationEnabled;
    @JSONField(name = "escalation_timeout")
    private String escalationTimeout;

    @JSONField(name = "labels")
    private Map<String, String> labels;

    public String getActionPolicyId() {
        return actionPolicyId;
    }

    public void setActionPolicyId(String actionPolicyId) {
        this.actionPolicyId = actionPolicyId;
    }

    public String getActionPolicyName() {
        return actionPolicyName;
    }

    public void setActionPolicyName(String actionPolicyName) {
        this.actionPolicyName = actionPolicyName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getPrimaryPolicyScript() {
        return primaryPolicyScript;
    }

    public void setPrimaryPolicyScript(String primaryPolicyScript) {
        this.primaryPolicyScript = primaryPolicyScript;
    }

    public String getSecondaryPolicyScript() {
        return secondaryPolicyScript;
    }

    public void setSecondaryPolicyScript(String secondaryPolicyScript) {
        this.secondaryPolicyScript = secondaryPolicyScript;
    }

    public boolean isEscalationStartEnabled() {
        return escalationStartEnabled;
    }

    public void setEscalationStartEnabled(boolean escalationStartEnabled) {
        this.escalationStartEnabled = escalationStartEnabled;
    }

    public String isEscalationStartTimeout() {
        return escalationStartTimeout;
    }

    public boolean isEscalationInProgressEnabled() {
        return escalationInProgressEnabled;
    }

    public void setEscalationInProgressEnabled(boolean escalationInProgressEnabled) {
        this.escalationInProgressEnabled = escalationInProgressEnabled;
    }

    public String isEscalationInProgressTimeout() {
        return escalationInProgressTimeout;
    }

    public boolean isEscalationEnabled() {
        return escalationEnabled;
    }

    public void setEscalationEnabled(boolean escalationEnabled) {
        this.escalationEnabled = escalationEnabled;
    }

    public String getEscalationTimeout() {
        return escalationTimeout;
    }

    public void setEscalationTimeout(String escalationTimeout) {
        this.escalationTimeout = escalationTimeout;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public String getEscalationStartTimeout() {
        return escalationStartTimeout;
    }

    public void setEscalationStartTimeout(String escalationStartTimeout) {
        this.escalationStartTimeout = escalationStartTimeout;
    }

    public String getEscalationInProgressTimeout() {
        return escalationInProgressTimeout;
    }

    public void setEscalationInProgressTimeout(String escalationInProgressTimeout) {
        this.escalationInProgressTimeout = escalationInProgressTimeout;
    }
}

