package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Date;
import com.aliyun.openservices.log.annotation.InternalApi;


abstract class AbstractJob implements JsonSerializable, JsonDeserializable {

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

    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = new JSONObject();
        put(value, "name", name);
        put(value, "displayName", displayName);
        put(value, "description", description);
        put(value, "type", type == null ? null : type.toString());
        value.put("recyclable", recyclable);
        put(value, "adminAttribute", adminAttribute);
        if (createTime != null) {
            value.put("createTime", Utils.dateToTimestamp(createTime));
        }
        if (lastModifiedTime != null) {
            value.put("lastModifiedTime", Utils.dateToTimestamp(lastModifiedTime));
        }
        JobConfiguration configuration = getConfiguration();
        if (configuration != null) {
            value.put("configuration", configuration.toJsonObject());
        }
        return value;
    }

    static void put(JSONObject value, String key, String item) {
        if (item != null) {
            value.put(key, item);
        }
    }

    @InternalApi
    public void fromJsonObject(JSONObject value) {
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
