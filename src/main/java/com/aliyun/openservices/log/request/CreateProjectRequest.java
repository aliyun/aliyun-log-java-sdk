package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.DataRedundancyType;

public class CreateProjectRequest extends Request {

    private String description;
    private String resourceGroupId;
    private DataRedundancyType dataRedundancyType;

    public CreateProjectRequest(String project, String description, String resourceGroupId) {
        this(project, description, resourceGroupId, null);
    }

    public CreateProjectRequest(String project, String description, String resourceGroupId, DataRedundancyType dataRedundancyType) {
        super(project);
        this.description = description;
        this.resourceGroupId = resourceGroupId;
        this.dataRedundancyType = dataRedundancyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }

    public DataRedundancyType getDataRedundancyType() {
        return dataRedundancyType;
    }

    public void setDataRedundancyType(DataRedundancyType dataRedundancyType) {
        this.dataRedundancyType = dataRedundancyType;
    }

    public String getRequestBody() {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("projectName", GetProject());
        if (description != null) {
            jsonBody.put("description", description);
        }
        if (resourceGroupId != null) {
            jsonBody.put("resourceGroupId", resourceGroupId);
        }
        if (dataRedundancyType != null) {
            jsonBody.put("dataRedundancyType", dataRedundancyType.toString());
        }
        return jsonBody.toString();
    }
}
