package com.aliyun.openservices.log.internal.json;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.aliyun.openservices.log.common.Advanced;
import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.AuditJob;
import com.aliyun.openservices.log.common.AuditJobConfiguration;
import com.aliyun.openservices.log.common.CommonConfigInputDetail;
import com.aliyun.openservices.log.common.ConfigInputDetail;
import com.aliyun.openservices.log.common.DataFormat;
import com.aliyun.openservices.log.common.ExportGeneralSink;
import com.aliyun.openservices.log.common.ExportContentDetail;
import com.aliyun.openservices.log.common.GeneralJobConfiguration;
import com.aliyun.openservices.log.common.IngestionGeneralSource;
import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.common.JobConfiguration;
import com.aliyun.openservices.log.common.JsonDeserializable;
import com.aliyun.openservices.log.common.JsonSerializable;
import com.aliyun.openservices.log.common.MetricDownSamplingConfig;
import com.aliyun.openservices.log.common.MetricsConfig;
import com.aliyun.openservices.log.common.DataSink;
import com.aliyun.openservices.log.common.DataSource;
import com.aliyun.openservices.log.common.EmailNotification;
import com.aliyun.openservices.log.common.EncryptConfig;
import com.aliyun.openservices.log.common.Notification;
import com.aliyun.openservices.log.common.OssShipperStorageDetail;
import com.aliyun.openservices.log.common.ScheduledSQLParameters;
import com.aliyun.openservices.log.common.SensitiveKey;
import com.aliyun.openservices.log.common.ShipperConfig;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetJobResponse;
import com.aliyun.openservices.log.response.ResponseList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExplicitJsonModelTest {

    private static final String COMMON_PACKAGE = "com.aliyun.openservices.log.common.";
    private static final String[] LEGACY_TO_JSON_STRING_MODELS = {
            "Chart", "Config", "ConfigInputDetail", "ConfigOutputDetail", "Dashboard",
            "Domain", "EncryptConf", "EncryptUserCmkConf", "GroupAttribute", "Index",
            "IndexKey", "IndexKeys", "IndexLine", "LogItem", "LogStore", "LogtailProfile",
            "Machine", "MachineGroup", "MetricStore", "OssShipperStorageColumn", "Project",
            "Resource", "ResourceRecord", "SavedSearch", "ShardingPolicy", "Topostore",
            "TopostoreNode", "TopostoreRelation"
    };
    private static final String[] LEGACY_FROM_JSON_STRING_MODELS = {
            "Chart", "Config", "ConfigInputDetail", "ConfigOutputDetail", "Dashboard",
            "Domain", "EncryptConf", "EncryptUserCmkConf", "GroupAttribute", "Index",
            "IndexKey", "IndexKeys", "IndexLine", "LogStore", "LogtailProfile", "Machine",
            "MachineGroup", "MachineList", "MetricStore", "OssShipperStorageColumn", "Project",
            "Resource", "ResourceRecord", "SavedSearch", "SensitiveKey", "ShardingPolicy",
            "SubStore", "Topostore", "TopostoreNode", "TopostoreRelation"
    };

    @Test
    public void jsonModelsDoNotOverrideObjectToString() throws Exception {
        assertDoesNotDeclareToString(AuditJob.class);
        assertDoesNotDeclareToString(AuditJobConfiguration.class);
        assertDoesNotDeclareToString(ExportGeneralSink.class);
        assertDoesNotDeclareToString(GeneralJobConfiguration.class);
        assertDoesNotDeclareToString(IngestionGeneralSource.class);
    }

    @Test
    public void jsonModelsExposeExplicitJsonStringMethod() throws Exception {
        assertEquals(String.class, AuditJob.class.getMethod("toJsonString").getReturnType());
        assertEquals(String.class, AuditJobConfiguration.class.getMethod("toJsonString").getReturnType());
        assertEquals(String.class, ExportGeneralSink.class.getMethod("toJsonString").getReturnType());
        assertEquals(String.class, GeneralJobConfiguration.class.getMethod("toJsonString").getReturnType());
        assertEquals(String.class, IngestionGeneralSource.class.getMethod("toJsonString").getReturnType());
    }

    @Test
    public void legacyJsonStringEntryPointsRemainPublicWithLowerCamelNames() throws Exception {
        for (String simpleName : LEGACY_TO_JSON_STRING_MODELS) {
            Method method = commonModel(simpleName).getMethod("toJsonString");
            assertTrue(simpleName + ".toJsonString must remain public",
                    Modifier.isPublic(method.getModifiers()));
            assertEquals(String.class, method.getReturnType());
        }
        Method etlOutput = commonModel("EtlJob").getMethod(
                "toJsonString", boolean.class, boolean.class);
        assertTrue(Modifier.isPublic(etlOutput.getModifiers()));
        assertEquals(String.class, etlOutput.getReturnType());

        for (String simpleName : LEGACY_FROM_JSON_STRING_MODELS) {
            Method method = commonModel(simpleName).getMethod("fromJsonString", String.class);
            assertTrue(simpleName + ".fromJsonString must remain public",
                    Modifier.isPublic(method.getModifiers()));
        }
    }

    @Test
    public void jsonInterfacesUseCheckedExceptionContractsAndDefaultStringMethods() throws Exception {
        assertArrayEquals(new Class<?>[] { LogException.class },
                JsonSerializable.class.getMethod("toJsonObject").getExceptionTypes());
        assertArrayEquals(new Class<?>[] { LogException.class },
                JsonSerializable.class.getMethod("toJsonString").getExceptionTypes());
        assertArrayEquals(new Class<?>[] { LogException.class },
                JsonDeserializable.class.getMethod("fromJsonObject", JSONObject.class).getExceptionTypes());
        assertArrayEquals(new Class<?>[] { LogException.class },
                JsonDeserializable.class.getMethod("fromJsonString", String.class).getExceptionTypes());
        assertEquals(JsonSerializable.class, Job.class.getMethod("toJsonString").getDeclaringClass());
    }

    @Test
    public void onlyStringJsonMethodsAreSupportedPublicContracts() throws Exception {
        Method toJsonObject = JsonSerializable.class.getMethod("toJsonObject");
        Method toJsonString = JsonSerializable.class.getMethod("toJsonString");
        Method fromJsonObject = JsonDeserializable.class.getMethod(
                "fromJsonObject", JSONObject.class);
        Method fromJsonString = JsonDeserializable.class.getMethod(
                "fromJsonString", String.class);

        assertNotNull(toJsonObject.getAnnotation(
                com.aliyun.openservices.log.annotation.InternalApi.class));
        assertNotNull(fromJsonObject.getAnnotation(
                com.aliyun.openservices.log.annotation.InternalApi.class));
        assertNull(toJsonString.getAnnotation(
                com.aliyun.openservices.log.annotation.InternalApi.class));
        assertNull(fromJsonString.getAnnotation(
                com.aliyun.openservices.log.annotation.InternalApi.class));
        assertEquals(String.class, toJsonString.getReturnType());
        assertArrayEquals(new Class<?>[] { String.class }, fromJsonString.getParameterTypes());
    }

    @Test
    public void compositionOnlyModelsDoNotExposeJsonStringCapability() {
        assertFalse(JsonSerializable.class.isAssignableFrom(Advanced.class));
        assertFalse(JsonSerializable.class.isAssignableFrom(CommonConfigInputDetail.class));
        assertFalse(JsonSerializable.class.isAssignableFrom(DataFormat.class));
        assertFalse(JsonSerializable.class.isAssignableFrom(EncryptConfig.class));
        assertFalse(JsonSerializable.class.isAssignableFrom(ExportContentDetail.class));
        assertFalse(JsonSerializable.class.isAssignableFrom(Notification.class));
        assertFalse(JsonSerializable.class.isAssignableFrom(EmailNotification.class));
        assertFalse(JsonSerializable.class.isAssignableFrom(OssShipperStorageDetail.class));
        assertFalse(JsonSerializable.class.isAssignableFrom(ScheduledSQLParameters.class));
        assertFalse(JsonSerializable.class.isAssignableFrom(SensitiveKey.class));

        // This concrete type already exposed a JSON string before the interface migration.
        assertTrue(JsonSerializable.class.isAssignableFrom(ConfigInputDetail.class));
    }

    @Test
    public void consumerModelsExposeOnlySupportedJsonStringCapabilities() throws Exception {
        Class<?>[] targetTypes = {
                ShipperConfig.class,
                MetricsConfig.class,
                MetricDownSamplingConfig.class,
                AlertConfiguration.TemplateConfiguration.class,
                AlertConfiguration.ConditionConfiguration.class,
                AlertConfiguration.JoinConfiguration.class,
                AlertConfiguration.Tag.class,
                AlertConfiguration.SeverityConfiguration.class,
                AlertConfiguration.GroupConfiguration.class,
                AlertConfiguration.PolicyConfiguration.class,
                AlertConfiguration.SinkEventStoreConfiguration.class,
                AlertConfiguration.SinkCmsConfiguration.class,
                AlertConfiguration.SinkAlerthubConfiguration.class
        };

        for (Class<?> type : targetTypes) {
            assertTrue(type.getName(), JsonSerializable.class.isAssignableFrom(type));
            assertTrue(type.getName(), JsonDeserializable.class.isAssignableFrom(type));

            Method toJsonString = type.getMethod("toJsonString");
            Method fromJsonString = type.getMethod("fromJsonString", String.class);
            assertTrue(type.getName(), Modifier.isPublic(toJsonString.getModifiers()));
            assertTrue(type.getName(), Modifier.isPublic(fromJsonString.getModifiers()));
            assertEquals(String.class, toJsonString.getReturnType());

            Method toJsonObject = type.getMethod("toJsonObject");
            Method fromJsonObject = type.getMethod("fromJsonObject", JSONObject.class);
            assertNotNull(type.getName(), toJsonObject.getAnnotation(
                    com.aliyun.openservices.log.annotation.InternalApi.class));
            assertNotNull(type.getName(), fromJsonObject.getAnnotation(
                    com.aliyun.openservices.log.annotation.InternalApi.class));
        }
    }

    @Test
    public void jsonModelMethodNamesUseLowerCamelToAndFrom() {
        assertNoLegacyJsonMethod(Job.class);
        assertNoLegacyJsonMethod(JobConfiguration.class);
        assertNoLegacyJsonMethod(DataSource.class);
        assertNoLegacyJsonMethod(DataSink.class);
        assertNoLegacyJsonMethod(ShipperConfig.class);
        assertNoLegacyJsonMethod(GetJobResponse.class);
        assertNoLegacyJsonMethod(ResponseList.class);
    }

    private static void assertDoesNotDeclareToString(Class<?> type) {
        for (Method method : type.getDeclaredMethods()) {
            if (method.getName().equals("toString") && method.getParameterTypes().length == 0) {
                fail(type.getName() + " must use toJsonString() instead of overriding toString()");
            }
        }
    }

    private static Class<?> commonModel(String simpleName) throws ClassNotFoundException {
        return Class.forName(COMMON_PACKAGE + simpleName);
    }

    private static void assertNoLegacyJsonMethod(Class<?> type) {
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            if (name.equals("serialize") || name.equals("deserialize")
                    || (name.contains("Json") && Character.isUpperCase(name.charAt(0)))) {
                fail(type.getName() + " has legacy JSON method name: " + name);
            }
        }
    }

}
