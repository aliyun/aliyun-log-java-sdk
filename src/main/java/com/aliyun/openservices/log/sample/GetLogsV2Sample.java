package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.common.QueryResult;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetLogsRequestV2;
import com.aliyun.openservices.log.response.GetLogsResponseV2;

public class GetLogsV2Sample {
    private static final String endpoint = "your_endpoint";
    private static final String akId = "your_access_id";
    private static final String ak = "your_access_key";
    private static final String project = "your_project_name";
    private static final String logStore = "your_log_store";

    public static void main(String[] args) {
        Client client = new Client(endpoint, akId, ak);
        int from = 1678456800;
        int to = 1678460400;
        GetLogsRequestV2 request = new GetLogsRequestV2(project, logStore, from, to, "", "* | SELECT * limit 10000");
        try {
            GetLogsResponseV2 response = client.GetLogsV2(request);
            System.out.println(response.getQueryResultAsString());
            QueryResult result = response.getResult();
            System.out.println("isCompleted = " + result.isCompleted());
            for (QueriedLog log : result.getLogs()) {
                LogItem item = log.GetLogItem();
                for (LogContent f : item.GetLogContents()) {
                    System.out.println(f.getKey() + "=" + f.getValue());
                }
            }
        } catch (LogException ex) {
            ex.printStackTrace();
        }
    }
}
