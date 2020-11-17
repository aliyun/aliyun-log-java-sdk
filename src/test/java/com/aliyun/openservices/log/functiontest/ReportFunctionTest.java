package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.common.DingTalkNotification;
import com.aliyun.openservices.log.common.EmailNotification;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobState;
import com.aliyun.openservices.log.common.Notification;
import com.aliyun.openservices.log.common.Report;
import com.aliyun.openservices.log.common.ReportConfiguration;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateReportRequest;
import com.aliyun.openservices.log.request.DeleteDashboardRequest;
import com.aliyun.openservices.log.request.DeleteReportRequest;
import com.aliyun.openservices.log.request.DisableReportRequest;
import com.aliyun.openservices.log.request.EnableReportRequest;
import com.aliyun.openservices.log.request.GetReportRequest;
import com.aliyun.openservices.log.request.ListDashboardRequest;
import com.aliyun.openservices.log.request.ListReportRequest;
import com.aliyun.openservices.log.response.GetReportResponse;
import com.aliyun.openservices.log.response.ListDashboardResponse;
import com.aliyun.openservices.log.response.ListReportResponse;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ReportFunctionTest extends JobIntgTest {

    private static String getReportName() {
        return "report-" + getNowTimestamp();
    }

    private Report createReport() {
        Report report = new Report();
        String jobName = getReportName();
        report.setName(jobName);
        report.setState(JobState.ENABLED);
        report.setDisplayName("Report-test");
        ReportConfiguration configuration = new ReportConfiguration();
        configuration.setDashboard(TEST_DASHBOARD);
        configuration.setAllowAnonymousAccess(randomBoolean());
        configuration.setEnableWatermark(randomBoolean());
        configuration.setLanguage("zh");
        EmailNotification notification = new EmailNotification();
        notification.setEmailList(Collections.singletonList("kel@test.com"));
        notification.setContent("Reporting");
        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(notification);
        configuration.setNotificationList(notifications);
        report.setConfiguration(configuration);
        report.setSchedule(createSchedule(true));
        return report;
    }

    @Ignore
    @Test
    public void testCrud() throws Exception {
        // test list jobs
        ListReportRequest listReq = new ListReportRequest(TEST_PROJECT);
        listReq.setOffset(0);
        listReq.setSize(100);
        ListReportResponse listJobsResponse = client.listReport(listReq);
        for (Report item : listJobsResponse.getResults()) {
            client.deleteReport(new DeleteReportRequest(TEST_PROJECT, item.getName()));
        }
        ListDashboardRequest listDashboardRequest = new ListDashboardRequest(TEST_PROJECT);
        listDashboardRequest.setSize(100);
        listDashboardRequest.setOffset(0);
        ListDashboardResponse listDashboardResponse = client.listDashboard(listDashboardRequest);
        for (Dashboard dashboard : listDashboardResponse.getDashboards()) {
            client.deleteDashboard(new DeleteDashboardRequest(TEST_PROJECT, dashboard.getDashboardName()));
        }
        Report report = createReport();
        String jobName = report.getName();
        // create
        CreateReportRequest request = new CreateReportRequest(TEST_PROJECT, report);
        try {
            client.createReport(request);
            fail("Dashboard not exist");
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "Dashboard does not exist: " + report.getConfiguration().getDashboard());
        }
        createDashboard();
        client.createReport(request);
        GetReportResponse response = client.getReport(new GetReportRequest(TEST_PROJECT, jobName));

        Report created = response.getReport();
        assertEquals(created.getName(), report.getName());
        assertEquals(created.getState(), report.getState());
        assertEquals(created.getConfiguration(), report.getConfiguration());
//        assertEquals(created.getSchedule(), report.getSchedule());

        client.disableReport(new DisableReportRequest(TEST_PROJECT, jobName));
        response = client.getReport(new GetReportRequest(TEST_PROJECT, jobName));
        Report Report1 = response.getReport();
        assertEquals(Report1.getState(), JobState.DISABLED);

        client.enableReport(new EnableReportRequest(TEST_PROJECT, jobName));
        response = client.getReport(new GetReportRequest(TEST_PROJECT, jobName));
        Report Report2 = response.getReport();
        assertEquals(Report2.getState(), JobState.ENABLED);

        DisableReportRequest disableReportRequest = new DisableReportRequest(TEST_PROJECT, jobName);
        client.disableJob(disableReportRequest);

        response = client.getReport(new GetReportRequest(TEST_PROJECT, jobName));
        Report report3 = response.getReport();
        assertEquals(report3.getState(), JobState.DISABLED);

        JobSchedule schedule1 = report3.getSchedule();
        JobSchedule schedule = report.getSchedule();
        assertEquals(schedule1.getInterval(), schedule.getInterval());
        assertEquals(schedule1.getType(), schedule.getType());

        for (int i = 0; i < 10; i++) {
            report.setName("report-" + i);
            client.createReport(new CreateReportRequest(TEST_PROJECT, report));
        }

        // test list jobs
        listReq = new ListReportRequest(TEST_PROJECT);
        listReq.setOffset(0);
        listReq.setSize(10);
        listJobsResponse = client.listReport(listReq);
        assertEquals(11, (int) listJobsResponse.getTotal());
        assertEquals(10, (int) listJobsResponse.getCount());

        client.deleteDashboard(new DeleteDashboardRequest(TEST_PROJECT, TEST_DASHBOARD));
    }

    @Test
    public void testCreateDingTalkWithTitle() throws Exception {
        createDashboard();
        Report report = createReport();
        ReportConfiguration configuration = report.getConfiguration();
        DingTalkNotification notification = new DingTalkNotification();
        notification.setTitle("Title-test");
        notification.setAtMobiles(Arrays.asList("123456"));
        notification.setServiceUri("https://testurl");
        configuration.getNotificationList().add(notification);
        client.createReport(new CreateReportRequest(TEST_PROJECT, report));

        GetReportResponse response = client.getReport(new GetReportRequest(TEST_PROJECT, report.getName()));
        Report report2 = response.getReport();
        List<Notification> notifications = report2.getConfiguration().getNotificationList();
        for (Notification notification1 : notifications) {
            if (notification1 instanceof DingTalkNotification) {
                DingTalkNotification dtk = (DingTalkNotification) notification1;
                assertEquals(notification.getAtMobiles(), dtk.getAtMobiles());
                assertEquals(notification.getServiceUri(), dtk.getServiceUri());
                assertEquals(notification.getTitle(), dtk.getTitle());
            }
        }
        client.deleteReport(new DeleteReportRequest(TEST_PROJECT, report.getName()));
    }

    @Test
    public void testCreateEmail() throws Exception {
        createDashboard();
        Report report = createReport();
        ReportConfiguration configuration = report.getConfiguration();
        EmailNotification notification = new EmailNotification();
        notification.setSubject("Title-test");
        notification.setEmailList(Arrays.asList("abc@abc.com"));
        configuration.getNotificationList().clear();
        configuration.getNotificationList().add(notification);
        client.createReport(new CreateReportRequest(TEST_PROJECT, report));

        GetReportResponse response = client.getReport(new GetReportRequest(TEST_PROJECT, report.getName()));
        Report report2 = response.getReport();
        List<Notification> notifications = report2.getConfiguration().getNotificationList();
        for (Notification notification1 : notifications) {
            if (notification1 instanceof EmailNotification) {
                EmailNotification dtk = (EmailNotification) notification1;
                assertEquals(notification.getSubject(), dtk.getSubject());
                assertEquals(notification.getEmailList(), dtk.getEmailList());
            }
        }
        client.deleteReport(new DeleteReportRequest(TEST_PROJECT, report.getName()));
    }
}
