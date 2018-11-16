package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.Chart;
import com.aliyun.openservices.log.common.Dashboard;
import com.aliyun.openservices.log.common.EmailNotification;
import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.common.JobState;
import com.aliyun.openservices.log.common.JobType;
import com.aliyun.openservices.log.common.Notification;
import com.aliyun.openservices.log.common.Query;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateDashboardRequest;
import com.aliyun.openservices.log.request.CreateJobRequest;
import com.aliyun.openservices.log.request.DeleteJobRequest;
import com.aliyun.openservices.log.request.DisableJobRequest;
import com.aliyun.openservices.log.request.EnableJobRequest;
import com.aliyun.openservices.log.request.GetJobRequest;
import com.aliyun.openservices.log.request.ListJobsRequest;
import com.aliyun.openservices.log.response.GetJobResponse;
import com.aliyun.openservices.log.response.ListJobsResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JobFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = "project-test-" + getNowTimestamp();

    @Before
    public void setUp() throws Exception {
        client.CreateProject(TEST_PROJECT, "");
    }

    private static String getJobName() {
        return "job-" + getNowTimestamp();
    }

    @Test
    public void testCrud() throws Exception {
        ListJobsRequest listReq = new ListJobsRequest(TEST_PROJECT);
        listReq.setOffset(0);
        listReq.setSize(100);
        ListJobsResponse listJobsResponse = client.listJobs(listReq);
        for (Job job : listJobsResponse.getResults()) {
            client.deleteJob(new DeleteJobRequest(TEST_PROJECT, job.getName()));
        }
        Job job = new Job();
        String jobName = getJobName();
        job.setName(jobName);
        job.setType(JobType.ALERT);
        job.setDescription("Alert desc");
        job.setState(JobState.ENABLED);

        AlertConfiguration arguments = new AlertConfiguration();
        arguments.setCondition("$0.count > 1");
        arguments.setDashboard("dashboard1");
        List<Query> queries = new ArrayList<Query>();
        Query query = new Query();
        query.setDuration("60s");
        query.setQuery("* | select count(1) as count");
        query.setLogStore("logStore1");
        query.setChartTitle("chart1");
        queries.add(query);
        arguments.setQueryList(queries);
        arguments.setThrottling("0s");
        EmailNotification notification = new EmailNotification();
        notification.setEmailList(Collections.singletonList("kel@test.com"));
        notification.setSubject("Alert fired");
        notification.setContent("Alerting");
        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(notification);
        arguments.setNotificationList(notifications);
        job.setConfiguration(arguments);

        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.FIXED_RATE);
        schedule.setInterval("60s");
        job.setSchedule(schedule);

        // create
        CreateJobRequest request = new CreateJobRequest(TEST_PROJECT, job);

        try {
            client.createJob(request);
            fail("Dashboard not exist");
        } catch (LogException ex) {
            assertEquals(ex.GetErrorMessage(), "Dashboard does not exist: dashboard1");
        }
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardName("dashboard1");
        dashboard.setDescription("Dashboard");
        dashboard.setDisplayName("Dashboard");
        dashboard.setChartList(new ArrayList<Chart>());
        CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(TEST_PROJECT, dashboard);
        client.createDashboard(createDashboardRequest);

        client.createJob(request);
        GetJobResponse response = client.getJob(new GetJobRequest(TEST_PROJECT, jobName));

        Job created = response.getJob();
        assertEquals(created.getName(), job.getName());
        assertEquals(created.getType(), JobType.ALERT);
        assertEquals(created.getDescription(), job.getDescription());
        assertEquals(created.getState(), job.getState());
        assertEquals(created.getConfiguration(), job.getConfiguration());
        assertEquals(created.getSchedule(), job.getSchedule());

        client.disableJob(new DisableJobRequest(TEST_PROJECT, jobName));
        response = client.getJob(new GetJobRequest(TEST_PROJECT, jobName));
        Job job1 = response.getJob();
        assertEquals(job1.getState(), JobState.DISABLED);

        client.enableJob(new EnableJobRequest(TEST_PROJECT, jobName));
        response = client.getJob(new GetJobRequest(TEST_PROJECT, jobName));
        Job job2 = response.getJob();
        assertEquals(job2.getState(), JobState.ENABLED);

        DisableJobRequest disableJobRequest = new DisableJobRequest(TEST_PROJECT, jobName);
        client.disableJob(disableJobRequest);

        response = client.getJob(new GetJobRequest(TEST_PROJECT, jobName));
        Job job3 = response.getJob();
        assertEquals(job3.getState(), JobState.DISABLED);

        JobSchedule schedule1 = job3.getSchedule();
        assertEquals(schedule1.getInterval(), schedule.getInterval());
        assertEquals(schedule1.getType(), schedule.getType());

        for (int i = 0; i < 10; i++) {
            job.setName("job-" + i);
            client.createJob(new CreateJobRequest(TEST_PROJECT, job));
        }

        // test list jobs
        listReq = new ListJobsRequest(TEST_PROJECT);
        listReq.setOffset(0);
        listReq.setSize(10);
        listJobsResponse = client.listJobs(listReq);
        assertEquals(11, (int) listJobsResponse.getTotal());
        assertEquals(10, (int) listJobsResponse.getCount());
    }

    @After
    public void tearDown() throws Exception {
        client.DeleteProject(TEST_PROJECT);
    }
}
