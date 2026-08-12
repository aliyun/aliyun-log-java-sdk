package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JobPolymorphicDeserializationTest {

    @Test
    public void testDeserializeEtlConfigurationAndLogSinks() {
        Job job = deserializeJob("ETL", "{"
                + "\"script\":\"e_set('target', v('source'))\","
                + "\"logstore\":\"source-store\","
                + "\"version\":2,"
                + "\"parameters\":{\"mode\":\"test\"},"
                + "\"sinks\":[{"
                + "\"type\":\"AliyunLOG\","
                + "\"name\":\"primary\","
                + "\"endpoint\":\"cn-hangzhou.log.aliyuncs.com\","
                + "\"project\":\"target-project\","
                + "\"logstore\":\"target-store\","
                + "\"roleArn\":\"acs:ram::123456789:role/etl\","
                + "\"datasets\":[\"main\",\"archive\"]"
                + "},{"
                + "\"type\":\"AliyunLOG\","
                + "\"name\":\"backup\","
                + "\"project\":\"backup-project\","
                + "\"logstore\":\"backup-store\""
                + "}],"
                + "\"fromTime\":100,"
                + "\"toTime\":200,"
                + "\"lang\":\"SPL\""
                + "}");

        ETLConfiguration configuration = assertType(ETLConfiguration.class, job.getConfiguration());
        assertEquals("source-store", configuration.getLogstore());
        assertEquals("test", configuration.getParameters().get("mode"));
        assertEquals(Integer.valueOf(100), configuration.getFromTime());
        assertEquals(Integer.valueOf(200), configuration.getToTime());
        assertEquals(2, configuration.getSinks().size());

        AliyunLOGSink primary = assertType(AliyunLOGSink.class, configuration.getSinks().get(0));
        assertEquals(DataSinkType.ALIYUN_LOG, primary.getType());
        assertEquals("target-project", primary.getProject());
        assertEquals("target-store", primary.getLogstore());
        assertEquals("acs:ram::123456789:role/etl", primary.getRoleArn());
        assertEquals(Arrays.asList("main", "archive"), primary.getDatasets());

        AliyunLOGSink backup = assertType(AliyunLOGSink.class, configuration.getSinks().get(1));
        assertEquals("backup-project", backup.getProject());
        assertEquals("", backup.getEndpoint());
    }

    @Test
    public void testDeserializeRemainingJobConfigurationTypes() {
        ReportConfiguration report = assertType(ReportConfiguration.class,
                deserializeJob("Report", "{"
                        + "\"dashboard\":\"operation-dashboard\","
                        + "\"notificationList\":[],"
                        + "\"language\":\"zh-CN\","
                        + "\"enableWatermark\":true"
                        + "}").getConfiguration());
        assertEquals("operation-dashboard", report.getDashboard());
        assertEquals("zh-CN", report.getLanguage());
        assertTrue(report.getEnableWatermark());

        AuditJobConfiguration audit = assertType(AuditJobConfiguration.class,
                deserializeJob("AuditJob", "{\"service\":\"actiontrail\"}").getConfiguration());
        assertEquals("actiontrail", JSONObject.parseObject(audit.getDetail()).getString("service"));

        RebuildIndexConfiguration rebuildIndex = assertType(RebuildIndexConfiguration.class,
                deserializeJob("RebuildIndex", "{"
                        + "\"logstore\":\"source-store\","
                        + "\"fromTime\":100,"
                        + "\"toTime\":200"
                        + "}").getConfiguration());
        assertEquals("source-store", rebuildIndex.getLogstore());
        assertEquals(Integer.valueOf(100), rebuildIndex.getFromTime());

        ScheduledSQLConfiguration scheduledSQL = assertType(ScheduledSQLConfiguration.class,
                deserializeJob("ScheduledSQL", "{"
                        + "\"sourceLogstore\":\"source-store\","
                        + "\"script\":\"* | select count(*)\","
                        + "\"destProject\":\"target-project\","
                        + "\"destLogstore\":\"target-store\","
                        + "\"dataFormat\":\"log2log\","
                        + "\"forceComplete\":true"
                        + "}").getConfiguration());
        assertEquals("* | select count(*)", scheduledSQL.getScript());
        assertEquals("target-store", scheduledSQL.getDestLogstore());
        assertEquals(Boolean.TRUE, scheduledSQL.getForceComplete());

        JobDownSamplingConfiguration downSampling = assertType(JobDownSamplingConfiguration.class,
                deserializeJob("DownSampling", "{"
                        + "\"sourceLogstore\":\"metric-store\","
                        + "\"destProject\":\"target-project\","
                        + "\"destLogstore\":\"downsampled-store\","
                        + "\"fromTime\":100,"
                        + "\"toTime\":200,"
                        + "\"dataFormat\":\"metric2metric\""
                        + "}").getConfiguration());
        assertEquals("metric-store", downSampling.getSourceLogstore());
        assertEquals("downsampled-store", downSampling.getDestLogstore());
        assertEquals(Long.valueOf(200), downSampling.getToTime());
    }

    @Test
    public void testDeserializeAllSupportedIngestionSourceTypes() {
        JDBCSource jdbc = assertType(JDBCSource.class, deserializeIngestionSource("{"
                + "\"type\":\"JDBC\","
                + "\"databaseType\":\"mysql\","
                + "\"jdbcDatabase\":\"orders\","
                + "\"tableName\":\"order_table\","
                + "\"pageSize\":100"
                + "}"));
        assertEquals("orders", jdbc.getJdbcDatabase());
        assertEquals("order_table", jdbc.getTableName());
        assertEquals(100, jdbc.getPageSize());

        AliyunBSSSource bss = assertType(AliyunBSSSource.class, deserializeIngestionSource("{"
                + "\"type\":\"AliyunBSS\","
                + "\"roleARN\":\"acs:ram::123456789:role/bss\","
                + "\"historyMonth\":6"
                + "}"));
        assertEquals("acs:ram::123456789:role/bss", bss.getRoleARN());
        assertEquals(Integer.valueOf(6), bss.getHistoryMonth());

        AliyunOSSSource oss = assertType(AliyunOSSSource.class, deserializeIngestionSource("{"
                + "\"type\":\"AliyunOSS\","
                + "\"bucket\":\"input-bucket\","
                + "\"endpoint\":\"oss-cn-hangzhou.aliyuncs.com\","
                + "\"format\":{\"type\":\"JSON\",\"skipInvalidRows\":true}"
                + "}"));
        assertEquals("input-bucket", oss.getBucket());
        JSONFormat format = assertType(JSONFormat.class, oss.getFormat());
        assertTrue(format.getSkipInvalidRows());

        AliyunMaxComputeSource maxCompute = assertType(AliyunMaxComputeSource.class,
                deserializeIngestionSource("{"
                        + "\"type\":\"AliyunMaxCompute\","
                        + "\"project\":\"warehouse\","
                        + "\"table\":\"events\","
                        + "\"partitionSpec\":\"ds=20260805\""
                        + "}"));
        assertEquals("warehouse", maxCompute.getProject());
        assertEquals("events", maxCompute.getTable());

        KafKaSource kafka = assertType(KafKaSource.class, deserializeIngestionSource("{"
                + "\"type\":\"Kafka\","
                + "\"topics\":\"audit-log\","
                + "\"bootstrapServers\":\"broker:9092\","
                + "\"valueType\":\"JSON\","
                + "\"fromPosition\":\"EARLIEST\""
                + "}"));
        assertEquals("audit-log", kafka.getTopics());
        assertEquals(KafKaSource.ValueType.JSON, kafka.getValueType());
        assertEquals(KafKaSource.KafkaPosition.EARLIEST, kafka.getFromPosition());

        AliyunCloudMonitorSource cloudMonitor = assertType(AliyunCloudMonitorSource.class,
                deserializeIngestionSource("{"
                        + "\"type\":\"AliyunCloudMonitor\","
                        + "\"accessKeyID\":\"test-id\","
                        + "\"startTime\":1722816000,"
                        + "\"namespaces\":[\"acs_ecs_dashboard\",\"acs_rds_dashboard\"],"
                        + "\"outputType\":\"json\","
                        + "\"delayTime\":60"
                        + "}"));
        assertEquals("test-id", cloudMonitor.getAccessKeyID());
        assertEquals(Arrays.asList("acs_ecs_dashboard", "acs_rds_dashboard"), cloudMonitor.getNamespaces());
        assertEquals(Integer.valueOf(60), cloudMonitor.getDelayTime());
    }

    @Test
    public void testDeserializeAllSupportedLegacyExportSinkTypes() {
        AliyunADBSink adb = assertType(AliyunADBSink.class, deserializeExportSink("{"
                + "\"type\":\"AliyunADB\","
                + "\"url\":\"jdbc:mysql://adb.example.com:3306\","
                + "\"user\":\"etl_user\","
                + "\"password\":\"secret\","
                + "\"dbType\":\"adb30\","
                + "\"vpcId\":\"vpc-test\","
                + "\"instanceId\":\"adb-test\","
                + "\"instancePort\":\"3306\","
                + "\"database\":\"analytics\","
                + "\"table\":\"events\","
                + "\"batchSize\":128,"
                + "\"strictMode\":true,"
                + "\"columnMapping\":{\"source_field\":\"target_field\"}"
                + "}"));
        assertEquals("analytics", adb.getDatabase());
        assertEquals("events", adb.getTable());
        assertEquals("target_field", adb.getColumnMapping().get("source_field"));

        AliyunTSDBSink tsdb = assertType(AliyunTSDBSink.class, deserializeExportSink("{"
                + "\"type\":\"AliyunTSDB\","
                + "\"endpoint\":\"tsdb.example.com\","
                + "\"vpcId\":\"vpc-test\","
                + "\"instanceId\":\"tsdb-test\","
                + "\"dbType\":\"tsdb\","
                + "\"dbVersion\":\"2.0\","
                + "\"metric\":\"cpu_usage\","
                + "\"fieldMapping\":[{\"name\":\"value\",\"type\":\"double\",\"value\":\"cpu\"}],"
                + "\"tagMapping\":[{\"key\":\"host\",\"value\":\"hostname\"}],"
                + "\"timestamp\":{\"key\":\"time\",\"value\":\"__time__\"},"
                + "\"strictMode\":true"
                + "}"));
        assertEquals("cpu_usage", tsdb.getMetric());
        assertEquals("value", tsdb.getFieldMapping().get(0).getName());
        assertEquals("hostname", tsdb.getTagMapping().get(0).getValue());
        assertEquals("__time__", tsdb.getTimestamp().getValue());

        AliyunOSSSink oss = assertType(AliyunOSSSink.class, deserializeExportSink(ossSinkJson(
                "csv",
                "{\"delimiter\":\";\",\"quote\":\"\\\"\",\"lineFeed\":\"\\n\","
                        + "\"null\":\"NULL\",\"header\":true,\"columns\":[\"time\",\"message\"]}"
        )));
        assertEquals("output-bucket", oss.getBucket());
        ExportContentCsvDetail csv = assertType(ExportContentCsvDetail.class, oss.getContentDetail());
        assertEquals(";", csv.getDelimiter());
        assertEquals(Arrays.asList("time", "message"), csv.getStorageColumns());

        AliyunODPSSink odps = assertType(AliyunODPSSink.class, deserializeExportSink("{"
                + "\"type\":\"AliyunODPS\","
                + "\"odpsEndpoint\":\"http://service.odps.aliyun.com/api\","
                + "\"odpsProject\":\"warehouse\","
                + "\"odpsTable\":\"events\","
                + "\"fields\":[\"time\",\"message\"],"
                + "\"partitionColumn\":[\"ds\"],"
                + "\"partitionTimeFormat\":\"yyyyMMdd\""
                + "}"));
        assertEquals("warehouse", odps.getOdpsProject());
        assertEquals(Arrays.asList("time", "message"), odps.getFields());
    }

    @Test
    public void testSerializePolymorphicMembersExplicitly() {
        SmsNotification sms = new SmsNotification();
        sms.setContent("alarm");
        sms.setMobileList(Arrays.asList("86-13800000000"));
        AlertConfiguration alert = new AlertConfiguration();
        alert.setDashboard("dashboard");
        alert.setNotificationList(Arrays.<Notification>asList(sms));

        JSONObject notification = alert.toJsonObject()
                .getJSONArray("notificationList").getJSONObject(0);
        assertEquals("SMS", notification.getString("type"));
        assertEquals("alarm", notification.getString("content"));
        assertEquals("86-13800000000", notification.getJSONArray("mobileList").getString(0));

        JSONFormat format = new JSONFormat();
        format.setSkipInvalidRows(true);
        AliyunOSSSource source = new AliyunOSSSource();
        source.setBucket("input-bucket");
        source.setEndpoint("oss-cn-hangzhou.aliyuncs.com");
        source.setFormat(format);
        JSONObject formatJson = source.toJsonObject().getJSONObject("format");
        assertEquals("JSON", formatJson.getString("type"));
        assertTrue(formatJson.getBooleanValue("skipInvalidRows"));

        ExportContentJsonDetail detail = new ExportContentJsonDetail(true);
        AliyunOSSSink sink = new AliyunOSSSink();
        sink.setBucket("output-bucket");
        sink.setContentType("json");
        sink.setContentDetail(detail);
        JSONObject sinkJson = sink.toJsonObject();
        assertEquals("AliyunOSS", sinkJson.getString("type"));
        assertTrue(sinkJson.getJSONObject("contentDetail").getBooleanValue("enableTag"));
    }

    @Test
    public void testDeserializeOssContentDetailByContentType() {
        AliyunOSSSink jsonSink = assertType(AliyunOSSSink.class, deserializeExportSink(
                ossSinkJson("json", "{\"enableTag\":true}")));
        ExportContentJsonDetail json = assertType(ExportContentJsonDetail.class, jsonSink.getContentDetail());
        assertTrue(json.isEnableTag());

        AliyunOSSSink parquetSink = assertType(AliyunOSSSink.class, deserializeExportSink(
                ossSinkJson("parquet", "{\"columns\":[{\"name\":\"count\",\"type\":\"int64\"}]}")));
        ExportContentColumnStorageDetail parquet = assertType(
                ExportContentColumnStorageDetail.class, parquetSink.getContentDetail());
        assertEquals("count", parquet.getColumns().get(0).getName());
        assertEquals("int64", parquet.getColumns().get(0).getType());

        AliyunOSSSink orcSink = assertType(AliyunOSSSink.class, deserializeExportSink(
                ossSinkJson("orc", "{\"columns\":[{\"name\":\"message\",\"type\":\"string\"}]}")));
        ExportContentColumnStorageDetail orc = assertType(
                ExportContentColumnStorageDetail.class, orcSink.getContentDetail());
        assertEquals("message", orc.getColumns().get(0).getName());
        assertEquals("string", orc.getColumns().get(0).getType());
    }

    private static DataSource deserializeIngestionSource(String sourceJson) {
        Job job = deserializeJob("Ingestion", "{"
                + "\"logstore\":\"source-store\","
                + "\"numberOfInstances\":2,"
                + "\"source\":" + sourceJson
                + "}");
        IngestionConfiguration configuration = assertType(
                IngestionConfiguration.class, job.getConfiguration());
        return configuration.getSource();
    }

    private static DataSink deserializeExportSink(String sinkJson) {
        Job job = deserializeJob("Export", "{"
                + "\"logstore\":\"source-store\","
                + "\"fromTime\":100,"
                + "\"toTime\":200,"
                + "\"sink\":" + sinkJson
                + "}");
        ExportConfiguration configuration = assertType(ExportConfiguration.class, job.getConfiguration());
        return configuration.getSink();
    }

    private static String ossSinkJson(String contentType, String contentDetail) {
        return "{"
                + "\"type\":\"AliyunOSS\","
                + "\"roleArn\":\"acs:ram::123456789:role/export\","
                + "\"bucket\":\"output-bucket\","
                + "\"prefix\":\"jobs/\","
                + "\"pathFormat\":\"%Y/%m/%d/%H/%M\","
                + "\"bufferSize\":256,"
                + "\"bufferInterval\":300,"
                + "\"timeZone\":\"+0800\","
                + "\"compressionType\":\"none\","
                + "\"contentType\":\"" + contentType + "\","
                + "\"contentDetail\":" + contentDetail
                + "}";
    }

    private static Job deserializeJob(String type, String configurationJson) {
        Job job = new Job();
        job.fromJsonObject(JSONObject.parseObject("{"
                + "\"name\":\"polymorphic-job\","
                + "\"type\":\"" + type + "\","
                + "\"configuration\":" + configurationJson
                + "}"));
        assertEquals(JobType.fromString(type), job.getType());
        return job;
    }

    private static <T> T assertType(Class<T> expectedType, Object value) {
        assertEquals(expectedType, value.getClass());
        return expectedType.cast(value);
    }
}
