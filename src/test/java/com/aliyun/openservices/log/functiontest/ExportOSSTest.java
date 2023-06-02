package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.*;
import org.junit.Test;

import java.util.List;

public class ExportOSSTest {
    private static final String endpoint = "http://cn-hangzhou.log.aliyuncs.com";
    private static final String accessKeyId = "";
    private static final String accessKeySecret = "";

    private static final Client client = new Client(endpoint, accessKeyId, accessKeySecret);

    private static final String project = "test";

    private static final String roleArn = "test";

    private static final String bucket = "test";

    private static final String logStore = "test";

    private static final String ossEndpoint = "test";
    @Test
    public void testOssSink() throws LogException {
        createOssExportJob();
        getOssExportJob();
        listOssExportJob();
        updateOssExportJob();
        deleteOssExportJob();
    }

    private static void deleteOssExportJob() throws LogException {
        DeleteExportResponse deleteExportResponse = client.deleteExport(new DeleteExportRequest(project, "test"));
    }

    private static void updateOssExportJob() throws LogException {
        Export export = new Export();
        export.setName("test");
        export.setDisplayName("test");
        export.setDescription("test");

        ExportConfiguration exportConfiguration = new ExportConfiguration();

        AliyunOSSSink ossSink = new AliyunOSSSink();
        ossSink.setRoleArn(roleArn);
        ossSink.setBucket(bucket);
        ossSink.setPrefix("");
        ossSink.setSuffix("");
        ossSink.setPathFormat("%Y/%m/%d/%H/%M");
        ossSink.setPathFormatType("time");
        ossSink.setBufferSize(255);
        ossSink.setBufferInterval(300);
        ossSink.setContentType("json");

        ExportContentCsvDetail csvDetail = new ExportContentCsvDetail();
        csvDetail.setNullIdentifier("");
        ossSink.setContentDetail(csvDetail);

        exportConfiguration.setLogstore(logStore);
        exportConfiguration.setRoleArn(roleArn);
        exportConfiguration.setSink(ossSink);
        exportConfiguration.setFromTime(1665504000);
        exportConfiguration.setToTime(1665565200);
        exportConfiguration.setVersion("v2.0");

        export.setConfiguration(exportConfiguration);
        UpdateExportResponse res = client.updateExport(new UpdateExportRequest(project,export));
    }


    private static void createOssExportJob() throws LogException {
        Export export = new Export();
        String name = "test";
        export.setName(name);
        export.setDisplayName(name);
        export.setDescription(name);

        ExportConfiguration exportConfiguration = new ExportConfiguration();
        AliyunOSSSink ossSink = new AliyunOSSSink();
        ossSink.setRoleArn(roleArn);
        ossSink.setBucket(bucket);
        ossSink.setPrefix(name);
        ossSink.setSuffix("");
        ossSink.setPathFormat("%Y/%m/%d/%H/%M");
        ossSink.setPathFormatType("time");
        ossSink.setBufferSize(255);
        ossSink.setBufferInterval(300);
        ossSink.setContentType("json");
        // ossSink.setDelaySeconds(15 * 24 * 3600);
        ossSink.setEndpoint(ossEndpoint);

        ExportContentJsonDetail jsonDetail = new ExportContentJsonDetail();
        jsonDetail.setEnableTag(true);

        ossSink.setContentDetail(jsonDetail);

        int fromTime = (int)(System.currentTimeMillis() / 1000) - 17 * 24 * 3600;
        exportConfiguration.setLogstore(logStore);
        exportConfiguration.setRoleArn(roleArn);
        exportConfiguration.setSink(ossSink);
        exportConfiguration.setFromTime(fromTime - fromTime % 300);
        exportConfiguration.setToTime(0);
        exportConfiguration.setVersion("v2.0");
        export.setConfiguration(exportConfiguration);
        CreateExportResponse createExportResponse = client.createExport(new CreateExportRequest(project, export));
    }

    private static void getOssExportJob() throws LogException {
        GetJobResponse getJobResponse = client.getJob(new GetJobRequest(project, "test"));
        Job job = getJobResponse.getJob();

        GetExportResponse getExportResponse = client.getExport(new GetExportRequest(project, "test"));
        Export res = getExportResponse.getExport();
    }

    private static void listOssExportJob() throws LogException {
        ListExportResponse listExportResponse = client.listExport(new ListExportRequest(project));
        List<Export> exports = listExportResponse.getResults();
    }
}
