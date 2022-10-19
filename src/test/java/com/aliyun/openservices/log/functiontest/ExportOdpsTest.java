package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.request.CreateExportRequest;
import com.aliyun.openservices.log.request.DeleteExportRequest;
import com.aliyun.openservices.log.response.CreateExportResponse;
import com.aliyun.openservices.log.response.DeleteExportResponse;
import com.aliyun.openservices.log.response.GetExportResponse;
import com.aliyun.openservices.log.request.GetExportRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExportOdpsTest extends FunctionTest {
    private String project = "111111";

    @Before
    public void setUp() {
    }

    @After
    public void clearUp() {
    }
    private Export createExport() throws Exception {
        AliyunODPSSink sink = new AliyunODPSSink();
        sink.setOdpsRolearn("acs:ram::111111111:role/aliyunlogdefaultrole");
        sink.setOdpsEndpoint("http://service.cn-hangzhou.maxcompute.aliyun-inc.com/api");
        sink.setOdpsTunnelEndpoint("http://dt.cn-hangzhou.maxcompute.aliyun-inc.com");
        sink.setOdpsProject("test_lichao_odps");
        sink.setOdpsTable("11111_odps_table");
        sink.setTimeZone("+0800");
        sink.setFields("acc_access_region", "http_method", "referer", "client_ip");
        sink.setPartitionColumn("bucket");
        sink.setPartitionTimeFormat("%Y");
        String encoded = JSONObject.toJSONString(sink);

        ExportGeneralSink general = new ExportGeneralSink();
        general.setFields(JSONObject.parseObject(encoded).getInnerMap());
        ExportConfiguration conf = new ExportConfiguration();
        conf.setRoleArn("acs:ram::11111111111:role/aliyunlogdefaultrole");
        conf.setLogstore("source-log");
        conf.setSink(sink);
        conf.setFromTime((int) ((System.currentTimeMillis() / (long) 1000) - 864000));
        conf.setToTime(0);
        conf.setVersion("v2.0");
        Export export = new Export();
        export.setConfiguration(conf);
        export.setName("my-odps-sink");
        export.setDisplayName("my-odps-sink");
        return export;
    }

    @Test
    public void testCrud() throws Exception {
        Export export = createExport();
        CreateExportRequest request = new CreateExportRequest(project, export);
        CreateExportResponse resp = client.createExport(request);
        System.out.println(JSONObject.toJSONString(resp));
    }

    @Test
    public void testDelete() throws Exception {
        DeleteExportRequest request = new DeleteExportRequest(project, "my-odps-sink");
        DeleteExportResponse resp = client.deleteExport(request);
        System.out.println(JSONObject.toJSONString(resp));
    }

    @Test 
    public void testGetResponse() throws Exception {
        GetExportRequest request = new GetExportRequest(project, "my-odps-sink");
        GetExportResponse resp = client.getExport(request);
        System.out.println(JSONObject.toJSONString(resp));
    }
}