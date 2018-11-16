package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.AlertV2;
import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.common.EmailNotification;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.common.JobState;
import com.aliyun.openservices.log.common.Notification;
import com.aliyun.openservices.log.common.Query;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateAlertRequestV2;
import com.aliyun.openservices.log.request.CreateDashboardRequest;
import com.aliyun.openservices.log.request.DeleteAlertRequestV2;
import com.aliyun.openservices.log.request.DeleteDashboardRequest;
import com.aliyun.openservices.log.request.DisableAlertRequest;
import com.aliyun.openservices.log.request.EnableAlertRequest;
import com.aliyun.openservices.log.request.GetAlertRequestV2;
import com.aliyun.openservices.log.request.ListAlertRequestV2;
import com.aliyun.openservices.log.request.ListDashboardRequest;
import com.aliyun.openservices.log.request.UpdateAlertRequestV2;
import com.aliyun.openservices.log.response.GetAlertResponseV2;
import com.aliyun.openservices.log.response.ListAlertResponseV2;
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
        ListAlertRequestV2 listReq = new ListAlertRequestV2(TEST_PROJECT);
        listReq.setOffset(0);
        listReq.setSize(100);
        ListAlertResponseV2 listJobsResponse = client.listAlert(listReq);
        for (AlertV2 item : listJobsResponse.getResults()) {
            client.deleteAlert(new DeleteAlertRequestV2(TEST_PROJECT, item.getName()));
        }
        ListDashboardRequest listDashboardRequest = new ListDashboardRequest(TEST_PROJECT);
        listDashboardRequest.setSize(100);
        listDashboardRequest.setOffset(0);
        ListDashboardResponse listDashboardResponse = client.listDashboard(listDashboardRequest);
        for (Dashboard dashboard : listDashboardResponse.getDashboards()) {
            client.deleteDashboard(new DeleteDashboardRequest(TEST_PROJECT, dashboard.getDashboardName()));
        }
        AlertV2 alertV2 = new AlertV2();
        String jobName = getAlertName();
        alertV2.setName(jobName);
        alertV2.setState(JobState.ENABLED);
        alertV2.setDisplayName("DisplayName");

        AlertConfiguration configuration = new AlertConfiguration();
        configuration.setCondition("$0.count > 1");
        configuration.setDashboard("dashboardtest");
        List<Query> queries = new ArrayList<Query>();
        Query query = new Query();
        query.setDuration("60s");
        query.setQuery("* | select count(1) as count");
        query.setLogStore("logStore1");
        query.setChartTitle("chart1");
        queries.add(query);
        configuration.setQueryList(queries);
        EmailNotification notification = new EmailNotification();
        notification.setEmailList(Collections.singletonList("kel@test.com"));
        notification.setSubject("Alert fired");
        notification.setContent("Alerting");
        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(notification);
        configuration.setNotificationList(notifications);
        configuration.setThrottling("0s");
        configuration.setNotifyThreshold(100);
        alertV2.setConfiguration(configuration);

        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.FIXED_RATE);
        schedule.setInterval("60s");
        alertV2.setSchedule(schedule);

        // create
        CreateAlertRequestV2 request = new CreateAlertRequestV2(TEST_PROJECT, alertV2);
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

        GetAlertResponseV2 response = client.getAlert(new GetAlertRequestV2(TEST_PROJECT, jobName));

        AlertV2 created = response.getAlert();
        assertEquals(created.getName(), alertV2.getName());
        assertEquals(created.getState(), alertV2.getState());
        assertEquals(created.getConfiguration(), alertV2.getConfiguration());
        assertEquals(created.getSchedule(), alertV2.getSchedule());

        client.disableAlert(new DisableAlertRequest(TEST_PROJECT, jobName));
        response = client.getAlert(new GetAlertRequestV2(TEST_PROJECT, jobName));
        AlertV2 alertV21 = response.getAlert();
        assertEquals(alertV21.getState(), JobState.DISABLED);

        client.enableAlert(new EnableAlertRequest(TEST_PROJECT, jobName));
        response = client.getAlert(new GetAlertRequestV2(TEST_PROJECT, jobName));
        AlertV2 alertV22 = response.getAlert();
        assertEquals(alertV22.getState(), JobState.ENABLED);

        DisableAlertRequest disableAlertRequest = new DisableAlertRequest(TEST_PROJECT, jobName);
        client.disableJob(disableAlertRequest);

        response = client.getAlert(new GetAlertRequestV2(TEST_PROJECT, jobName));
        AlertV2 alertV23 = response.getAlert();
        assertEquals(alertV23.getState(), JobState.DISABLED);

        JobSchedule schedule1 = alertV23.getSchedule();
        assertEquals(schedule1.getInterval(), schedule.getInterval());
        assertEquals(schedule1.getType(), schedule.getType());

        Date muteTo = new Date(System.currentTimeMillis() + 60 * 1000);
        alertV23.getConfiguration().setMuteUntil(muteTo);
        client.updateAlert(new UpdateAlertRequestV2(TEST_PROJECT, alertV23));
        response = client.getAlert(new GetAlertRequestV2(TEST_PROJECT, jobName));
        AlertV2 alertV24 = response.getAlert();
        assertEquals(muteTo.getTime() / 1000, alertV24.getConfiguration().getMuteUntil().getTime() / 1000);

        for (int i = 0; i < 10; i++) {
            alertV2.setName("alert-" + i);
            client.createAlert(new CreateAlertRequestV2(TEST_PROJECT, alertV2));
        }

        // test list jobs
        listReq = new ListAlertRequestV2(TEST_PROJECT);
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
