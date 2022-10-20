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
        assertEquals(body, "{\"configuration\":{\"autoAnnotation\":false,\"condition\":\"ID > 100\",\"dashboard\":\"dashboardtest\",\"noDataFire\":false,\"noDataSeverity\":6,\"notificationList\":[{\"content\":\"messagetest\",\"mobileList\":[\"86-13738162867\"],\"type\":\"SMS\"}],\"notifyThreshold\":1,\"queryList\":[{\"chartTitle\":\"chart1\",\"end\":\"now\",\"logStore\":\"logstore-test\",\"query\":\"*\",\"start\":\"-60s\",\"timeSpanType\":\"Custom\"}],\"sendRecoveryMessage\":false,\"sendResolved\":false,\"threshold\":1},\"name\":\"alertTest\",\"schedule\":{\"interval\":\"60s\",\"runImmediately\":false,\"type\":\"FixedRate\"},\"state\":\"Enabled\",\"type\":\"Alert\"}");
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

    @Test
    public void testDeserializeIngestionWithGeneralSource() {
        String body = "{\n" +
                "  \"configuration\": {\n" +
                "    \"logstore\": \"rds-data\",\n" +
                "    \"source\": {\n" +
                "      \"batchMaxSize\": \"0\",\n" +
                "      \"checkPointFieldName\": \"\",\n" +
                "      \"checkPointValue\": \"{\\\"id\\\":\\\"0\\\"}\",\n" +
                "      \"database\": \"employees\",\n" +
                "      \"databaseType\": \"mysql\",\n" +
                "      \"dialTimeOutMs\": \"3000\",\n" +
                "      \"host\": \"rm-test.mysql.rds.aliyuncs.com\",\n" +
                "      \"meta\": \"{\\\"__source__\\\": \\\"10.23.2.2\\\", \\\"__topic__\\\": \\\"db_salaries\\\", \\\"__tag__:__hostname__\\\": \\\"2323\\\"}\",\n" +
                "      \"pageSize\": \"100\",\n" +
                "      \"password\": \"test#123\",\n" +
                "      \"port\": \"3306\",\n" +
                "      \"queryTimeoutMs\": \"1000\",\n" +
                "      \"readTimeOutMs\": \"5000\",\n" +
                "      \"sql\": \"select * from salaries_copy\",\n" +
                "      \"tableName\": \"mysql_table\",\n" +
                "      \"timeFieldName\": \"hire_date1\",\n" +
                "      \"timeFormat\": \"2006-01-02\",\n" +
                "      \"timeZone\": \"Asia/Shanghai\",\n" +
                "      \"type\": \"RDS\",\n" +
                "      \"user\": \"root_huolang\"\n" +
                "    },\n" +
                "    \"version\": \"v2.0\"\n" +
                "  },\n" +
                "  \"displayName\": \"test ingestion\",\n" +
                "  \"name\": \"test-ingestion-general\",\n" +
                "  \"schedule\": {\n" +
                "    \"interval\": \"2m\",\n" +
                "    \"type\": \"FixedRate\"\n" +
                "  },\n" +
                "  \"state\": \"Enabled\",\n" +
                "  \"type\": \"Ingestion\",\n" +
                "  \"createTime\": 1640588846,\n" +
                "  \"lastModifiedTime\": 1640588846,\n" +
                "  \"version\": \"v2.0\"\n" +
                "}";
        Job job = new Job();
        job.deserialize(JSONObject.parseObject(body));
        assertEquals(JobType.INGESTION, job.getType());
        IngestionConfiguration ingestionConfiguration = (IngestionConfiguration) job.getConfiguration();
        DataSource dataSource = ingestionConfiguration.getSource();
        assertEquals(dataSource.type, DataSourceType.GENERAL);
        IngestionGeneralSource ingestionGeneralSource = (IngestionGeneralSource) dataSource;
        System.out.println(JSONObject.toJSONString(ingestionConfiguration));
        System.out.println(ingestionGeneralSource.toString());
        System.out.println(dataSource.toString());
        System.out.println(ingestionGeneralSource.get("port"));
        System.out.println(ingestionGeneralSource.get("sql"));

        Ingestion ingestion = new Ingestion();
        ingestion.deserialize(JSONObject.parseObject(body));
        System.out.println(JSONObject.toJSONString(ingestion.getConfiguration()));
        DataSource source = ingestion.getConfiguration().getSource();
        System.out.println(source.getType());
        System.out.println(source.toString());
        System.out.println(JSONObject.toJSONString(source));
    }

    @Test
    public void testSerializeIngestionWithGeneralSource() {
        Job job = new Job();
        job.setType(JobType.INGESTION);
        job.setName("test-ingestion-general");
        job.setDisplayName("test ingestion");
        job.setSchedule(new JobSchedule() {{
            setType(JobScheduleType.FIXED_RATE);
            setInterval("2m");
        }});
        job.setState(JobState.ENABLED);
        IngestionConfiguration ingestionConfiguration = new IngestionConfiguration();
        IngestionGeneralSource ingestionGeneralSource = new IngestionGeneralSource();
        ingestionGeneralSource.setType(DataSourceType.GENERAL);
        ingestionGeneralSource.put("batchMaxSize", "0");
        ingestionGeneralSource.put("type", "RDS");
        ingestionGeneralSource.put("timeZone", "Asia/Shanghai");
        System.out.println(JsonUtils.serialize(ingestionGeneralSource));
        System.out.println(JSONObject.toJSONString(ingestionGeneralSource));
        ingestionConfiguration.setSource(ingestionGeneralSource);
        job.setConfiguration(ingestionConfiguration);
        String jobStr = "{\"configuration\":{\"source\":{\"batchMaxSize\":\"0\",\"timeZone\":\"Asia/Shanghai\",\"type\":\"RDS\"}},\"displayName\":\"test ingestion\",\"name\":\"test-ingestion-general\",\"schedule\":{\"interval\":\"2m\",\"runImmediately\":false,\"type\":\"FixedRate\"},\"state\":\"Enabled\",\"type\":\"Ingestion\"}";
        assertEquals(jobStr, JSONObject.toJSONString(job));
        System.out.println(JSONObject.toJSONString(job.getConfiguration()));
    }

    @Test
    public void testDeserializeExportWithGeneralSink() {
        String body = "{\n" +
                "  \"configuration\": {\n" +
                "    \"logstore\": \"rds-data\",\n" +
                "    \"sink\": {\n" +
                "      \"type\": \"test-type\",\n" +
                "      \"desc\": \"主要存储了xxx\",\n" +
                "      \"id\": \"stock_storage_1\",\n" +
                "      \"instances\": [\n" +
                "        {\n" +
                "          \"id\": \"stock_storage_1_mysql\",\n" +
                "          \"name\": \"stock_storage_1_mysql\",\n" +
                "          \"ports\": 3306,\n" +
                "          \"type\": \"mysql\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"stock_storage_1_postgresql\",\n" +
                "          \"name\": \"stock_storage_1_postgresql\",\n" +
                "          \"ports\": 5432,\n" +
                "          \"type\": \"postgresql\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"name\": \"库存管理数据库\",\n" +
                "      \"output\": {\n" +
                "        \"logstore\": \"packetbeat-logstore\",\n" +
                "        \"project\": \"test-huolang-k8s\",\n" +
                "        \"region\": \"cn-hangzhou\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"version\": \"v2.0\"\n" +
                "  },\n" +
                "  \"createTime\": 1640588846,\n" +
                "  \"displayName\": \"test ingestion\",\n" +
                "  \"lastModifiedTime\": 1640588846,\n" +
                "  \"name\": \"test-ingestion-general\",\n" +
                "  \"schedule\": {\n" +
                "    \"interval\": \"2m\",\n" +
                "    \"type\": \"FixedRate\"\n" +
                "  },\n" +
                "  \"state\": \"Enabled\",\n" +
                "  \"type\": \"Export\",\n" +
                "  \"version\": \"v2.0\"\n" +
                "}";
        Job job = new Job();
        job.deserialize(JSONObject.parseObject(body));
        assertEquals(JobType.EXPORT, job.getType());
        ExportConfiguration exportConfiguration = (ExportConfiguration) job.getConfiguration();
        DataSink dataSink = exportConfiguration.getSink();
        System.out.println(JSONObject.toJSONString(exportConfiguration));
        assertEquals(DataSinkType.GENERAL, dataSink.getType());

        ExportGeneralSink exportGeneralSink = (ExportGeneralSink) dataSink;
        System.out.println(JSONObject.toJSONString(exportConfiguration));
        System.out.println(exportGeneralSink.toString());
        System.out.println(dataSink.toString());
        System.out.println(exportGeneralSink.get("instances"));
        assertEquals("库存管理数据库", exportGeneralSink.get("name"));
        assertEquals("test-type", exportGeneralSink.get("type"));

        Export export = new Export();
        export.deserialize(JSONObject.parseObject(body));
        System.out.println(JSONObject.toJSONString(export.getConfiguration()));
        DataSink sink = export.getConfiguration().getSink();
        System.out.println(sink.getType());
        System.out.println(sink.toString());
        System.out.println(JSONObject.toJSONString(sink));
    }

    @Test
    public void testSerializeExportWithGeneralSource() {
        Job job = new Job();
        job.setType(JobType.EXPORT);
        job.setName("test-export-general");
        job.setDisplayName("test export");
        job.setSchedule(new JobSchedule() {{
            setType(JobScheduleType.FIXED_RATE);
            setInterval("2m");
        }});
        job.setState(JobState.ENABLED);
        ExportConfiguration exportConfiguration = new ExportConfiguration();
        ExportGeneralSink exportGeneralSink = new ExportGeneralSink();
        exportGeneralSink.setType(DataSinkType.GENERAL);
        exportGeneralSink.put("batchMaxSize", "0");
        exportGeneralSink.put("type", "RDS");
        exportGeneralSink.put("timeZone", "Asia/Shanghai");
        exportGeneralSink.put("someInt", 33);
        exportConfiguration.setSink(exportGeneralSink);
        job.setConfiguration(exportConfiguration);
        String jobStr = "{\"configuration\":{\"fromTime\":0,\"sink\":{\"batchMaxSize\":\"0\",\"timeZone\":\"Asia/Shanghai\",\"type\":\"RDS\",\"someInt\":33},\"toTime\":0},\"displayName\":\"test export\",\"name\":\"test-export-general\",\"schedule\":{\"interval\":\"2m\",\"runImmediately\":false,\"type\":\"FixedRate\"},\"state\":\"Enabled\",\"type\":\"Export\"}";
        assertEquals(jobStr, JSONObject.toJSONString(job));
        System.out.println(JSONObject.toJSONString(job.getConfiguration()));
    }
}
