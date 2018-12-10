package com.aliyun.openservices.log.functiontest;


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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AlertFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = "project-intg-1540362408";

    @Before
    public void setUp() throws Exception {
//        client.CreateProject(TEST_PROJECT, "");
    }

    private static String getAlertName() {
        return "alert-" + getNowTimestamp();
    }

    @Test
    public void testCrud() throws Exception {
        // test list jobs
        ListAlertRequest listReq = new ListAlertRequest(TEST_PROJECT);
        listReq.setOffset(0);
        listReq.setSize(100);
        ListAlertResponse listJobsResponse = client.listAlert(listReq);
        for (Alert item : listJobsResponse.getResults()) {
            client.deleteAlert(new DeleteAlertRequest(TEST_PROJECT, item.getName()));
        }
        ListDashboardRequest listDashboardRequest = new ListDashboardRequest(TEST_PROJECT);
        listDashboardRequest.setSize(100);
        listDashboardRequest.setOffset(0);
        ListDashboardResponse listDashboardResponse = client.listDashboard(listDashboardRequest);
        for (Dashboard dashboard : listDashboardResponse.getDashboards()) {
            client.deleteDashboard(new DeleteDashboardRequest(TEST_PROJECT, dashboard.getDashboardName()));
        }
        Alert alert = new Alert();
        String jobName = getAlertName();
        alert.setName(jobName);
        alert.setState(JobState.ENABLED);
        alert.setDisplayName("DisplayName");

        AlertConfiguration configuration = new AlertConfiguration();
        configuration.setCondition("$0.count > 1");
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
        CreateAlertRequest request = new CreateAlertRequest(TEST_PROJECT, alert);
        try {
            client.createAlert(request);
            fail("Dashboard not exist");
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "Dashboard does not exist: " + configuration.getDashboard());
        }
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardName("dashboardtest");
        dashboard.setDescription("Dashboard");
        dashboard.setChartList(new ArrayList<Chart>());
        CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(TEST_PROJECT, dashboard);
        client.createDashboard(createDashboardRequest);

        client.createAlert(request);

        GetAlertResponse response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));

        Alert created = response.getAlert();
        assertEquals(created.getName(), alert.getName());
        assertEquals(created.getState(), alert.getState());
        assertEquals(created.getConfiguration(), alert.getConfiguration());
        assertEquals(created.getSchedule(), alert.getSchedule());

        client.disableAlert(new DisableAlertRequest(TEST_PROJECT, jobName));
        response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));
        Alert alert1 = response.getAlert();
        assertEquals(alert1.getState(), JobState.DISABLED);

        client.enableAlert(new EnableAlertRequest(TEST_PROJECT, jobName));
        response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));
        Alert alert2 = response.getAlert();
        assertEquals(alert2.getState(), JobState.ENABLED);

        DisableAlertRequest disableAlertRequest = new DisableAlertRequest(TEST_PROJECT, jobName);
        client.disableJob(disableAlertRequest);

        response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));
        Alert alert3 = response.getAlert();
        assertEquals(alert3.getState(), JobState.DISABLED);

        JobSchedule schedule1 = alert3.getSchedule();
        assertEquals(schedule1.getInterval(), schedule.getInterval());
        assertEquals(schedule1.getType(), schedule.getType());

        Date muteTo = new Date(System.currentTimeMillis() + 60 * 1000);
        alert3.getConfiguration().setMuteUntil(muteTo);
        client.updateAlert(new UpdateAlertRequest(TEST_PROJECT, alert3));
        response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));
        Alert alert4 = response.getAlert();
        assertEquals(muteTo.getTime() / 1000, alert4.getConfiguration().getMuteUntil().getTime() / 1000);

        for (int i = 0; i < 10; i++) {
            alert.setName("alert-" + i);
            client.createAlert(new CreateAlertRequest(TEST_PROJECT, alert));
        }

        // test list jobs
        listReq = new ListAlertRequest(TEST_PROJECT);
        listReq.setOffset(0);
        listReq.setSize(10);
        listJobsResponse = client.listAlert(listReq);
        assertEquals(11, (int) listJobsResponse.getTotal());
        assertEquals(10, (int) listJobsResponse.getCount());
    }

    @After
    public void tearDown() throws Exception {
//        client.DeleteProject(TEST_PROJECT);
    }
}
