package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JobTest {

    @Test
    public void testSerialize() {
        Job job = new Job();
        job.setName("alertTest");
        job.setState(JobState.ENABLED);
        job.setType(JobType.ALERT);
        JobSchedule schedule = new JobSchedule();
        schedule.setInterval("60s");
        schedule.setType(JobScheduleType.FIXED_RATE);
        job.setSchedule(schedule);

        AlertConfiguration configuration = new AlertConfiguration();
        configuration.setCondition("ID > 100");
        List<Query> queryList = new ArrayList<Query>();
        Query query = new Query();
        query.setStart("-60s");
        query.setEnd("now");
        query.setTimeSpanType(TimeSpanType.CUSTOM);
        query.setChartTitle("chart1");
        query.setLogStore("logstore-test");
        query.setQuery("*");
        queryList.add(query);
        configuration.setQueryList(queryList);

        List<Notification> notifications = new ArrayList<Notification>();
        SmsNotification smsNotification = new SmsNotification();
        smsNotification.setMobileList(Collections.singletonList("86-13738162867"));
        smsNotification.setContent("messagetest");
        notifications.add(smsNotification);
        configuration.setNotificationList(notifications);
        configuration.setDashboard("dashboardtest");
        job.setConfiguration(configuration);

        String body = JsonUtils.serialize(job);
        assertEquals(body, "{\"configuration\":{\"condition\":\"ID > 100\",\"dashboard\":\"dashboardtest\"," +
                "\"noDataFire\":false,\"noDataSeverity\":6,\"notificationList\":" +
                "[{\"content\":\"messagetest\",\"mobileList\":[\"86-13738162867\"],\"type\":\"SMS\"}]," +
                "\"notifyThreshold\":1,\"queryList\":[{\"chartTitle\":\"chart1\",\"end\":\"now\"," +
                "\"logStore\":\"logstore-test\",\"query\":\"*\",\"start\":\"-60s\",\"timeSpanType\":\"Custom\"}]," +
                "\"sendRecoveryMessage\":false,\"sendResolved\":false,\"threshold\":1}," +
                "\"name\":\"alertTest\",\"schedule\":{\"interval\":\"60s\",\"runImmediately\":false," +
                "\"type\":\"FixedRate\"},\"state\":\"Enabled\",\"type\":\"Alert\"}");
    }

    @Test
    public void testDeserialize() {
        String body = "{\"configuration\":{\"condition\":\"ID > 100\",\"dashboard\":\"dashboardtest\"," +
                "\"notificationList\":[{\"content\":\"messagetest\",\"mobileList\":[\"86-13738162867\"]," +
                "\"type\":\"SMS\"}],\"notifyThreshold\":1,\"queryList\":[{\"chartTitle\":\"chart1\",\"end\":" +
                "\"now\",\"logStore\":\"logstore-test\",\"query\":\"*\",\"start\":\"-60s\",\"timeSpanType\":" +
                "\"Custom\"}]},\"name\":\"alertTest\",\"schedule\":{\"interval\":\"60s\"," +
                "\"type\":\"FixedRate\"},\"state\":\"Enabled\",\"type\":\"Alert\",\"createTime\":1542763714,\"lastModifiedTime\":1542763714}";
        Job job = new Job();
        job.deserialize(JSONObject.parseObject(body));

        assertEquals("alertTest", job.getName());
        assertNull(job.getDescription());
        assertEquals(JobState.ENABLED, job.getState());
        assertEquals(JobType.ALERT, job.getType());

        JobSchedule schedule = new JobSchedule();
        schedule.setInterval("60s");
        schedule.setType(JobScheduleType.FIXED_RATE);
        assertEquals(schedule, job.getSchedule());
        AlertConfiguration configuration = new AlertConfiguration();
        configuration.setCondition("ID > 100");
        configuration.setDashboard("dashboardtest");

        List<Query> queryList = new ArrayList<Query>();
        Query query = new Query();
        query.setStart("-60s");
        query.setEnd("now");
        query.setTimeSpanType(TimeSpanType.CUSTOM);
        query.setChartTitle("chart1");
        query.setLogStore("logstore-test");
        query.setQuery("*");
        queryList.add(query);
        configuration.setQueryList(queryList);

        List<Notification> notifications = new ArrayList<Notification>();
        SmsNotification smsNotification = new SmsNotification();
        smsNotification.setMobileList(Collections.singletonList("86-13738162867"));
        smsNotification.setContent("messagetest");
        notifications.add(smsNotification);
        configuration.setNotificationList(notifications);
        assertEquals(configuration, job.getConfiguration());
    }

    @Test
    public void testScheduleSerializeAndDeserialize() {
        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.FIXED_RATE);
        schedule.setInterval("60s");
        assertEquals("{\"interval\":\"60s\",\"runImmediately\":false,\"type\":\"FixedRate\"}", JsonUtils.serialize(schedule));
        JobSchedule schedule1 = new JobSchedule();
        schedule1.deserialize(JSONObject.parseObject("{\"interval\":\"60s\",\"type\":\"FixedRate\"}"));
        assertEquals("60s", schedule1.getInterval());
        assertEquals(JobScheduleType.FIXED_RATE, schedule1.getType());
    }

    @Test
    public void testDeserializeIngestion() {
        String body = "{\n" +
                "\"configuration\": {\n" +
                "   \"logstore\": \"aliyun_bill\",\n" +
                "   \"source\": {\n" +
                "      \"type\": \"AliyunBSS\",\n" +
                "       \"roleARN\": \"acs:ram::1654218965343050:role/aliyunlogimportbssrole\",\n" +
                "       \"historyMonth\": 6\n" +
                "   }\n" +
                "},\n" +
                "\"displayName\": \"ingestion\",\n" +
                "\"name\": \"ingestion-muzi-test\",\n" +
                "\"schedule\": {\n" +
                "   \"delay\": 0,\n" +
                "   \"type\": \"FixedRate\",\n" +
                "   \"interval\": \"1h\"\n" +
                "},\n" +
                "\"state\": \"Enabled\",\n" +
                "\"type\": \"Ingestion\"\n" +
                "}";
        Job job = new Job();
        job.deserialize(JSONObject.parseObject(body));
        assertEquals(JobType.INGESTION, job.getType());
    }
}
