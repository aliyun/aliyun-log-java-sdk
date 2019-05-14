package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.common.DingTalkNotification;
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
import com.aliyun.openservices.log.request.DisableAlertRequest;
import com.aliyun.openservices.log.request.EnableAlertRequest;
import com.aliyun.openservices.log.request.GetAlertRequest;
import com.aliyun.openservices.log.request.ListAlertRequest;
import com.aliyun.openservices.log.request.UpdateAlertRequest;
import com.aliyun.openservices.log.response.GetAlertResponse;
import com.aliyun.openservices.log.response.ListAlertResponse;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AlertSample {

    public static void main(String[] args) {
        String accessId = "";
        String accessKey = "";

        String project = "ali-cn-devcommon-sls-admin";
        String host = "cn-hangzhou-devcommon-intranet.sls.aliyuncs.com";
        String alertName = "test-alert";
        String dashboardName = "dashboardtest";
        String logstore = "logstore-test";

        Client client = new Client(host, accessId, accessKey);
        try {
            // create a dashboard first
            Dashboard dashboard = new Dashboard();
            dashboard.setDashboardName(dashboardName);
            dashboard.setDescription("Dashboard");

            // add chart to dashboard
            Chart chart = new Chart();
            chart.setDisplayName("chart-test");
            chart.setQuery("* | select count(1) as count");
            chart.setLogstore(logstore);
            chart.setTitle("chart-1234567");
            chart.setType("table");
            chart.setTopic("");
            chart.setHeight(5);
            chart.setWidth(5);
            chart.setStart("-360s");
            chart.setEnd("now");
            chart.setxPosition(0);
            chart.setyPosition(-1);
            JSONObject searchAttr = new JSONObject();
            searchAttr.put("logstore", logstore);
            searchAttr.put("start", chart.getStart());
            searchAttr.put("end", chart.getEnd());
            searchAttr.put("topic", "");
            searchAttr.put("query", chart.getQuery());
            searchAttr.put("timeSpanType", "custom");
            chart.setRawSearchAttr(searchAttr.toString());
            ArrayList<Chart> charts = new ArrayList<Chart>();
            charts.add(chart);
            dashboard.setChartList(charts);
            CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(project, dashboard);
            try {
                client.createDashboard(createDashboardRequest);
            } catch (LogException ex) {
                if (!ex.GetErrorMessage().equals("specified dashboard already exists")) {
                    throw ex;
                }
            }
            Alert alert = new Alert();
            alert.setName(alertName);
            alert.setState(JobState.ENABLED);
            alert.setDisplayName("count monitoring");
            AlertConfiguration configuration = new AlertConfiguration();
            configuration.setCondition("count > 1");
            // The name of dashboard this alert associated to
            configuration.setDashboard(dashboardName);
            List<Query> queries = new ArrayList<Query>();
            Query query = new Query();
            query.setStart("-360s");
            query.setEnd("now");
            query.setTimeSpanType(TimeSpanType.CUSTOM);
            query.setQuery(chart.getQuery());
            query.setLogStore(logstore);
            query.setChartTitle(chart.getTitle());
            queries.add(query);
            configuration.setQueryList(queries);

            // Send email if alert fired
            EmailNotification notification = new EmailNotification();
            notification.setEmailList(Collections.singletonList("kel@test.com"));
            notification.setContent("Alerting");
            List<Notification> notifications = new ArrayList<Notification>();
            notifications.add(notification);

            // Send Ding Ding if alert fired
            DingTalkNotification dingTalkNotification = new DingTalkNotification();
            dingTalkNotification.setServiceUri("https://oapi.dingtalk.com/robot/send?access_token=xxx");
            dingTalkNotification.setContent("Ding talk message");
            notifications.add(dingTalkNotification);

            // send voice, sms, webhook, etc

            configuration.setNotificationList(notifications);
            configuration.setThrottling("0s");
            configuration.setNotifyThreshold(100);
            alert.setConfiguration(configuration);

            JobSchedule schedule = new JobSchedule();
            schedule.setType(JobScheduleType.FIXED_RATE);
            schedule.setInterval("60s");
            alert.setSchedule(schedule);
            client.createAlert(new CreateAlertRequest(project, alert));

            // Get alert rule by name
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
            for (int i = 0; i < 10; i++) {
                alert.setName("alert-" + i);
                client.createAlert(new CreateAlertRequest(project, alert));
            }
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
        } catch (LogException lex) {
            lex.printStackTrace();
        }
    }
}
