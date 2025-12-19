package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.DataRedundancyType;
import com.aliyun.openservices.log.common.ProjectQuota;
import com.aliyun.openservices.log.exception.LogException;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class GetProjectResponse extends Response {

    private static final long serialVersionUID = 1938728647331317823L;
    private String createTime = "";
    private String lastModifyTime = "";
    private String description = "";
    private String status = "";
    private String resourceGroupId = "";

    private String region = "";
    private String owner = "";

    private DataRedundancyType dataRedundancyType;

    private ProjectQuota quota;

    private String transferAcceleration;
    private Boolean deletionProtection;

    public GetProjectResponse(Map<String, String> headers) {
        super(headers);
    }

    public void FromJsonObject(JSONObject obj) throws LogException {
        try {
            description = obj.getString("description");
            status = obj.getString("status");
            resourceGroupId = obj.getString("resourceGroupId");
            region = obj.getString("region");
            owner = obj.getString("owner");
            dataRedundancyType = DataRedundancyType.parse(obj.getString("dataRedundancyType"));
            quota = ProjectQuota.parseFromJSON(obj.getJSONObject(Consts.CONST_QUOTA));
            transferAcceleration = obj.getString("transferAcceleration");
            setCreateTime(obj.getString(Consts.CONST_CREATTIME));
            setLastModifyTime(obj.getString(Consts.CONST_LASTMODIFYTIME));
            deletionProtection = obj.getBoolean("deletionProtection");
        } catch (JSONException e) {
            throw new LogException("InvalidErrorResponse", e.getMessage(), GetRequestId());
        }
    }

    public String GetProjectDescription() {
        return description;
    }

    public String GetProjectStatus() {
        return status;
    }

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public String GetProjectRegion() {
        return region;
    }

    public String GetProjectOwner() {
        return owner;
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

    public String getTransferAcceleration() {
        return transferAcceleration;
    }

    public void setTransferAcceleration(String transferAcceleration) {
        this.transferAcceleration = transferAcceleration;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Boolean getDeletionProtection() {
        return deletionProtection;
    }

    public void setDeletionProtection(Boolean deletionProtection) {
        this.deletionProtection = deletionProtection;
    }
}
