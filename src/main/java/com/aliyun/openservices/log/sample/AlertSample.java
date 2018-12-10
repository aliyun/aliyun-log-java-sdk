package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.common.EmailNotification;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.common.JobState;
import com.aliyun.openservices.log.common.Notification;
import com.aliyun.openservices.log.common.Query;
import com.aliyun.openservices.log.common.TimeSpanType;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateAlertRequest;
import com.aliyun.openservices.log.request.CreateDashboardRequest;
import com.aliyun.openservices.log.request.DeleteAlertRequest;
import com.aliyun.openservices.log.request.DeleteDashboardRequest;
import com.aliyun.openservices.log.request.DisableAlertRequest;
import com.aliyun.openservices.log.request.EnableAlertRequest;
import com.aliyun.openservices.log.request.GetAlertRequest;
import com.aliyun.openservices.log.request.ListAlertRequest;
import com.aliyun.openservices.log.request.ListDashboardRequest;
import com.aliyun.openservices.log.request.UpdateAlertRequest;
import com.aliyun.openservices.log.response.GetAlertResponse;
import com.aliyun.openservices.log.response.ListAlertResponse;
import com.aliyun.openservices.log.response.ListDashboardResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AlertSample {
    public static void main(String args[]) {
        String accessId = "";
        String accessKey = "";

        String project = "ali-cn-devcommon-sls-admin";
        String host = "cn-hangzhou-devcommon-intranet.sls.aliyuncs.com";
        String alertName = "test-alert";

        Client client = new Client(host, accessId, accessKey);
        try {
            // test list jobs
            ListAlertRequest listReq = new ListAlertRequest(project);
            listReq.setOffset(0);
            listReq.setSize(100);
            ListAlertResponse listJobsResponse = client.listAlert(listReq);
            for (Alert item : listJobsResponse.getResults()) {
                client.deleteAlert(new DeleteAlertRequest(project, item.getName()));
            }
            ListDashboardRequest listDashboardRequest = new ListDashboardRequest(project);
            listDashboardRequest.setSize(100);
            listDashboardRequest.setOffset(0);
            ListDashboardResponse listDashboardResponse = client.listDashboard(listDashboardRequest);
            for (Dashboard dashboard : listDashboardResponse.getDashboards()) {
                client.deleteDashboard(new DeleteDashboardRequest(project, dashboard.getDashboardName()));
            }
            Alert alert = new Alert();
            alert.setName(alertName);
            alert.setState(JobState.ENABLED);
            alert.setDisplayName("DisplayName");

            AlertConfiguration configuration = new AlertConfiguration();
            configuration.setCondition("$0.count > 1");

            // The name of dashboard this alert associated to
            configuration.setDashboard("dashboardtest");
            List<Query> queries = new ArrayList<Query>();
            Query query = new Query();
            query.setStart("-60s");
            query.setEnd("now");
            query.setTimeSpanType(TimeSpanType.CUSTOM);
            query.setQuery("* | select count(1) as count");
            query.setLogStore("logStore1");
            query.setChartTitle("chart1");
            queries.add(query);
            configuration.setQueryList(queries);
            EmailNotification notification = new EmailNotification();
            notification.setEmailList(Collections.singletonList("kel@test.com"));
            notification.setContent("Alerting");
            List<Notification> notifications = new ArrayList<Notification>();
            notifications.add(notification);
            configuration.setNotificationList(notifications);
            configuration.setThrottling("0s");
            configuration.setNotifyThreshold(100);
            alert.setConfiguration(configuration);

            JobSchedule schedule = new JobSchedule();
            schedule.setType(JobScheduleType.FIXED_RATE);
            schedule.setInterval("60s");
            alert.setSchedule(schedule);

            // create
            Dashboard dashboard = new Dashboard();
            dashboard.setDashboardName("dashboardtest");
            dashboard.setDescription("Dashboard");
            dashboard.setChartList(new ArrayList<Chart>());
            CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(project, dashboard);
            client.createDashboard(createDashboardRequest);
            CreateAlertRequest request = new CreateAlertRequest(project, alert);
            client.createAlert(request);

            GetAlertResponse response = client.getAlert(new GetAlertRequest(project, alertName));

            Alert created = response.getAlert();
            System.out.println(created.getName());
            System.out.println(created.getDisplayName());
            System.out.println(created.getCreateTime());

            client.disableAlert(new DisableAlertRequest(project, alertName));
            response = client.getAlert(new GetAlertRequest(project, alertName));
            Alert alert1 = response.getAlert();
            System.out.println(alert1.getState());

            client.enableAlert(new EnableAlertRequest(project, alertName));
            response = client.getAlert(new GetAlertRequest(project, alertName));
            Alert alert2 = response.getAlert();
            System.out.println(alert2.getState());

            DisableAlertRequest disableAlertRequest = new DisableAlertRequest(project, alertName);
            client.disableJob(disableAlertRequest);

            response = client.getAlert(new GetAlertRequest(project, alertName));
            Alert alert3 = response.getAlert();
            System.out.println(alert3.getState());

            JobSchedule schedule1 = alert3.getSchedule();
            System.out.println(schedule1.getInterval());
            System.out.println(schedule1.getType());

            Date muteTo = new Date(System.currentTimeMillis() + 60 * 1000);
            alert3.getConfiguration().setMuteUntil(muteTo);
            client.updateAlert(new UpdateAlertRequest(project, alert3));
            response = client.getAlert(new GetAlertRequest(project, alertName));
            for (int i = 0; i < 10; i++) {
                alert.setName("alert-" + i);
                client.createAlert(new CreateAlertRequest(project, alert));
            }
            // test list jobs
            listReq = new ListAlertRequest(project);
            listReq.setOffset(0);
            listReq.setSize(10);
            listJobsResponse = client.listAlert(listReq);
            System.out.println(listJobsResponse.getTotal());
            System.out.println(listJobsResponse.getCount());
        } catch (LogException lex) {
            lex.printStackTrace();
        }
    }
}
