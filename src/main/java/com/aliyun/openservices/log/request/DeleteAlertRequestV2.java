package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.util.Args;

public class DeleteAlertRequestV2 extends Request {

    private String name;

    public DeleteAlertRequestV2(String project, String name) {
        super(project);
        Args.notNullOrEmpty(name, "name");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
