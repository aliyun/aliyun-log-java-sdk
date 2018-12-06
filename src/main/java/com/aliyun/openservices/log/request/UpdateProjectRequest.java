package com.aliyun.openservices.log.request;

import java.util.Collections;


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

    public Object getBody() {
        return Collections.singletonMap("description", description);
    }
}
