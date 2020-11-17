package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.common.DingTalkNotification;
import com.aliyun.openservices.log.common.EmailNotification;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobState;
import com.aliyun.openservices.log.common.Notification;
import com.aliyun.openservices.log.common.NotificationType;
import com.aliyun.openservices.log.common.Query;
import com.aliyun.openservices.log.common.TimeSpanType;
import com.aliyun.openservices.log.common.VoiceNotification;
import com.aliyun.openservices.log.common.WebhookNotification;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.request.CreateAlertRequest;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AlertFunctionTest extends JobIntgTest {

    private static String getAlertName() {
        return "alert-" + getNowTimestamp();
    }

    private Alert createAlert() {
        Alert alert = new Alert();
        String jobName = getAlertName();
        alert.setName(jobName);
        alert.setState(JobState.ENABLED);
        alert.setDisplayName("Alert-test");
        AlertConfiguration configuration = new AlertConfiguration();
        configuration.setCondition("$0.count > 1");
        configuration.setDashboard(TEST_DASHBOARD);
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
        alert.setSchedule(createSchedule(true));
        return alert;
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

        Alert alert = createAlert();
        String jobName = alert.getName();
        // create
        CreateAlertRequest request = new CreateAlertRequest(TEST_PROJECT, alert);
        try {
            client.createAlert(request);
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "Dashboard does not exist: " + alert.getConfiguration().getDashboard());
        }
        createDashboard();
        try {
            client.createAlert(request);
        } catch (LogException ex) {
            assertEquals("Job " + jobName +" already exists", ex.GetErrorMessage());
        }
        GetAlertResponse response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));

        Alert created = response.getAlert();
        assertEquals(created.getName(), alert.getName());
        assertEquals(created.getState(), alert.getState());
        assertEquals(created.getConfiguration(), alert.getConfiguration());
