package com.aliyun.openservices.log.internal.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.GeneralJobConfiguration;
import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.common.JobState;
import com.aliyun.openservices.log.common.JobType;
import com.aliyun.openservices.log.common.Log2MetricParameters;
import com.aliyun.openservices.log.common.Notification;
import com.aliyun.openservices.log.common.Query;
import com.aliyun.openservices.log.common.ScheduledSQLConfiguration;
import com.aliyun.openservices.log.common.SmsNotification;
import com.aliyun.openservices.log.common.TimeSpanType;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertNull;

/**
 * Golden-baseline tests: serialization output must stay semantically identical
 * to the legacy fastjson output (tree compare, key order ignored). The golden
 * strings below were captured while fastjson was still the serializer.
 */
public class SerializationGoldenTest {

    @Test
    public void testNullGeneralJobConfigurationGolden() throws Exception {
        assertNull(new GeneralJobConfiguration().toJsonObject());

        Job job = new Job();
        job.setName("general-job");
        job.setType(JobType.DOWN_SAMPLING);
        job.setConfiguration(new GeneralJobConfiguration());
        assertJsonEquals("{\"name\":\"general-job\",\"type\":\"DownSampling\"}",
                job.toJsonString());
    }

    @Test
    public void testGeneralJobConfigurationOmitsExplicitNullGolden() throws Exception {
        Job job = new Job();
        job.setName("general-job");
        job.setType(JobType.DOWN_SAMPLING);
        job.setConfiguration(new GeneralJobConfiguration(
                "{\"explicit\":null,\"kept\":1}"));

        assertJsonEquals("{\"name\":\"general-job\",\"type\":\"DownSampling\","
                        + "\"configuration\":{\"kept\":1}}",
                job.toJsonString());
    }

    @Test
    public void testAlertJobGolden() throws Exception {
        Job job = new Job();
        job.setName("alertTest");
        job.setState(JobState.ENABLED);
        job.setType(JobType.ALERT);
        JobSchedule schedule = new JobSchedule();
        schedule.setInterval("60s");
        schedule.setType(JobScheduleType.FIXED_RATE);
        job.setSchedule(schedule);

        AlertConfiguration configuration = new AlertConfiguration();
        configuration.setCondition("ID > 100 && name < 'x' & y");
        List<Query> queryList = new ArrayList<Query>();
        Query query = new Query();
        query.setStart("-60s");
        query.setEnd("now");
        query.setTimeSpanType(TimeSpanType.CUSTOM);
        query.setChartTitle("chart1");
        query.setLogStore("logstore-test");
        query.setQuery("* | select count(*) as cnt");
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

        assertJsonEquals("{\"type\":\"FixedRate\",\"interval\":\"60s\",\"runImmediately\":false}",
                schedule.toJsonString());
        String configGolden = "{\"condition\":\"ID > 100 && name < 'x' & y\","
                + "\"queryList\":[{\"chartTitle\":\"chart1\",\"query\":\"* | select count(*) as cnt\","
                + "\"logStore\":\"logstore-test\",\"timeSpanType\":\"Custom\",\"start\":\"-60s\",\"end\":\"now\"}],"
                + "\"notifyThreshold\":1,\"sendRecoveryMessage\":false,\"autoAnnotation\":false,\"threshold\":1,"
                + "\"noDataFire\":false,\"noDataSeverity\":6,\"sendResolved\":false,\"dashboard\":\"dashboardtest\","
                + "\"notificationList\":[{\"mobileList\":[\"86-13738162867\"],\"type\":\"SMS\",\"content\":\"messagetest\"}]}";
        assertJsonEquals(configGolden, configuration.toJsonString());
        assertJsonEquals("{\"name\":\"alertTest\",\"type\":\"Alert\","
                + "\"schedule\":{\"type\":\"FixedRate\",\"interval\":\"60s\",\"runImmediately\":false},"
                + "\"state\":\"Enabled\",\"configuration\":" + configGolden + "}",
                job.toJsonString());
    }

    @Test
    public void testScheduledSqlGolden() throws Exception {
        ScheduledSQLConfiguration config = new ScheduledSQLConfiguration();
        config.setSourceLogstore("source-logstore");
        config.setDestProject("dest-project");
        config.setDestEndpoint("cn-hangzhou.log.aliyuncs.com");
        config.setDestLogstore("dest-logstore");
        config.setScript("* | select time, count(*) as cnt group by time");
        config.setRoleArn("acs:ram::123:role/aliyunlogetlrole");
        config.setDestRoleArn("acs:ram::123:role/aliyunlogetlrole");
        config.setFromTimeExpr("@m-1m");
        config.setToTimeExpr("@m");
        config.setMaxRunTimeInSeconds(60);
        config.setMaxRetries(3);
        config.setFromTime(1648105200L);
        config.setToTime(0L);
        config.setDataFormat("log2metric");

        Log2MetricParameters params = new Log2MetricParameters();
        params.withFields("timeKey", "metricKeys", "labelKeys", "hashLabels", "addLabels");
        params.addBaseParams("__task_type__", "log2metric");
        params.setTimeKey("time");
        params.setMetricKeys("cnt");
        params.setLabelKeys("host");
        params.setHashLabels("false");
        params.setAddLabels("env=prod");
        config.setParameters(params);

        // baseParams flattened into the parent, internal "fields" set dropped
        String paramsGolden = "{\"timeKey\":\"time\",\"metricKeys\":\"cnt\",\"labelKeys\":\"host\","
                + "\"hashLabels\":\"false\",\"addLabels\":\"env=prod\",\"__task_type__\":\"log2metric\"}";
        assertJsonEquals(paramsGolden, params.toJsonObject().toString());

        String configGolden = "{\"sourceLogstore\":\"source-logstore\",\"destProject\":\"dest-project\","
                + "\"destEndpoint\":\"cn-hangzhou.log.aliyuncs.com\",\"destLogstore\":\"dest-logstore\","
                + "\"script\":\"* | select time, count(*) as cnt group by time\",\"sqlType\":\"standard\","
                + "\"resourcePool\":\"default\",\"roleArn\":\"acs:ram::123:role/aliyunlogetlrole\","
                + "\"destRoleArn\":\"acs:ram::123:role/aliyunlogetlrole\",\"fromTimeExpr\":\"@m-1m\","
                + "\"toTimeExpr\":\"@m\",\"maxRunTimeInSeconds\":60,\"maxRetries\":3,"
                + "\"fromTime\":1648105200,\"toTime\":0,\"dataFormat\":\"log2metric\",\"forceComplete\":false,"
                + "\"parameters\":" + paramsGolden + "}";
        assertJsonEquals(configGolden, config.toJsonObject().toString());

        Job job = new Job();
        job.setType(JobType.SCHEDULED_SQL);
        job.setName("scheduled-sql-test");
        job.setDisplayName("scheduled sql");
        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.FIXED_RATE);
        schedule.setInterval("60s");
        schedule.setRunImmediately(true);
        job.setSchedule(schedule);
        job.setState(JobState.ENABLED);
        job.setConfiguration(config);
        assertJsonEquals("{\"name\":\"scheduled-sql-test\",\"displayName\":\"scheduled sql\","
                + "\"type\":\"ScheduledSQL\","
                + "\"schedule\":{\"type\":\"FixedRate\",\"interval\":\"60s\",\"runImmediately\":true},"
                + "\"state\":\"Enabled\",\"configuration\":" + configGolden + "}",
                job.toJsonString());
    }
}
