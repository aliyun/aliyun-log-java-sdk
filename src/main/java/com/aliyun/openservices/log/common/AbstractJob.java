package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Date;
import com.aliyun.openservices.log.annotation.ProtectedApi;


abstract class AbstractJob {

    private String name;

    private String displayName;

    private String description;

    private JobType type;

    private boolean recyclable;

    private String adminAttribute;

    private Date createTime;

    private Date lastModifiedTime;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JobType getType() {
        return type;
    }

    protected void setType(JobType type) {
        this.type = type;
    }

    public boolean getRecyclable() {
        return recyclable;
    }

    public void setRecyclable(boolean recyclable) {
        this.recyclable = recyclable;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setAdminAttribute(String adminAttr) {this.adminAttribute = adminAttr;}

    public String getAdminAttribute() {return adminAttribute;}

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public abstract JobConfiguration getConfiguration();

    @ProtectedApi
    public void deserialize(JSONObject value) {
        name = value.getString("name");
        type = JobType.fromString(value.getString("type"));
        displayName = JsonUtils.readOptionalString(value, "displayName");
        description = JsonUtils.readOptionalString(value, "description");
        adminAttribute = JsonUtils.readOptionalString(value,"adminAttribute");
        recyclable = JsonUtils.readBool(value, "recyclable", false);
        if (value.containsKey("createTime")) {
            createTime = Utils.timestampToDate(value.getLong("createTime"));
        }
        if (value.containsKey("lastModifiedTime")) {
            lastModifiedTime = Utils.timestampToDate(value.getLong("lastModifiedTime"));
        }
    }
}
