/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Histogram;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;

import java.util.Date;

public class QueryLogsSample {

    private Client client;
    String project = "your_project_name";
    String logStore = "your_logstore";
    String topic = "";

    QueryLogsSample() {
        String endpoint = "";
        String accessKeyID = "test_accessKeyId";
        String accessKeySecret = "test_accessKey";
        // create a client
        this.client = new Client(endpoint, accessKeyID, accessKeySecret);
    }

    public static void main(String args[]) throws LogException,
            InterruptedException {
        QueryLogsSample sample = new QueryLogsSample();
        sample.GetLogs();
        sample.GetLogsWithSql();
        sample.GetHistogram();
        sample.GetProjectLogs();
    }

    // query logs
    public void GetLogs() {
        try {
            String query = "error";
            int from = (int) (new Date().getTime() / 1000 - 10000);
            int to = (int) (new Date().getTime() / 1000 + 10);
            GetLogsResponse logsResponse = client.GetLogs(project, logStore,
                    from, to, topic, query, 100, 0, false);

            // if the query is imcomplete, please retry later.
            if (!logsResponse.IsCompleted()) {
                System.out.println("The query is imcomplete, please retry later.");
                return;
            }

            System.out.println("Returned log data count:" + logsResponse.GetCount());
            for (QueriedLog log : logsResponse.getLogs()) {
                System.out.println("source : " + log.GetSource());
                LogItem item = log.GetLogItem();
                System.out.println("time : " + item.mLogTime);
                for (LogContent content : item.mContents) {
                    System.out.println(content.mKey + ":" + content.mValue);
                }
            }
        } catch (LogException e) {
            System.out.println("error code :" + e.getErrorCode());
            System.out.println("error message :" + e.getMessage());
        }
    }

    // query logs using sql
    public void GetLogsWithSql() {
        try {
            String sql = "* | select count(1)";
            int from = (int) (new Date().getTime() / 1000 - 600);
            int to = (int) (new Date().getTime() / 1000);
            GetLogsResponse logsResponse = client.GetLogs(project, logStore,
                    from, to, topic, sql, 100, 0, false);

            // if the query is imcomplete, please retry later.
            if (!logsResponse.IsCompleted()) {
                System.out.println("The query is imcomplete, please retry later.");
                return;
            }

            System.out.println("Returned sql result count:" + logsResponse.GetCount());
            for (QueriedLog log : logsResponse.getLogs()) {
                LogItem item = log.GetLogItem();
                System.out.println("time : " + item.mLogTime);
                for (LogContent content : item.mContents) {
                    System.out.println(content.mKey + ":" + content.mValue);
                }
            }
        } catch (LogException e) {
            System.out.println("error code :" + e.getErrorCode());
            System.out.println("error message :" + e.getMessage());
        }
    }

    public void GetHistogram() {
        try {
            String query = "";
            int from = (int) (new Date().getTime() / 1000 - 10000);
            int to = (int) (new Date().getTime() / 1000 + 10);
            GetHistogramsResponse histogramsResponse = client.GetHistograms(
                    project, logStore, from, to, topic, query);
            // if the query is imcomplete, please retry later.
            if (!histogramsResponse.IsCompleted()) {
                System.out.println("The query is imcomplete, please retry later.");
                return;
            }
            System.out.println("histogram result: " + histogramsResponse.GetTotalCount());
            System.out.println("is_completed : "
                    + histogramsResponse.IsCompleted());
            for (Histogram histogram : histogramsResponse.GetHistograms()) {
                System.out.println("beginTime:" + histogram.mFromTime
                        + " endTime:" + histogram.mToTime + " logCount:"
                        + histogram.mCount + " is_completed:"
                        + histogram.mIsCompleted);
            }
            System.out.println("");
        } catch (LogException e) {
            System.out.println("error code :" + e.getErrorCode());
            System.out.println("error message :" + e.getMessage());
            System.out.println("error requestId :" + e.getRequestId());
        }
    }

    public void GetProjectLogs() {
        try {
            int now = (int) (new Date().getTime() / 1000);
            String sql = "select count(1) as cnt from xxx where __time__ > " + (now - 3600) + " and __time__ < " + now;
            GetLogsResponse logsResponse = client.executeProjectSql(project, sql, true);

            // if the query is imcomplete, please retry later.
            if (!logsResponse.IsCompleted()) {
                System.out.println("The query is imcomplete, please retry later.");
                return;
            }
            System.out.println("Returned sql result count:" + logsResponse.GetCount());
            for (QueriedLog log : logsResponse.getLogs()) {
                LogItem item = log.GetLogItem();
                for (LogContent content : item.mContents) {
                    System.out.println(content.mKey + ":" + content.mValue);
                }
            }
        } catch (LogException e) {
            System.out.println("error code :" + e.getErrorCode());
            System.out.println("error message :" + e.getMessage());
        }
    }
}
