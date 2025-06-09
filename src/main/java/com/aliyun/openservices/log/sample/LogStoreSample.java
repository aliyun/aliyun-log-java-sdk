package com.aliyun.openservices.log.sample;

import java.util.List;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.CreateLogStoreResponse;
import com.aliyun.openservices.log.response.GetLogStoreResponse;

public class LogStoreSample {

    public static void main(String[] args) {
        String endpoint = "";
        String accessKeyID = "test_accessKeyId";
        String accessKeySecret = "test_accessKey";

        // create a client
        Client client = new Client(endpoint, accessKeyID, accessKeySecret);

        String project = "test-project";
        String logstore = "test-logstore";
        try {
            LogStore store = new LogStore(logstore, 1, 10);
            CreateLogStoreResponse res = client.CreateLogStore(project, store);
            System.out.println(res.GetRequestId());

            GetLogStoreResponse getResp = client.GetLogStore(project, logstore);
            System.out.println(getResp.GetLogStore().GetCreateTime());
            System.out.println(getResp.GetLogStore().GetLogStoreName());

            List<String> logStores = client.ListLogStores(project, 0, 500, "")
                    .GetLogStores();
            System.out.println("ListLogs:" + logStores.toString() + "\n");
        } catch (LogException e) {
            e.printStackTrace();
        }
    }
}
