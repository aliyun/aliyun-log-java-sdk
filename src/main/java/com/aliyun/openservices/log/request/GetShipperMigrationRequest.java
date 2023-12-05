package com.aliyun.openservices.log.request;

public class GetShipperMigrationRequest extends Request {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Construct the base request
     *
     * @param project project name
     */
    public GetShipperMigrationRequest(String project, String name) {
        super(project);
        this.name = name;
    }
}
