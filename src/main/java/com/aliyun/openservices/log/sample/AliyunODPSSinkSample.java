package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.common.AliyunODPSSink;
import com.aliyun.openservices.log.common.ExportConfiguration;
import com.aliyun.openservices.log.common.ExportGeneralSink;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.request.CreateExportRequest;
import com.aliyun.openservices.log.request.GetExportRequest;
import com.aliyun.openservices.log.response.CreateExportResponse;
import com.aliyun.openservices.log.response.GetExportResponse;
import com.aliyun.openservices.log.Client;

public class AliyunODPSSinkSample {
    public static void main(String args[]) throws LogException, InterruptedException {
        AliyunODPSSink sink = new AliyunODPSSink();
        sink.setOdpsRolearn("your_roleArn");
        sink.setOdpsEndpoint("your_endpoint");
        sink.setOdpsTunnelEndpoint("your_TunnelEndpoint");
        sink.setOdpsProject("your_odps_project_name");
        sink.setOdpsTable("your_odps_table_name");
        sink.setTimeZone("+0800");
        sink.setFields("1", "2", "3");
        sink.setPartitionColumn("bucket");
        sink.setPartitionTimeFormat("%Y");
        sink.setMode("interval");
        String encoded = sink.toJsonString();

        ExportGeneralSink general = new ExportGeneralSink();
        general.setFields(JsonCodec.toMap(JSONObject.parseObject(encoded)));
        ExportConfiguration conf = new ExportConfiguration();
        conf.setRoleArn("your_roleArn");
        conf.setLogstore("your_logstore");
        conf.setSink(sink);
        conf.setFromTime((int) ((System.currentTimeMillis() / (long) 1000) - 864000));
        conf.setToTime(0);
        conf.setVersion("v.0");
        Export export = new Export();
        export.setConfiguration(conf);
        export.setName("my-odps-sink");
        export.setDisplayName("my-odps-sink");
        String project = "your_project_name";
        CreateExportRequest request = new CreateExportRequest(project, export);
        Client client = new Client("your_endpoint", "your_access_id", "your_access_key");
        CreateExportResponse resp = client.createExport(request);
        System.out.println(JsonUtils.serialize(resp));
        GetExportRequest requestExport = new GetExportRequest(project, "my-odps-sink");
        GetExportResponse respExport = client.getExport(requestExport);
        System.out.println(JsonUtils.serialize(respExport));
    }
}
