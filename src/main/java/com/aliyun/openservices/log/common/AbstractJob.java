package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;
import net.sf.json.JSONObject;

import java.util.Date;

public abstract class AbstractJob {

    /**
     * Job name.
     */
    @JSONField
    private String name;

    /**
     * Job display name.
     */
    @JSONField
    private String displayName;

    /**
     * Job description.
     */
    @JSONField
    private String description;

    /**
     * The type of job
     */
    @JSONField
    private JobType type;

    /**
     * Job create time.
     */
    private Date createTime;

    /**
     * Job last modified time.
     */
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

    public void setType(JobType type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public abstract JobConfiguration getConfiguration();

    public void deserialize(JSONObject value) {
        name = value.getString("name");
        displayName = JsonUtils.readOptionalString(value, "displayName");
        description = JsonUtils.readOptionalString(value, "description");
        createTime = Utils.timestampToDate(value.getLong("createTime"));
        lastModifiedTime = Utils.timestampToDate(value.getLong("lastModifiedTime"));
    }
}
