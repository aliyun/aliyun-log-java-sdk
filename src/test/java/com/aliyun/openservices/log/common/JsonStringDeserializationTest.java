package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonStringDeserializationTest {

    @Test
    public void scheduledJobSubclassesExposeInheritedStringEntryPoint() throws Exception {
        Class<?>[] jobTypes = {
                Alert.class,
                Ingestion.class,
                Export.class,
                AuditJob.class,
                Report.class,
                ScheduledSQL.class
        };

        for (Class<?> jobType : jobTypes) {
            Method method = jobType.getMethod("fromJsonString", String.class);
            assertEquals(ScheduledJob.class, method.getDeclaringClass());
        }

        Ingestion ingestion = new Ingestion();
        ingestion.fromJsonString("{"
                + "\"name\":\"ingestion\","
                + "\"type\":\"Ingestion\","
                + "\"state\":\"Enabled\","
                + "\"schedule\":{\"type\":\"Resident\"},"
                + "\"configuration\":{"
                + "\"version\":\"v2.0\","
                + "\"logstore\":\"source-store\","
                + "\"source\":{\"type\":\"RDS\"}"
                + "}}"
        );

        assertEquals("ingestion", ingestion.getName());
        assertEquals("source-store", ingestion.getConfiguration().getLogstore());
    }

    @Test
    public void standaloneJobModelsDeserializeFromString() throws Exception {
        Job legacyJob = new Job();
        legacyJob.fromJsonString("{"
                + "\"name\":\"legacy-job\","
                + "\"type\":\"RebuildIndex\","
                + "\"configuration\":{\"logstore\":\"source-store\"}"
                + "}"
        );
        assertEquals(JobType.REBUILD_INDEX, legacyJob.getType());

        ETLV2 etl = new ETLV2();
        etl.fromJsonString("{"
                + "\"name\":\"etl-job\","
                + "\"type\":\"ETL\","
                + "\"status\":\"RUNNING\","
                + "\"schedule\":{\"type\":\"Resident\"},"
                + "\"configuration\":{"
                + "\"script\":\"e_keep()\","
                + "\"logstore\":\"source-store\","
                + "\"version\":2,"
                + "\"sinks\":[],"
                + "\"accessKeyId\":\"access-key-id\","
                + "\"accessKeySecret\":\"access-key-secret\""
                + "}}"
        );
        assertEquals("etl-job", etl.getName());
        assertEquals("source-store", etl.getConfiguration().getLogstore());

        RebuildIndex rebuildIndex = new RebuildIndex();
        rebuildIndex.fromJsonString("{"
                + "\"name\":\"rebuild-index\","
                + "\"type\":\"RebuildIndex\","
                + "\"configuration\":{\"logstore\":\"source-store\"}"
                + "}"
        );
        assertEquals("source-store", rebuildIndex.getConfiguration().getLogstore());
    }

    @Test
    public void etlJobDeserializesFromString() throws Exception {
        EtlJob job = new EtlJob();
        job.fromJsonString("{"
                + "\"etlJobName\":\"etl-job\","
                + "\"sourceConfig\":{\"logstoreName\":\"source-store\"},"
                + "\"triggerConfig\":{"
                + "\"roleArn\":\"role\","
                + "\"triggerInterval\":60,"
                + "\"maxRetryTime\":3"
                + "},"
                + "\"functionConfig\":{\"functionProvider\":\"Other\"},"
                + "\"functionParameter\":{\"key\":\"value\"},"
                + "\"logConfig\":{"
                + "\"endpoint\":\"endpoint\","
                + "\"projectName\":\"project\","
                + "\"logstoreName\":\"logstore\""
                + "},"
                + "\"enable\":true"
                + "}"
        );

        assertEquals("etl-job", job.getJobName());
        assertEquals("source-store", job.getSourceConfig().getLogstoreName());
        assertEquals("Other", job.getFunctionConfig().getFunctionProvider());
        assertTrue(job.getEnable());
    }

    @Test
    public void externalStoresDeserializeFromString() throws Exception {
        String json = "{"
                + "\"externalStoreName\":\"csv-store\","
                + "\"storeType\":\"csv\","
                + "\"parameter\":{"
                + "\"externalStoreCsv\":\"content\","
                + "\"externalStoreCsvSize\":7,"
                + "\"objects\":[\"table.csv\"],"
                + "\"columns\":[{\"name\":\"name\",\"type\":\"varchar\"}]"
                + "}}";

        ExternalStore externalStore = new ExternalStore();
        externalStore.fromJsonString(json);
        assertEquals("csv-store", externalStore.getExternalStoreName());
        assertEquals("table.csv", externalStore.getParameter().getObjects().get(0));

        CsvExternalStore csvExternalStore = new CsvExternalStore();
        csvExternalStore.fromJsonString(json);
        assertEquals("table.csv", csvExternalStore.getExternalStoreCsv());
        assertEquals(new CsvColumn("name", "varchar"), csvExternalStore.getColumns().get(0));
    }

    @Test
    public void loggingUsesStaticStringFactory() throws Exception {
        Method method = Logging.class.getMethod("fromJsonString", String.class);
        assertTrue(Modifier.isStatic(method.getModifiers()));

        Logging logging = Logging.fromJsonString("{"
                + "\"loggingProject\":\"logging-project\","
                + "\"loggingDetails\":[{\"type\":\"audit\",\"logstore\":\"audit-store\"}]"
                + "}"
        );

        assertEquals("logging-project", logging.getLoggingProject());
        assertEquals("audit-store", logging.getLoggingDetails().get(0).getLogstore());
    }

    @Test
    public void shipperConfigsUseInterfaceStringEntryPoint() throws Exception {
        ShipperConfig odpsConfig = new OdpsShipperConfig();
        odpsConfig.fromJsonString("{"
                + "\"odpsEndpoint\":\"endpoint\","
                + "\"odpsProject\":\"project\","
                + "\"odpsTable\":\"table\","
                + "\"fields\":[\"field\"],"
                + "\"partitionColumn\":[\"partition\"],"
                + "\"partitionTimeFormat\":\"yyyy_MM_dd\","
                + "\"bufferInterval\":1800"
                + "}"
        );
        assertEquals("project", ((OdpsShipperConfig) odpsConfig).GetOdpsProject());

        ShipperConfig ossConfig = new OssShipperConfig();
        ossConfig.fromJsonString("{"
                + "\"ossBucket\":\"bucket\","
                + "\"ossPrefix\":\"prefix\","
                + "\"roleArn\":\"role\","
                + "\"bufferInterval\":300,"
                + "\"bufferSize\":64,"
                + "\"compressType\":\"snappy\","
                + "\"pathFormat\":\"%Y/%m/%d\","
                + "\"storage\":{\"format\":\"json\",\"detail\":{\"enableTag\":true}}"
                + "}"
        );
        OssShipperConfig oss = (OssShipperConfig) ossConfig;
        assertEquals("bucket", oss.GetOssBucket());
        assertTrue(((OssShipperJsonStorageDetail) oss.GetStorageDetail()).isEnableTag());
    }

    @Test(expected = LogException.class)
    public void malformedJsonIsReportedAsLogException() throws Exception {
        new ExternalStore().fromJsonString("not-json");
    }

    @Test(expected = LogException.class)
    public void illTypedExternalStoreIsReportedAsLogException() throws Exception {
        new ExternalStore().fromJsonString("{"
                + "\"externalStoreName\":\"csv-store\","
                + "\"storeType\":\"csv\","
                + "\"parameter\":{\"externalStoreCsvSize\":\"not-a-number\"}"
                + "}");
    }
}
