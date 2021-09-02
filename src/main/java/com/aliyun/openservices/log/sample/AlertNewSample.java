package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.*;
import com.aliyun.openservices.log.response.GetAlertResponse;
import com.aliyun.openservices.log.response.ListAlertResponse;

public class AlertNewSample {
    public static void main(String[] args) {
        String accessId = "";
        String accessKey = "";

        String project = "test-huolang-alert";
        String host = "cn-shanghai-staging-share.sls.aliyuncs.com";
        String alertName = "test-alert-count-tpl";
        String dashboardName = "dashboardtest";
        String logstore = "logstore-test";

        host = "pub-cn-hangzhou-staging-share.log.aliyuncs.com";
        project = "test-hq123";
        alertName = "alert-1600604931412";

        Client client = new Client(host, accessId, accessKey);
        try {
            // test list jobs
            ListAlertRequest listReq = new ListAlertRequest(project);
            listReq.setOffset(0);
            listReq.setSize(10);
            ListAlertResponse listJobsResponse = client.listAlert(listReq);
            System.out.println(listJobsResponse.getTotal());
            System.out.println(listJobsResponse.getCount());
            for (Alert alert4 : listJobsResponse.getResults()) {
                System.out.println(alert4.getName());
            }


            // Get alert rule by name
            GetAlertResponse response = client.getAlert(new GetAlertRequest(project, alertName));

            Alert created = response.getAlert();
            System.out.println(created.getName());
            System.out.println("created.getConfiguration().getMuteUntil()");
            System.out.println(created.getConfiguration().getMuteUntil());
            System.out.println(created.getDisplayName());
            System.out.println(created.getCreateTime());


        } catch (LogException lex) {
            lex.printStackTrace();
        }
    }
}
