package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONObject;
import org.junit.Test;

import java.util.List;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** Contract tests for model dispatch that cannot be represented by ordinary field binding. */
public class PolymorphicJsonContractTest {

    @Test
    public void configurationVersionTakesPriorityOverLegacySourceAndSinkTypes() {
        IngestionConfiguration ingestion = new IngestionConfiguration();
        ingestion.fromJsonObject(JSONObject.parseObject("{"
                + "\"version\":\"v2.0\","
                + "\"logstore\":\"source-store\","
                + "\"numberOfInstances\":2,"
                + "\"source\":{\"type\":\"JDBC\",\"customField\":\"source-value\"}"
                + "}"));
        IngestionGeneralSource source = assertExactType(
                IngestionGeneralSource.class, ingestion.getSource());
        assertEquals("JDBC", source.get("type"));
        assertEquals("source-value", source.get("customField"));
        assertEquals("source-value", ingestion.toJsonObject()
                .getJSONObject("source").getString("customField"));

        ExportConfiguration export = new ExportConfiguration();
        export.fromJsonObject(JSONObject.parseObject("{"
                + "\"version\":\"v2.0\","
                + "\"logstore\":\"source-store\","
                + "\"sink\":{\"type\":\"AliyunOSS\",\"customField\":\"sink-value\"}"
                + "}"));
        ExportGeneralSink sink = assertExactType(ExportGeneralSink.class, export.getSink());
        assertEquals("AliyunOSS", sink.get("type"));
        assertEquals("sink-value", sink.get("customField"));
        assertEquals("sink-value", export.toJsonObject()
                .getJSONObject("sink").getString("customField"));
    }

