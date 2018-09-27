package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.AlertArguments;
import com.aliyun.openservices.log.common.AlertResource;
import com.aliyun.openservices.log.common.EmailNotification;
import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobStatus;
import com.aliyun.openservices.log.common.JobType;
import com.aliyun.openservices.log.common.Notification;
import com.aliyun.openservices.log.common.Query;
import com.aliyun.openservices.log.common.RetryPolicy;
import com.aliyun.openservices.log.request.CreateJobRequest;
import com.aliyun.openservices.log.request.DisableJobRequest;
import com.aliyun.openservices.log.request.EnableJobRequest;
import com.aliyun.openservices.log.request.GetJobRequest;
import com.aliyun.openservices.log.request.ListJobsRequest;
import com.aliyun.openservices.log.response.GetJobResponse;
import com.aliyun.openservices.log.response.ListJobsResponse;
import com.aliyun.openservices.log.util.JsonUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JobFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = "project1";

    private static String getJobName() {
        return "job-" + getNowTimestamp();
    }

    @Test
    public void testCrud() throws Exception {
        Job job = new Job();
        String jobName = getJobName();
        job.setJobName(jobName);
        job.setType(JobType.Alert);
        job.setDescription("Job desc");
        job.setTimeout(120);
        job.setState(Job.JobState.Enabled);

        AlertArguments arguments = new AlertArguments();
        arguments.setCondition("$0.count > 1");
        AlertResource resource = new AlertResource();
        resource.setDashboard("dashboard1");
        List<Query> queries = new ArrayList<Query>();
        Query query = new Query();
        query.setPeriod(60);
        query.setQuery("* | select count(1) as count");
        query.setLogStore("logStore1");
        query.setChart("chart1");
        queries.add(query);
        resource.setQueryList(queries);
        arguments.setResource(resource);

        EmailNotification notification = new EmailNotification();
        notification.setDashboard("dashboard1");
        notification.setEmailList(Collections.singletonList("kel@test.com"));
        notification.setSubject("Alert fired");
        notification.setContent("Alerting");
        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(notification);
        arguments.setNotificationList(notifications);
        job.setArguments(arguments);

        RetryPolicy retryPolicy = new RetryPolicy();
        retryPolicy.setMaxAttempts(120);
        retryPolicy.setDeltaBackoff(10);
        retryPolicy.setType(RetryPolicy.RetryType.Linear);
        job.setRetryPolicy(retryPolicy);

        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobSchedule.JobScheduleType.FixedTimeInterval);
        schedule.setInterval(60L);
        job.setSchedule(schedule);

        // create
        CreateJobRequest request = new CreateJobRequest(TEST_PROJECT, job);
        client.createJob(request);

        GetJobResponse response = client.getJob(new GetJobRequest(TEST_PROJECT, jobName));

        Job created = response.getJob();
        assertEquals(created.getJobName(), job.getJobName());
        assertEquals(created.getType(), JobType.Alert);
        assertEquals(created.getJobName(), job.getJobName());
        assertEquals(created.getDescription(), job.getDescription());
        assertEquals(created.getTimeout(), job.getTimeout());
        assertEquals(created.getState(), job.getState());
        assertEquals(created.getArguments(), job.getArguments());
        job.getSchedule().setDoNotRunUntil(new Date(0));
        assertEquals(created.getSchedule(), job.getSchedule());
        assertEquals(created.getLogSetting(), job.getLogSetting());
        assertEquals(created.getRetryPolicy(), job.getRetryPolicy());
        assertEquals(created.getStatus(), new JobStatus());

        client.disableJob(new DisableJobRequest(TEST_PROJECT, jobName));
        response = client.getJob(new GetJobRequest(TEST_PROJECT, jobName));
        Job job1 = response.getJob();
        assertEquals(job1.getState(), Job.JobState.Disabled);

        client.enableJob(new EnableJobRequest(TEST_PROJECT, jobName));
        response = client.getJob(new GetJobRequest(TEST_PROJECT, jobName));
        Job job2 = response.getJob();
        assertEquals(job2.getState(), Job.JobState.Enabled);

        // disable for a while
        DisableJobRequest disableForAWhile = new DisableJobRequest(TEST_PROJECT, jobName);

        long now = new Date().getTime();
        Date doNotRunUntil = new Date(now - (now % 1000));
        disableForAWhile.setDisableUntil(doNotRunUntil);
        client.disableJob(disableForAWhile);

        response = client.getJob(new GetJobRequest(TEST_PROJECT, jobName));
        Job job3 = response.getJob();
//        System.out.println(JsonUtils.serialize(job3));
        assertEquals(job3.getState(), Job.JobState.Disabled);

        JobSchedule schedule1 = job3.getSchedule();
        assertEquals(schedule1.getDoNotRunUntil(), doNotRunUntil);
        assertEquals(schedule1.getInterval(), schedule.getInterval());
        assertEquals(schedule1.getType(), schedule.getType());

        for (int i = 0; i < 10; i++) {
            job.setJobName("job-" + i);
            client.createJob(new CreateJobRequest(TEST_PROJECT, job));
        }

        // test list jobs
        ListJobsRequest listReq = new ListJobsRequest(TEST_PROJECT);
        listReq.setOffset(0);
        listReq.setSize(10);
        ListJobsResponse listJobsResponse = client.listJobs(listReq);
        assertEquals(11, listJobsResponse.getTotal());
        assertEquals(10, listJobsResponse.getCount());
    }
}
