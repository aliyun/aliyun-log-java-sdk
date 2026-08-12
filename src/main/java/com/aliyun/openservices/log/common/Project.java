package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class Project implements Serializable, JsonSerializable, JsonDeserializable {

    /**
     * project resource
     */
    private static final long serialVersionUID = 3190120783809426119L;
    protected String projectName = "";
    protected String projectStatus = "";
    protected String projectOwner = "";
    protected String projectDesc = "";
    protected String region = "";
    protected String createTime = "";
    protected String lastModifyTime = "";
    protected String resourceGroupId;
    private DataRedundancyType dataRedundancyType;
    private ProjectQuota quota;

    public Project() {
        super();
    }

    public Project(String projectName, String projectStatus, String projectOwner, String projectDesc, String region,
                   String createTime, String lastModifyTime) {
        super();
        this.projectName = projectName;
        this.projectStatus = projectStatus;
        this.projectOwner = projectOwner;
        this.projectDesc = projectDesc;
        this.region = region;
        this.createTime = createTime;
        this.lastModifyTime = lastModifyTime;
        this.resourceGroupId = "";
    }

    public Project(Project project) {
        super();
        this.projectName = project.getProjectName();
        this.projectStatus = project.getProjectStatus();
        this.projectOwner = project.getProjectOwner();
        this.projectDesc = project.getProjectDesc();
        this.region = project.getRegion();
        this.createTime = project.getCreateTime();
        this.lastModifyTime = project.getLastModifyTime();
        this.resourceGroupId = project.getResourceGroupId();
        this.dataRedundancyType = project.getDataRedundancyType();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getProjectOwner() {
        return projectOwner;
    }

    public void setProjectOwner(String projectOwner) {
        this.projectOwner = projectOwner;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public DataRedundancyType getDataRedundancyType() {
        return dataRedundancyType;
    }

    public void setDataRedundancyType(DataRedundancyType dataRedundancyType) {
        this.dataRedundancyType = dataRedundancyType;
    }

    public ProjectQuota getQuota() {
        return quota;
    }

    public void setQuota(ProjectQuota quota) {
        this.quota = quota;
    }

    private JSONObject toRequestJson() {
        JSONObject projectDict = new JSONObject();
        projectDict.put(Consts.CONST_PROJECTNAME, getProjectName());
        projectDict.put(Consts.CONST_PROJECTSTATUS, getProjectStatus());
        projectDict.put(Consts.CONST_PROJECTOWNER, getProjectOwner());
        projectDict.put(Consts.CONST_PROJECTDESC, getProjectDesc());
        projectDict.put(Consts.CONST_PROJECTREGION, getRegion());
        projectDict.put(Consts.CONST_RESOURCEGROUPID, getResourceGroupId());
        return projectDict;
    }

    public String toRequestString() {
        return toRequestJson().toString();
    }

    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject projectDict = toRequestJson();
        projectDict.put(Consts.CONST_CREATTIME, getCreateTime());
        projectDict.put(Consts.CONST_LASTMODIFYTIME, getLastModifyTime());
        return projectDict;
    }


    @InternalApi
    public void fromJsonObject(JSONObject dict) throws LogException {
        try {
            setProjectName(dict.getString(Consts.CONST_PROJECTNAME));
            setProjectDesc(dict.getString(Consts.CONST_PROJECTDESC));
            setProjectOwner(dict.getString(Consts.CONST_PROJECTOWNER));
            setProjectStatus(dict.getString(Consts.CONST_PROJECTSTATUS));
            setRegion(dict.getString(Consts.CONST_PROJECTREGION));
            setCreateTime(readTime(dict, Consts.CONST_CREATTIME));
            setLastModifyTime(readTime(dict, Consts.CONST_LASTMODIFYTIME));
            setResourceGroupId(dict.getString(Consts.CONST_RESOURCEGROUPID));
            setDataRedundancyType(DataRedundancyType.parse(dict.getString("dataRedundancyType")));
            setQuota(ProjectQuota.parseFromJsonObject(dict.getJSONObject(Consts.CONST_QUOTA)));
        } catch (JSONException e) {
            throw new LogException("FailToGenerateProject", e.getMessage(), e, "");
        }
    }

    private static String readTime(JSONObject value, String key) {
        return value.isNumber(key)
                ? String.valueOf(value.getLongValue(key))
                : value.getString(key);
    }

    public void fromJsonString(String projectString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(projectString);
            fromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateProject", e.getMessage(), e, "");
        }
    }
}