    @Test
    public void legacyIngestionAndExportRejectUnknownDiscriminators() {
        assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                IngestionConfiguration configuration = new IngestionConfiguration();
                configuration.fromJsonObject(JSONObject.parseObject("{"
                        + "\"source\":{\"type\":\"UnknownSource\"}"
                        + "}"));
            }
        });

        assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                ExportConfiguration configuration = new ExportConfiguration();
                configuration.fromJsonObject(JSONObject.parseObject("{"
                        + "\"sink\":{\"type\":\"UnknownSink\"}"
                        + "}"));
            }
        });
    }

    @Test
    public void ossSourceDispatchesEveryFormatAndPreservesSubtypeFields() {
        assertOssFormat("DelimitedText",
                "\"fieldDelimiter\":\"|\",\"fieldNames\":[\"time\",\"message\"]",
                DelimitedTextFormat.class, "fieldDelimiter", "|");
        assertOssFormat("JSON", "\"skipInvalidRows\":true",
                JSONFormat.class, "skipInvalidRows", Boolean.TRUE);
        assertOssFormat("Multiline", "\"pattern\":\"^ERROR\",\"match\":\"after\"",
                MultilineFormat.class, "pattern", "^ERROR");
        assertOssFormat("Parquet", "\"timeField\":\"event_time\"",
                ParquetFormat.class, "timeField", "event_time");
        assertOssFormat("Line", "\"timePattern\":\"yyyy-MM-dd\"",
                LineFormat.class, "timePattern", "yyyy-MM-dd");
        assertOssFormat("Avro", "\"timeField\":\"timestamp\"",
                StructuredDataFormat.class, "timeField", "timestamp");
    }

    @Test
    public void scheduledSqlDispatchesParameterSubtypesAndFallback() {
        ScheduledSQLConfiguration log2metric = scheduledSql("log2metric", "{"
                + "\"timeKey\":\"time\","
                + "\"metricKeys\":\"cpu\","
                + "\"labelKeys\":\"host\","
                + "\"hashLabels\":\"false\","
                + "\"addLabels\":\"env=prod\""
                + "}");
        Log2MetricParameters logParameters = assertExactType(
                Log2MetricParameters.class, log2metric.getParameters());
        assertEquals("time", logParameters.getTimeKey());
        assertEquals("cpu", log2metric.toJsonObject()
                .getJSONObject("parameters").getString("metricKeys"));

        ScheduledSQLConfiguration metric2metric = scheduledSql("metric2metric", "{"
                + "\"metricName\":\"cpu_total\","
                + "\"hashLabels\":\"true\","
                + "\"addLabels\":\"cluster=prod\""
                + "}");
        Metric2MetricParameters metricParameters = assertExactType(
                Metric2MetricParameters.class, metric2metric.getParameters());
        assertEquals("cpu_total", metricParameters.getMetricName());
        assertEquals("cluster=prod", metric2metric.toJsonObject()
                .getJSONObject("parameters").getString("addLabels"));

        ScheduledSQLConfiguration log2log = scheduledSql("log2log", "{"
                + "\"__task_type__\":\"log2log\","
                + "\"ignored\":\"not-a-base-field\""
                + "}");
        ScheduledSQLBaseParameters baseParameters = assertExactType(
                ScheduledSQLBaseParameters.class, log2log.getParameters());
        assertEquals("log2log", baseParameters.getBaseParams().get("__task_type__"));
        assertFalse(log2log.toJsonObject().getJSONObject("parameters").containsKey("ignored"));
    }

    @Test
    public void configDispatchesInputTypeAndFileLogType() throws Exception {
        assertInputDetail(Consts.CONST_CONFIG_INPUTTYPE_SYSLOG,
                new StreamLogConfigInputDetail("syslog"), StreamLogConfigInputDetail.class);
        assertInputDetail(Consts.CONST_CONFIG_INPUTTYPE_STREAMLOG,
                new StreamLogConfigInputDetail("stream"), StreamLogConfigInputDetail.class);

        PluginLogConfigInputDetail plugin = new PluginLogConfigInputDetail();
        plugin.setPluginDetail("{\"inputs\":[{\"type\":\"service_docker_stdout\"}]}");
        plugin.setAdvanced(new Advanced(true));
        assertInputDetail(Consts.CONST_CONFIG_INPUTTYPE_PLUGIN,
                plugin, PluginLogConfigInputDetail.class);

        assertInputDetail(Consts.CONST_CONFIG_INPUTTYPE_FILE,
                new JsonConfigInputDetail(), JsonConfigInputDetail.class);
        assertInputDetail(Consts.CONST_CONFIG_INPUTTYPE_FILE,
                new DelimiterConfigInputDetail(), DelimiterConfigInputDetail.class);
        assertInputDetail(Consts.CONST_CONFIG_INPUTTYPE_FILE,
                new ApsaraLogConfigInputDetail(), ApsaraLogConfigInputDetail.class);
        assertInputDetail(Consts.CONST_CONFIG_INPUTTYPE_FILE,
                new ConfigInputDetail(), ConfigInputDetail.class);

        Config config = new Config();
        JSONObject configJson = new JSONObject();
        configJson.put("configName", "json-config");
        configJson.put("inputType", Consts.CONST_CONFIG_INPUTTYPE_FILE);
        configJson.put("inputDetail", new JsonConfigInputDetail().toJsonObject());
        config.fromJsonObject(configJson);
        assertExactType(JsonConfigInputDetail.class, config.GetInputDetail());
    }

    @Test
    public void configRejectsUnknownInputAndFileLogTypes() {
        assertThrows(LogException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                CommonConfigInputDetail.fromJsonObject(
                        Consts.CONST_CONFIG_INPUTTYPE_FILE,
                        JSONObject.parseObject("{\"logType\":\"unknown\"}"));
            }
        });
        assertThrows(LogException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                CommonConfigInputDetail.fromJsonObject(
                        "unknown-input", new JSONObject());
            }
        });
    }

    @Test
    public void alertAndReportDispatchNotificationSubtypesAndFallbacks() {
        AlertConfiguration alert = new AlertConfiguration();
        alert.fromJsonObject(JSONObject.parseObject("{"
                + "\"notificationList\":["
                + "{\"type\":\"DingTalk\",\"content\":\"dingtalk\"},"
                + "{\"type\":\"Email\",\"content\":\"email\"},"
                + "{\"type\":\"MessageCenter\",\"content\":\"message\"},"
                + "{\"type\":\"SMS\",\"content\":\"sms\"},"
                + "{\"type\":\"Webhook\",\"content\":\"webhook\"},"
                + "{\"type\":\"Voice\",\"content\":\"voice\"},"
                + "{\"type\":\"FutureNotification\"}"
                + "]"
                + "}"));

        List<Notification> notifications = alert.getNotificationList();
        assertEquals(7, notifications.size());
        assertExactType(DingTalkNotification.class, notifications.get(0));
        assertExactType(EmailNotification.class, notifications.get(1));
        assertExactType(MessageCenterNotification.class, notifications.get(2));
        assertExactType(SmsNotification.class, notifications.get(3));
        assertExactType(WebhookNotification.class, notifications.get(4));
        assertExactType(VoiceNotification.class, notifications.get(5));
        assertNull(notifications.get(6));

        JSONArray serialized = alert.toJsonObject().getJSONArray("notificationList");
        assertEquals("DingTalk", serialized.getJSONObject(0).getString("type"));
        assertEquals("Voice", serialized.getJSONObject(5).getString("type"));
        assertNull(serialized.getJSONObject(6));

        ReportConfiguration report = new ReportConfiguration();
        report.fromJsonObject(JSONObject.parseObject("{"
                + "\"notificationList\":["
                + "{\"type\":\"DingTalk\"},"
                + "{\"type\":\"Email\"},"
                + "{\"type\":\"Webhook\"}"
                + "]"
                + "}"));
        assertExactType(DingTalkNotification.class, report.getNotificationList().get(0));
        assertExactType(EmailNotification.class, report.getNotificationList().get(1));
        assertExactType(WebhookNotification.class, report.getNotificationList().get(2));

        assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                ReportConfiguration unsupported = new ReportConfiguration();
                unsupported.fromJsonObject(JSONObject.parseObject("{"
                        + "\"notificationList\":[{\"type\":\"SMS\"}]"
                        + "}"));
            }
        });
    }

    @Test
    public void shipperDispatchesTargetAndStorageTypesIncludingFallback() throws Exception {
        ShipperConfig odps = ShipperConfig.fromJsonObject("odps", JSONObject.parseObject("{"
                + "\"odpsEndpoint\":\"endpoint\","
                + "\"odpsProject\":\"project\","
                + "\"odpsTable\":\"table\","
                + "\"fields\":[\"field\"],"
                + "\"partitionColumn\":[\"ds\"],"
                + "\"partitionTimeFormat\":\"yyyyMMdd\","
                + "\"bufferInterval\":1800"
                + "}"));
        assertExactType(OdpsShipperConfig.class, odps);
        assertEquals("project", odps.toJsonObject().getString("odpsProject"));

        assertOssShipperStorage("json", "{\"enableTag\":true}",
                OssShipperJsonStorageDetail.class);
        assertOssShipperStorage("csv", "{"
                        + "\"columns\":[\"time\",\"message\"],"
                        + "\"delimiter\":\",\",\"quote\":\"\","
                        + "\"nullIdentifier\":\"NULL\",\"header\":true"
                        + "}",
                OssShipperCsvStorageDetail.class);
        assertOssShipperStorage("parquet", "{"
                        + "\"columns\":[{\"name\":\"message\",\"type\":\"string\"}]"
                        + "}",
                OssShipperParquetStorageDetail.class);
        assertOssShipperStorage("avro", "{\"enableTag\":true}",
                OssShipperJsonStorageDetail.class);

        assertThrows(LogException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                ShipperConfig.fromJsonObject("unknown", new JSONObject());
            }
        });
    }

    @Test
    public void indexKeysDispatchJsonAndOrdinaryKeysRecursively() throws Exception {
        IndexKeys keys = new IndexKeys();
        keys.fromJsonObject(JSONObject.parseObject("{"
                + "\"message\":{"
                + "\"type\":\"text\",\"token\":[\",\"],"
                + "\"caseSensitive\":false,\"doc_value\":true,\"alias\":\"msg\""
                + "},"
                + "\"payload\":{"
                + "\"type\":\"json\",\"token\":[],\"caseSensitive\":false,"
                + "\"doc_value\":true,\"alias\":\"\","
                + "\"index_all\":false,\"max_depth\":4,"
                + "\"json_keys\":{"
                + "\"count\":{\"type\":\"long\",\"doc_value\":true,\"alias\":\"\"}"
                + "}"
                + "},"
                + "\"location\":{\"type\":\"geo\",\"doc_value\":false,\"alias\":\"\"}"
                + "}"));

        assertExactType(IndexKey.class, keys.GetKeys().get("message"));
        IndexJsonKey payload = assertExactType(IndexJsonKey.class, keys.GetKeys().get("payload"));
        assertFalse(payload.isIndexAll());
        assertEquals(4, payload.getMaxDepth());
        assertExactType(IndexKey.class, payload.getJsonKeys().GetKeys().get("count"));
        assertExactType(IndexKey.class, keys.GetKeys().get("location"));

        JSONObject serialized = keys.toJsonObject();
        assertEquals("json", serialized.getJSONObject("payload").getString("type"));
        assertEquals("long", serialized.getJSONObject("payload")
                .getJSONObject("json_keys").getJSONObject("count").getString("type"));
    }

    @Test
    public void etlJobDispatchesFunctionProviderAndSerializesFcFields() throws Exception {
        EtlJob fcJob = new EtlJob();
        fcJob.fromJsonObject(etlJobJson("{"
                + "\"functionProvider\":\"" + Consts.FUNCTION_PROVIDER_FC + "\","
                + "\"endpoint\":\"fc-endpoint\","
                + "\"accountId\":\"123456\","
                + "\"regionName\":\"cn-hangzhou\","
                + "\"serviceName\":\"etl-service\","
                + "\"functionName\":\"transform\","
                + "\"roleArn\":\"acs:ram::123456:role/etl\""
                + "}"));
        EtlFunctionFcConfig fc = assertExactType(
                EtlFunctionFcConfig.class, fcJob.getFunctionConfig());
        assertEquals("transform", fc.getFunctionName());
        JSONObject encodedFc = fcJob.toJsonObject(true, true).getJSONObject("functionConfig");
        assertEquals(Consts.FUNCTION_PROVIDER_FC, encodedFc.getString("functionProvider"));
        assertEquals("fc-endpoint", encodedFc.getString("endpoint"));
        assertEquals("acs:ram::123456:role/etl", encodedFc.getString("roleArn"));

        EtlJob otherJob = new EtlJob();
        otherJob.fromJsonObject(etlJobJson("{\"functionProvider\":\"Builtin\"}"));
        assertExactType(EtlFunctionConfig.class, otherJob.getFunctionConfig());
        JSONObject encodedOther = otherJob.toJsonObject(true, true)
                .getJSONObject("functionConfig");
        assertEquals("Builtin", encodedOther.getString("functionProvider"));
        assertFalse(encodedOther.containsKey("endpoint"));
    }

    @Test
    public void ossExportRejectsUnsupportedContentType() {
        assertThrows(RuntimeException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                AliyunOSSSink sink = new AliyunOSSSink();
                sink.fromJsonObject(JSONObject.parseObject("{"
                        + "\"contentType\":\"avro\","
                        + "\"contentDetail\":{}"
                        + "}"));
            }
        });
    }

    @Test
    public void concreteJobResponsesUseTheirFixedConfigurationTypes() {
        assertFixedJobConfiguration(new Alert(), JobType.ALERT,
                "{\"dashboard\":\"alert-dashboard\"}", AlertConfiguration.class);
        assertFixedJobConfiguration(new Report(), JobType.REPORT,
                "{\"dashboard\":\"report-dashboard\"}", ReportConfiguration.class);
        assertFixedJobConfiguration(new AuditJob(), JobType.AUDIT_JOB,
                "{\"enabled\":true}", AuditJobConfiguration.class);
        assertFixedJobConfiguration(new Ingestion(), JobType.INGESTION,
                "{\"version\":\"v2.0\",\"source\":{\"custom\":\"ingestion\"}}",
                IngestionConfiguration.class);
        assertFixedJobConfiguration(new Export(), JobType.EXPORT,
                "{\"version\":\"v2.0\",\"sink\":{\"custom\":\"export\"}}",
                ExportConfiguration.class);
        assertFixedJobConfiguration(new ScheduledSQL(), JobType.SCHEDULED_SQL,
                "{\"dataFormat\":\"log2log\","
                        + "\"parameters\":{\"__task_type__\":\"log2log\"}}",
                ScheduledSQLConfiguration.class);
        assertFixedJobConfiguration(new RebuildIndex(), JobType.REBUILD_INDEX,
                "{\"logstore\":\"source-store\"}", RebuildIndexConfiguration.class);
        assertFixedJobConfiguration(new ETLV2(), JobType.ETL,
                "{\"script\":\"return data\",\"logstore\":\"source-store\","
                        + "\"version\":2,\"sinks\":[]}",
                ETLConfiguration.class);
    }

    private static void assertOssFormat(
            String type,
            String extraFields,
            Class<? extends DataFormat> expectedType,
            String markerKey,
            Object markerValue) {
        AliyunOSSSource source = new AliyunOSSSource();
        source.fromJsonObject(JSONObject.parseObject("{"
                + "\"type\":\"AliyunOSS\","
                + "\"bucket\":\"bucket\","
                + "\"endpoint\":\"endpoint\","
                + "\"format\":{\"type\":\"" + type + "\"," + extraFields + "}"
                + "}"));
        assertExactType(expectedType, source.getFormat());
        JSONObject serialized = source.toJsonObject().getJSONObject("format");
        assertEquals(type, serialized.getString("type"));
        if (markerValue instanceof Boolean) {
            assertEquals(markerValue, serialized.getBoolean(markerKey));
        } else {
            assertEquals(markerValue, serialized.getString(markerKey));
        }
    }

    private static ScheduledSQLConfiguration scheduledSql(String dataFormat, String parameters) {
        ScheduledSQLConfiguration configuration = new ScheduledSQLConfiguration();
        configuration.fromJsonObject(JSONObject.parseObject("{"
                + "\"dataFormat\":\"" + dataFormat + "\","
                + "\"parameters\":" + parameters
                + "}"));
        return configuration;
    }

    private static void assertInputDetail(
            String inputType,
            CommonConfigInputDetail original,
            Class<? extends CommonConfigInputDetail> expectedType) throws Exception {
        JSONObject encoded = original.toJsonObject();
        String expectedJson = encoded.toString();
        CommonConfigInputDetail decoded = CommonConfigInputDetail.fromJsonObject(inputType, encoded);
        assertExactType(expectedType, decoded);
        assertJsonEquals(expectedJson, decoded.toJsonObject().toString());
    }

    private static void assertOssShipperStorage(
            String format,
            String detail,
            Class<? extends OssShipperStorageDetail> expectedType) throws Exception {
        ShipperConfig config = ShipperConfig.fromJsonObject("oss", JSONObject.parseObject("{"
                + "\"ossBucket\":\"bucket\","
                + "\"ossPrefix\":\"prefix\","
                + "\"roleArn\":\"role\","
                + "\"bufferInterval\":300,"
                + "\"bufferSize\":64,"
                + "\"compressType\":\"snappy\","
                + "\"pathFormat\":\"%Y/%m/%d\","
                + "\"storage\":{\"format\":\"" + format + "\",\"detail\":" + detail + "}"
                + "}"));
        OssShipperConfig oss = assertExactType(OssShipperConfig.class, config);
        assertExactType(expectedType, oss.getStorageDetail());
        assertEquals(format, oss.toJsonObject()
                .getJSONObject("storage").getString("format"));
    }

    private static JSONObject etlJobJson(String functionConfig) {
        return JSONObject.parseObject("{"
                + "\"etlJobName\":\"etl-job\","
                + "\"sourceConfig\":{\"logstoreName\":\"source-store\"},"
                + "\"triggerConfig\":{"
                + "\"roleArn\":\"trigger-role\","
                + "\"triggerInterval\":60,"
                + "\"maxRetryTime\":3"
                + "},"
                + "\"functionConfig\":" + functionConfig + ","
                + "\"functionParameter\":{\"key\":\"value\"},"
                + "\"logConfig\":{"
                + "\"endpoint\":\"log-endpoint\","
                + "\"projectName\":\"project\","
                + "\"logstoreName\":\"logstore\""
                + "},"
                + "\"enable\":true"
                + "}");
    }

    private static void assertFixedJobConfiguration(
            AbstractJob job,
            JobType jobType,
            String configuration,
            Class<? extends JobConfiguration> expectedType) {
        boolean hasSchedule = job instanceof ScheduledJob || job instanceof ETLV2;
        JSONObject encoded = JSONObject.parseObject("{"
                + "\"name\":\"job\","
                + "\"type\":\"" + jobType + "\","
                + (hasSchedule ? "\"schedule\":{\"type\":\"Resident\"}," : "")
                + "\"configuration\":" + configuration
                + "}");

        job.fromJsonObject(encoded);

        assertEquals(jobType, job.getType());
        assertExactType(expectedType, job.getConfiguration());
        assertTrue(job.toJsonObject().containsKey("configuration"));
    }

    private static <T> T assertExactType(Class<T> expectedType, Object value) {
        assertEquals(expectedType, value.getClass());
        return expectedType.cast(value);
    }

    private static <T extends Throwable> T assertThrows(
            Class<T> expectedType, ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return expectedType.cast(throwable);
            }
            throw new AssertionError("Expected " + expectedType.getName()
                    + " but caught " + throwable.getClass().getName(), throwable);
        }
        fail("Expected " + expectedType.getName());
        return null;
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
