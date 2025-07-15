package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.ClientConfiguration;

public class CnameSample {

    public static void main(String[] args) {
        // project not used
        String project = "test-project";
        String logStoreName = "test-logstore";
        String endpoint = "";
        String accessId = "";
        String accessKey = "";
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setCname(true);
        Client client = new Client(endpoint, accessId, accessKey, clientConfiguration);
        try {
            client.ListShard(project, logStoreName);
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

}
