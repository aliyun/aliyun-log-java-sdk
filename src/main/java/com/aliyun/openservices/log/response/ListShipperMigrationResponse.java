package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ShipperMigration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListShipperMigrationResponse extends Response {
    protected int count = 0;
    protected int total = 0;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ShipperMigration> getMigrations() {
        return migrations;
    }

    public void setMigrations(List<ShipperMigration> migrations) {
        this.migrations = migrations;
    }

    protected List<ShipperMigration> migrations = new ArrayList<ShipperMigration>();

    /**
     * Construct the base response body with http headers
     *
     * @param headers http headers
     */
    public ListShipperMigrationResponse(Map<String, String> headers, int count, int total, List<ShipperMigration> migrations) {
        super(headers);
        setCount(count);
        setTotal(total);
        this.migrations = migrations;
    }
}
