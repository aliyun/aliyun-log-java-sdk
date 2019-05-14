package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Logging;
import com.aliyun.openservices.log.common.LoggingDetail;
import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateLoggingRequest;
import com.aliyun.openservices.log.request.UpdateLoggingRequest;
import com.aliyun.openservices.log.response.ListProjectResponse;

import java.util.ArrayList;
import java.util.List;


public class LoggingSample {

    // The project to save those logs
    private static final String PROJECT_TO_STORE_LOG = "";

    private static final String ACCESS_KEY = "";
    private static final String ACCESS_KEY_SECRET = "";
    private static final String ENDPOINT = "";

    public static void main(String[] args) throws Exception {

        Client client = new Client(ENDPOINT, ACCESS_KEY, ACCESS_KEY_SECRET);

        // step 1. Get all projects in this region

        ListProjectResponse response = client.ListProject();
        String[] logTypes = new String[]{
                "operation_log",
                "metering",
                "consumergroup_log",
                "logtail_alarm",
                "logtail_profile",
                "logtail_status",
        };
        // step 2. Enable logging for each project
        for (Project project : response.getProjects()) {

            System.out.println(project.getProjectName());

            final String projectName = project.getProjectName();
            List<LoggingDetail> logListToEnable = new ArrayList<LoggingDetail>();
            for (String logType : logTypes) {
                String logstoreToSaveLog;
                if (logType.equals("operation_log")) {
                    // operation-log save to a separated logstore
                    logstoreToSaveLog = "internal-operation_log";
                } else {
                    // the other logs save to a free logstore
                    logstoreToSaveLog = "internal-diagnostic_log";
                }
                logListToEnable.add(new LoggingDetail(logType, logstoreToSaveLog));
            }
            Logging logging = new Logging(PROJECT_TO_STORE_LOG, logListToEnable);
            try {
                // try to update logging configuration first
                client.updateLogging(new UpdateLoggingRequest(projectName, logging));
            } catch (LogException ex) {
                if (ex.GetErrorCode().equals("LoggingNotExist")) {
                    // enable logging if not enabled previously.
                    client.createLogging(new CreateLoggingRequest(projectName, logging));
                } else {
                    throw ex;
                }
            }
        }
    }
}
