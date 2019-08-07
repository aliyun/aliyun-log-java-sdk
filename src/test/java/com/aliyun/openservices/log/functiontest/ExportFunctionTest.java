package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.AliyunADBSink;
import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.common.ExportConfiguration;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.GetExportResponse;
import com.aliyun.openservices.log.response.ListExportResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ExportFunctionTest extends JobIntgTest {

    private final String testProject = "project-to-test-alert";
    private final String testLogstore = "test-logstore";

    private Export constructExport() {
        Export exp = new Export();
        String jobName = "adb-export-" + getNowTimestamp();
        exp.setName(jobName);
        exp.setDisplayName("test-export-adb-job");
        exp.setDescription("export to adb");
        ExportConfiguration conf = new ExportConfiguration();
        conf.setLogstore(testLogstore);
        conf.setAccessKeyId("dummy");
        conf.setAccessKeySecret("dummy");
        conf.setFromTime((int)(System.currentTimeMillis() / 1000));
        conf.setInstanceType("Standard");
        AliyunADBSink sink = new AliyunADBSink();
        sink.setBatchSize(10240);
        sink.setStrictMode(false);
        sink.setDbType("adb20");
        sink.setRegionId("cn-hangzhou");
        sink.setZoneId("cn-hangzhou-1");
        sink.setTableGroupName("test-group");
        sink.setDatabase("aaa");
        sink.setTable("bbb");
        sink.setUrl("host:port");
        sink.setUser("username");
        sink.setPassword("pwd123");
        HashMap<String, String> columnMapping = new HashMap<String, String>();
        columnMapping.put("__tag__:__receive_time__", "loghub_receive_time");
        columnMapping.put("__source__", "source_ip");
        columnMapping.put("__time__", "event_time");
        columnMapping.put("__topic__", "topic");
        columnMapping.put("content_key_1", "key_1");
        columnMapping.put("content_key_2", "key_2");
        columnMapping.put("content_key_3", "key_3");
        sink.setColumnMapping(columnMapping);
        conf.setSink(sink);
        conf.setInstanceType("Standard");
        exp.setConfiguration(conf);
        return exp;
    }

    @Before
    public void cleanUp() throws Exception {
        ListExportRequest request = new ListExportRequest(testProject);
        request.setOffset(0);
        request.setSize(100);
        ListExportResponse response = client.listExport(request);
        for (Export item : response.getResults()) {
            client.deleteExport(new DeleteExportRequest(testProject, item.getName()));
        }
    }

    @Test
    public void testExportCRUD() throws Exception {
        //create
        Export export = constructExport();
        client.createExport(new CreateExportRequest(testProject, export));

        //get
        GetExportResponse response = client.getExport(new GetExportRequest(testProject, export.getName()));
        Export result = response.getExport();
        assertEquals(export.getName(), result.getName());
        assertEquals(export.getDisplayName(), result.getDisplayName());

        //update
        export.setDisplayName("New display name");
        export.setDescription("New description");
        client.updateExport(new UpdateExportRequest(testProject, export));
        response = client.getExport(new GetExportRequest(testProject, export.getName()));
        result = response.getExport();
        assertEquals(export.getName(), result.getName());
        assertEquals(export.getDisplayName(), result.getDisplayName());

        //list
        ListExportRequest listExportRequest = new ListExportRequest(testProject);
        listExportRequest.setOffset(0);
        listExportRequest.setSize(100);
        ListExportResponse listExportResponse = client.listExport(listExportRequest);
        assertEquals(1, (int) listExportResponse.getCount());
        assertEquals(1, (int) listExportResponse.getTotal());

        //delete
        client.deleteExport(new DeleteExportRequest(testProject, export.getName()));
        try {
            client.getExport(new GetExportRequest(testProject, export.getName()));
            fail();
        } catch (LogException ex) {
            assertEquals("Job " + export.getName() + " does not exist", ex.GetErrorMessage());
        }
    }

    @Test
    public void testExportAction() throws Exception {
        String testExportName = "test-export-job";
        client.stopExport(new StopExportRequest(testProject, testExportName));

        Thread.sleep(3000); //wait status changed
        System.out.println("status: " + client.getExport(new GetExportRequest(testProject, testExportName)).getExport().getState());

        client.startExport(new StartExportRequest(testProject, testExportName));
        Thread.sleep(3000); //wait status changed
        System.out.println("status: " + client.getExport(new GetExportRequest(testProject, testExportName)).getExport().getState());
    }
}
