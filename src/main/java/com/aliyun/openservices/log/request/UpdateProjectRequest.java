package com.aliyun.openservices.log.request;

import net.sf.json.JSONObject;


public class UpdateProjectRequest extends Request {

    private String description;

    public UpdateProjectRequest(String project, String description) {
        super(project);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JSONObject marshal() {
        JSONObject object = new JSONObject();
        object.put("description", description);
        return object;
    }
}
