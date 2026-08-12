package com.aliyun.openservices.log.internal.json;

import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.DataSinkType;
import com.aliyun.openservices.log.common.DataSourceType;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.common.JobState;
import com.aliyun.openservices.log.common.JobType;
import com.aliyun.openservices.log.common.NotificationType;
import com.aliyun.openservices.log.common.ResourceName;
import com.aliyun.openservices.log.common.TimeSpanType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonEnumAdapterTest {

    @Test
    public void testAnnotatedEnumsUseExplicitMethods() {
        assertRoundTrip(JobType.ALERT, "Alert", JobType.class);
        assertRoundTrip(JobState.ENABLED, "Enabled", JobState.class);
        assertRoundTrip(JobScheduleType.FIXED_RATE, "FixedRate", JobScheduleType.class);
        assertRoundTrip(TimeSpanType.THEDAYBEFOREYESTERDAY, "TheDayBeforeYesterday", TimeSpanType.class);
        assertRoundTrip(DataSourceType.ALIYUN_MAX_COMPUTE, "AliyunMaxCompute", DataSourceType.class);
        assertRoundTrip(DataSinkType.ALIYUN_OSSHDFS, "AliyunOSSHDFS", DataSinkType.class);
        assertRoundTrip(NotificationType.DING_TALK, "DingTalk", NotificationType.class);
        assertRoundTrip(ResourceName.ALERT_POLICY, "sls.alert.alert_policy", ResourceName.class);
        assertRoundTrip(AlertConfiguration.JoinType.CROSS_JOIN, "cross_join",
                AlertConfiguration.JoinType.class);
        assertRoundTrip(AlertConfiguration.GroupType.LABELS_AUTO, "labels_auto",
                AlertConfiguration.GroupType.class);
        assertRoundTrip(AlertConfiguration.StoreType.METRIC, "metric",
                AlertConfiguration.StoreType.class);
    }

    @Test
    public void testClassAnnotationUsesDefaultEnumNamesWhenMethodsAreAbsent() {
        assertEquals("\"FIRST_VALUE\"", JsonCodec.toJson(DefaultEnum.FIRST_VALUE));
        assertEquals(DefaultEnum.FIRST_VALUE, JsonCodec.fromJson("\"FIRST_VALUE\"", DefaultEnum.class));
        assertNull(JsonCodec.fromJson("\"first_value\"", DefaultEnum.class));
    }

    @Test
    public void testValueAndCreatorMethodsAreIndependentlyOptional() {
        assertEquals("\"wire-value\"", JsonCodec.toJson(ValueOnly.VALUE));
        assertNull(JsonCodec.fromJson("\"wire-value\"", ValueOnly.class));
        assertEquals(ValueOnly.VALUE, JsonCodec.fromJson("\"VALUE\"", ValueOnly.class));

        assertEquals("\"VALUE\"", JsonCodec.toJson(CreatorOnly.VALUE));
        assertEquals(CreatorOnly.VALUE, JsonCodec.fromJson("\"wire-value\"", CreatorOnly.class));
    }

    @Test
    public void testEnumInputMustBeAJsonString() {
        try {
            JsonCodec.fromJson("1", JobType.class);
            fail("Expected a strict enum type error");
        } catch (JSONException e) {
            assertTrue(e.getMessage().contains("Expected JSON string"));
        }
    }

    private static <E extends Enum<E>> void assertRoundTrip(E value, String wireValue, Class<E> type) {
        assertEquals("\"" + wireValue + "\"", JsonCodec.toJson(value));
        assertEquals(value, JsonCodec.fromJson("\"" + wireValue + "\"", type));
        assertNull(JsonCodec.fromJson("\"unknown-value\"", type));
    }

    @JsonEnumAdapter
    private enum DefaultEnum {
        FIRST_VALUE
    }

    @JsonEnumAdapter
    private enum ValueOnly {
        VALUE;

        @JsonEnumValue
        public String jsonValue() {
            return "wire-value";
        }
    }

    @JsonEnumAdapter
    private enum CreatorOnly {
        VALUE;

        @JsonEnumCreator
        public static CreatorOnly fromJson(String value) {
            return "wire-value".equals(value) ? VALUE : null;
        }
    }
}