//        assertEquals(created.getSchedule(), alert.getSchedule());

        client.disableAlert(new DisableAlertRequest(TEST_PROJECT, jobName));
        response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));
        Alert alert1 = response.getAlert();
        assertEquals(alert1.getState(), JobState.DISABLED);
        assertEquals(alert1.getStatus(), "DISABLED");

        client.enableAlert(new EnableAlertRequest(TEST_PROJECT, jobName));
        response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));
        Alert alert2 = response.getAlert();
        assertEquals(alert2.getState(), JobState.ENABLED);
        assertEquals(alert2.getStatus(), "ENABLED");

        DisableAlertRequest disableAlertRequest = new DisableAlertRequest(TEST_PROJECT, jobName);
        client.disableJob(disableAlertRequest);

        response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));
        Alert alert3 = response.getAlert();
        assertEquals(alert3.getState(), JobState.DISABLED);
        assertEquals(alert3.getStatus(), "DISABLED");

        alert3.setState(JobState.ENABLED);
        client.updateAlert(new UpdateAlertRequest(TEST_PROJECT, alert3));
        response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));
        Alert alert4 = response.getAlert();
        assertEquals(alert4.getState(), JobState.ENABLED);
        assertEquals(alert4.getStatus(), "ENABLED");

        JobSchedule schedule1 = alert3.getSchedule();
        JobSchedule schedule = alert.getSchedule();
        assertEquals(schedule1.getInterval(), schedule.getInterval());
        assertEquals(schedule1.getType(), schedule.getType());

        Date muteTo = new Date(System.currentTimeMillis() + 60 * 1000);
        alert3.getConfiguration().setMuteUntil(muteTo);
        client.updateAlert(new UpdateAlertRequest(TEST_PROJECT, alert3));
        response = client.getAlert(new GetAlertRequest(TEST_PROJECT, jobName));
        Alert alert5 = response.getAlert();
        assertEquals(muteTo.getTime() / 1000, alert5.getConfiguration().getMuteUntil().getTime() / 1000);

        for (int i = 0; i < 10; i++) {
            alert.setName("alert-" + i);
            JobState state = randomBoolean() ? JobState.ENABLED : JobState.DISABLED;
            alert.setState(state);
            client.createAlert(new CreateAlertRequest(TEST_PROJECT, alert));
            response = client.getAlert(new GetAlertRequest(TEST_PROJECT, alert.getName()));
            Alert alert6 = response.getAlert();
            assertEquals(state, alert6.getState());
            assertEquals(state == JobState.ENABLED ? "ENABLED" : "DISABLED", alert6.getStatus());
        }

        // test list jobs
        listReq = new ListAlertRequest(TEST_PROJECT);
        listReq.setOffset(0);
        listReq.setSize(10);
        listJobsResponse = client.listAlert(listReq);
        assertEquals(11, (int) listJobsResponse.getTotal());
        assertEquals(10, (int) listJobsResponse.getCount());
    }

    @Test
    public void testCreateDingTalkWithTitle() throws Exception {
        createDashboard();
        Alert alert = createAlert();
        AlertConfiguration configuration = alert.getConfiguration();
        DingTalkNotification notification = new DingTalkNotification();
        notification.setTitle("Title-test");
        notification.setAtMobiles(Arrays.asList("123456"));
        notification.setServiceUri("https://testurl");
        configuration.getNotificationList().add(notification);
        client.createAlert(new CreateAlertRequest(TEST_PROJECT, alert));

        GetAlertResponse response = client.getAlert(new GetAlertRequest(TEST_PROJECT, alert.getName()));
        Alert alert2 = response.getAlert();
        List<Notification> notifications = alert2.getConfiguration().getNotificationList();
        for (Notification notification1 : notifications) {
            if (notification1 instanceof DingTalkNotification) {
                DingTalkNotification dtk = (DingTalkNotification) notification1;
                assertEquals(notification.getAtMobiles(), dtk.getAtMobiles());
                assertEquals(notification.getServiceUri(), dtk.getServiceUri());
                assertEquals(notification.getTitle(), dtk.getTitle());
            }
        }
        client.deleteAlert(new DeleteAlertRequest(TEST_PROJECT, alert.getName()));
    }

    //    @Ignore
    @Test
    public void testCreateWebHookWithHeaders() throws Exception {
        createDashboard();
        Alert alert = createAlert();
        AlertConfiguration configuration = alert.getConfiguration();
        WebhookNotification notification = new WebhookNotification();
        notification.setServiceUri("http://testurl");
        notification.setMethod(HttpMethod.POST);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x", "y");
        headers.put("foo", "bar");
        notification.setHeaders(headers);

        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(notification);
        configuration.setNotificationList(notifications);
        client.createAlert(new CreateAlertRequest(TEST_PROJECT, alert));

        GetAlertResponse response = client.getAlert(new GetAlertRequest(TEST_PROJECT, alert.getName()));
        Alert alert2 = response.getAlert();
        List<Notification> notifications2 = alert2.getConfiguration().getNotificationList();
        WebhookNotification notification1 = (WebhookNotification) notifications2.get(0);
        assertEquals(notification.getServiceUri(), notification1.getServiceUri());
        assertEquals(notification.getMethod(), notification1.getMethod());
        assertEquals(notification.getHeaders(), notification1.getHeaders());

        client.deleteAlert(new DeleteAlertRequest(TEST_PROJECT, alert.getName()));
    }

    @Test
    public void testCreateVoiceNotification() throws Exception {
        createDashboard();
        Alert alert = createAlert();
        AlertConfiguration configuration = alert.getConfiguration();
        VoiceNotification notification = new VoiceNotification();
        notification.setMobileList(Arrays.asList("123-4567-8901"));
        notification.setContent("alert fired");
        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(notification);
        configuration.setNotificationList(notifications);
        client.createAlert(new CreateAlertRequest(TEST_PROJECT, alert));

        GetAlertResponse response = client.getAlert(new GetAlertRequest(TEST_PROJECT, alert.getName()));
        Alert alert2 = response.getAlert();
        List<Notification> notifications2 = alert2.getConfiguration().getNotificationList();
        VoiceNotification notification1 = (VoiceNotification) notifications2.get(0);
        assertEquals(NotificationType.VOICE, notification1.getType());
        assertEquals(notification.getMobileList(), notification1.getMobileList());
        assertEquals(notification.getContent(), notification1.getContent());

        client.deleteAlert(new DeleteAlertRequest(TEST_PROJECT, alert.getName()));
    }

    //    @Ignore
    @Test
    public void testCreateEmail() throws Exception {
        createDashboard();
        Alert alert = createAlert();
        AlertConfiguration configuration = alert.getConfiguration();
        EmailNotification notification = new EmailNotification();
        notification.setSubject("Title-test");
        notification.setEmailList(Arrays.asList("abc@abc.com"));
        configuration.getNotificationList().clear();
        configuration.getNotificationList().add(notification);
        client.createAlert(new CreateAlertRequest(TEST_PROJECT, alert));

        GetAlertResponse response = client.getAlert(new GetAlertRequest(TEST_PROJECT, alert.getName()));
        Alert alert1 = response.getAlert();
        List<Notification> notifications = alert1.getConfiguration().getNotificationList();
        for (Notification notification1 : notifications) {
            if (notification1 instanceof EmailNotification) {
                EmailNotification dtk = (EmailNotification) notification1;
                assertEquals(notification.getSubject(), dtk.getSubject());
                assertEquals(notification.getEmailList(), dtk.getEmailList());
            }
        }
        client.deleteAlert(new DeleteAlertRequest(TEST_PROJECT, alert.getName()));
    }
}
