package com.aliyun.openservices.log.sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.exception.LogException;

public class PutLogsSample {
    private Client client;

    PutLogsSample() {
        String endpoint = "";
        String accessKeyID = "test_accessKeyId";
        String accessKeySecret = "test_accessKey";

        // create a client
        this.client = new Client(endpoint, accessKeyID, accessKeySecret);
    }
    public static void main(String[] args) {
        PutLogsSample sample = new PutLogsSample();
        String project = "test-project";
        String logStore = "test-logstore";
        String topic = "TestTopic_2";
        // send logs to an exsiting logstore,
        // to create a logstore, see file LogStoreSample.java
        sample.PutLog(project, logStore, topic);
    }

    public void PutLog(String project, String logStore, String topic) {
        for (int i = 0; i < 10; i++) {
            List<LogItem> log = new ArrayList<LogItem>();
            LogItem logItem = new LogItem((int) (new Date().getTime() / 1000));
            logItem.PushBack("level", "info");
            logItem.PushBack("name", String.valueOf(i));
            logItem.PushBack("message", "it's a test message");

            log.add(logItem);

            LogItem logItem2 = new LogItem((int) (new Date().getTime() / 1000));
            logItem2.PushBack("level", "error");
            logItem2.PushBack("name", String.valueOf(i));
            logItem2.PushBack("message", "it's a test message");
            log.add(logItem2);

            try {
                client.PutLogs(project, logStore, topic, log, "");
            } catch (LogException e) {
                System.out.println("error code :" + e.getErrorCode());
                System.out.println("error message :" + e.getMessage());
                System.out.println("error requestId :" + e.getRequestId());
            }

        }
    }
}
