package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;

public class CnameSample {

    public static void main(String[] args) {
        // project not used
        String project = "test-project";
        String logStoreName = "test-logstore";
        String endpoint = "your custom domain";
        String accessId = "";
        String accessKey = "";
        Client client = new Client(endpoint, accessId, accessKey);
        client.setCname(true);
        try {
            client.ListShard(project, logStoreName);
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

}
