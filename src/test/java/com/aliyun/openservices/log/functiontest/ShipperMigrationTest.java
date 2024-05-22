package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.log.common.ShipperMigration;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateShipperMigrationRequest;
import com.aliyun.openservices.log.request.GetShipperMigrationRequest;
import com.aliyun.openservices.log.request.ListShipperMigrationRequest;
import com.aliyun.openservices.log.response.CreateShipperMigrationResponse;
import com.aliyun.openservices.log.response.GetShipperMigrationResponse;
import com.aliyun.openservices.log.response.ListShipperMigrationResponse;
import org.junit.Test;

public class ShipperMigrationTest extends FunctionTest {

    private static String testProject = "zhongzhou-shipper";
    private static String testLogstore = "oss-shipper";
    private static String testOssShipper = "testosssdk";

    @Test
    public void testMigration() throws LogException {
//        testCreateMigration();
        testListMigration();
        testGetMigration();
    }

    public void testListMigration() throws LogException {
        ListShipperMigrationRequest request = new ListShipperMigrationRequest(testProject, 0, 0);
        ListShipperMigrationResponse resp = client.listShipperMigration(request);
        System.out.println(JSONObject.toJSONString(resp));
    }

    public void testGetMigration() throws LogException {
        GetShipperMigrationRequest request = new GetShipperMigrationRequest(testProject, "migrationjobname2");
        GetShipperMigrationResponse resp = client.getShipperMigration(request);
        System.out.println(JSONObject.toJSONString(resp));
    }

    public void testCreateMigration() throws LogException {
        ShipperMigration migration = new ShipperMigration(
                "migrationjobname2",
                testLogstore, testOssShipper
        );
        CreateShipperMigrationRequest request = new CreateShipperMigrationRequest(
                testProject, migration
        );
        CreateShipperMigrationResponse resp = client.createShipperMigration(request);
        System.out.println(JSONObject.toJSONString(resp));
    }
}
