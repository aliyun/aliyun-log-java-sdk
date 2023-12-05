package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ShipperMigration;

import java.util.Map;

public class GetShipperMigrationResponse extends Response {
    private ShipperMigration migration;

    /**
     * Construct the base response body with http headers
     *
     * @param headers http headers
     */
    public GetShipperMigrationResponse(Map<String, String> headers, ShipperMigration migration) {
        super(headers);
        this.migration = migration;
    }
    public ShipperMigration getMigration() {
        return migration;
    }

    public void setMigration(ShipperMigration migration) {
        this.migration = migration;
    }
}
