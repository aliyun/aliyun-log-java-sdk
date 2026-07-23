package com.aliyun.openservices.log.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResourceUserGroup implements Serializable {
    @SerializedName("user_group_id")
    private String groupId;
    @SerializedName("user_group_name")
    private String groupName;
    @SerializedName("enabled")
    private boolean enabled;
    @SerializedName("members")
    private List<String> members;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}

