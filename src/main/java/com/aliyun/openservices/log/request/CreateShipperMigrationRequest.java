package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ShipperMigration;

public class CreateShipperMigrationRequest extends Request {
    public ShipperMigration getMigration() {
        return migration;
    }

    public void setMigration(ShipperMigration migration) {
        this.migration = migration;
    }

    private ShipperMigration migration;
    /**
     * @param project project name
     */
    public CreateShipperMigrationRequest(String project, ShipperMigration migration) {
        super(project);
        this.migration = migration;
    }
}
