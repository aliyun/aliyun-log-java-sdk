package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ETL implements Serializable {

    private static final long serialVersionUID = 949447748635414993L;

    /**
     * ETL rule name.
     */
    private String name;

    /**
     * Alert display name.
     */
    private String displayName;

    /**
     * Alert description.
     */
    private String description;

    /**
     * Alert rule state.
     */
    private JobState state;

    /**
     * ETL configuration.
     */
    private ETLConfiguration configuration;

    /**
     * Alert rule create time.
     */
    private Date createTime;

    /**
     * Alert rule last modified time.
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

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public ETLConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ETLConfiguration configuration) {
        this.configuration = configuration;
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

    public void deserialize(JSONObject value) {
        name = value.getString("name");
        displayName = JsonUtils.readOptionalString(value, "displayName");
        description = JsonUtils.readOptionalString(value, "description");
        state = JobState.fromString(value.getString("state"));
        configuration = new ETLConfiguration();
        configuration.deserialize(value.getJSONObject("configuration"));
        createTime = Utils.timestampToDate(value.getLong("createTime"));
        lastModifiedTime = Utils.timestampToDate(value.getLong("lastModifiedTime"));
    }

    public Job makeJob() {
        Job job = new Job();
        job.setType(JobType.ETL);
        job.setName(getName());
        job.setDisplayName(getDisplayName());
        job.setDescription(getDescription());
        job.setState(getState());
        job.setConfiguration(getConfiguration());
        return job;
    }
}
