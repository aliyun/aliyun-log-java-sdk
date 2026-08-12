package com.aliyun.openservices.log.internal.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.common.AlertConfiguration;
import com.aliyun.openservices.log.common.GeneralJobConfiguration;
import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.common.JobInstance;
import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.common.JobType;

/** Tests for the strict internal JSON tree and the model codec boundary. */
public class ShimBehaviorTest {

    private static final String SAMPLE = "{"
            + "\"str\":\"value\","
            + "\"int\":30,"
            + "\"negative\":-5,"
            + "\"long\":2147483648,"
            + "\"double\":1.5,"
            + "\"boolTrue\":true,"
            + "\"boolFalse\":false,"
            + "\"nullValue\":null,"
            + "\"obj\":{\"a\":1,\"b\":\"x\"},"
            + "\"arr\":[1,\"two\",true,{\"k\":\"v\"}],"
            + "\"special.key\":\"dot\","
            + "\"$ref\":\"not-a-ref\","
            + "\"chars\":\"a<b>c&d=e中文\""
            + "}";

    private JSONObject parseSample() {
        return JSONObject.parseObject(SAMPLE);
    }

    @Test
    public void testExactCategoryGetters() {
        JSONObject value = parseSample();
        assertEquals("value", value.getString("str"));
        assertEquals(30, value.getIntValue("int"));
        assertEquals(2147483648L, value.getLongValue("long"));
        assertEquals(1.5D, value.getDoubleValue("double"), 0D);
        assertEquals(Boolean.TRUE, value.getBoolean("boolTrue"));

        assertTypeMismatch(new ThrowingAction() {
            @Override
            public void run() {
                parseSample().getString("int");
            }
        });
        assertTypeMismatch(new ThrowingAction() {
            @Override
            public void run() {
                parseSample().getIntValue("str");
            }
        });
        assertTypeMismatch(new ThrowingAction() {
            @Override
            public void run() {
                parseSample().getBoolean("int");
            }
        });
        assertTypeMismatch(new ThrowingAction() {
            @Override
            public void run() {
                parseSample().getJSONObject("str");
            }
        });
        assertTypeMismatch(new ThrowingAction() {
            @Override
            public void run() {
                parseSample().getJSONArray("obj");
            }
        });
    }

    @Test
    public void testMissingAndJsonNullAccessors() {
        JSONObject value = parseSample();
        assertNull(value.getString("missing"));
        assertNull(value.getString("nullValue"));
        assertNull(value.getInteger("missing"));
        assertNull(value.getInteger("nullValue"));
        assertNull(value.getLong("missing"));
        assertNull(value.getBoolean("nullValue"));
        assertEquals(0, value.getIntValue("missing"));
        assertEquals(0L, value.getLongValue("nullValue"));
        assertFalse(value.getBooleanValue("missing"));
        assertEquals(0D, value.getDoubleValue("nullValue"), 0D);
        assertTrue(value.containsKey("nullValue"));
        assertFalse(value.containsKey("missing"));
    }

    @Test
    public void testNestedAccess() {
        JSONObject value = parseSample();
        assertEquals("x", value.getJSONObject("obj").getString("b"));
        assertEquals(1, value.getJSONObject("obj").getIntValue("a"));

        JSONArray array = value.getJSONArray("arr");
        assertEquals(4, array.size());
        assertEquals(1, array.getIntValue(0));
        assertEquals("two", array.getString(1));
        assertEquals("v", array.getJSONObject(3).getString("k"));
        assertEquals("[1,\"two\",true,{\"k\":\"v\"}]", array.toString());
    }

    @Test
    public void testGsonNumericNarrowingBehavior() {
        JSONObject value = JSONObject.parseObject("{\"fraction\":1.9,\"overflow\":2147483648,\"exp\":1e3}");
        assertEquals(1, value.getIntValue("fraction"));
        assertEquals(Integer.MIN_VALUE, value.getIntValue("overflow"));
        assertEquals(1000, value.getIntValue("exp"));
    }

    @Test
    public void testStrictSyntax() {
        assertInvalid("{invalid}");
        assertInvalid("{'a':1}");
        assertInvalid("{\"a\":1,}");
        assertInvalid("{/*comment*/\"a\":1}");
        assertInvalid("{\"a\":1} trailing");
        assertInvalid("{\"a\":01}");
        assertInvalid("{\"a\":+1}");
        assertInvalid("{\"a\":NaN}");
        assertInvalid("{\"a\":Infinity}");
        assertInvalid("");
        assertEquals(1, JSONObject.parseObject("{\"a\":1}").getIntValue("a"));
    }

    @Test
    public void testObjectAndArrayTopLevelRequired() {
        assertInvalidObject("[]");
        assertInvalidObject("1");
        try {
            JSONArray.parseArray("{}");
            fail("should have rejected a non-array root");
        } catch (JSONException expected) {
            assertTrue(expected.getMessage().contains("Expected JSON array"));
        }
        assertNull(JSONObject.parseObject("null"));
        assertNull(JSONArray.parseArray("null"));
    }

    @Test
    public void testNullWriteSemantics() {
        JSONObject value = new JSONObject();
        value.put("omitted", (String) null);
        assertTrue(value.containsKey("omitted"));
        assertEquals("{}", value.toString());

        JSONObject parsed = JSONObject.parseObject(
                "{\"top\":null,\"nested\":{\"omitted\":null,\"kept\":1}}");
        assertEquals("{\"nested\":{\"kept\":1}}", parsed.toString());

        JSONArray array = new JSONArray();
        array.add((String) null);
        assertEquals("[null]", array.toString());
    }

    @Test
    public void testNonFiniteNumbersRejected() {
        JSONObject value = new JSONObject();
        assertInvalidNumber(new ThrowingAction() {
            @Override
            public void run() {
                value.put("nan", Double.NaN);
            }
        });

        JSONArray array = new JSONArray();
        assertInvalidNumber(new ThrowingAction() {
            @Override
            public void run() {
                array.add(Double.POSITIVE_INFINITY);
            }
        });
        assertInvalidNumber(new ThrowingAction() {
            @Override
            public void run() {
                JsonCodec.toJson(new NonFiniteModel());
            }
        });
    }

    @Test
    public void testNumberLiteralsAndJavaValues() {
        for (String json : new String[]{
                "{\"v\":30}",
                "{\"v\":-1}",
                "{\"v\":2147483648}",
                "{\"v\":9223372036854775807}",
                "{\"v\":1.5}",
                "{\"v\":0.001}"}) {
            assertEquals(json, JSONObject.parseObject(json).toString());
        }
        Map<String, Object> values = JsonCodec.toMap(JSONObject.parseObject("{\"v\":30}"));
        assertTrue(values instanceof HashMap);
        assertTrue(values.get("v") instanceof Integer);
        assertTrue(JsonCodec.toMap(JSONObject.parseObject("{\"v\":2147483648}")).get("v") instanceof Long);
    }

    @Test
    public void testJsonAssertIgnoresObjectMemberOrder() {
        JsonAsserts.assertJsonEquals(
                "{\"first\":1,\"nested\":{\"left\":2,\"right\":3}}",
                "{\"nested\":{\"right\":3,\"left\":2},\"first\":1}");
    }

    @Test
    public void testCodecCompatibilityRules() {
        Date date = new Date(1700000000000L);
        JobSchedule schedule = new JobSchedule();
        schedule.setStartTime(date);
        assertEquals(1700000000L,
                schedule.toJsonObject().getLongValue("startTime"));
        JobSchedule decoded = new JobSchedule();
        decoded.fromJsonObject(JSONObject.parseObject(
                "{\"type\":\"FixedRate\",\"interval\":\"60s\",\"startTime\":1700000000}"));
        assertEquals(date, decoded.getStartTime());

        try {
            decoded.fromJsonObject(JSONObject.parseObject(
                    "{\"type\":\"FixedRate\",\"interval\":\"60s\",\"startTime\":\"1700000000\"}"));
            fail("should have rejected a string timestamp");
        } catch (JSONException expected) {
            assertTrue(expected.getMessage().contains("must be JSON number"));
        }

        assertEquals("\"Alert\"", JsonCodec.toJson(JobType.ALERT));
        assertEquals("\"ScheduledSQL\"", JsonCodec.toJson(JobType.SCHEDULED_SQL));
    }

    @Test
    public void testAllDateFieldsUseExplicitUnixSecondsSerialization() throws Exception {
        Date date = new Date(1700000000000L);

        JobSchedule schedule = new JobSchedule();
        schedule.setCreateTime(date);
        schedule.setLastModifiedTime(date);
        schedule.setStartTime(date);
        schedule.setCompleteTime(date);
        JSONObject scheduleJson = schedule.toJsonObject();
        assertUnixSeconds(scheduleJson, "createTime");
        assertUnixSeconds(scheduleJson, "lastModifiedTime");
        assertUnixSeconds(scheduleJson, "startTime");
        assertUnixSeconds(scheduleJson, "completeTime");

        Job job = new Job();
        job.setCreateTime(date);
        job.setLastModifiedTime(date);
        JSONObject jobJson = JSONObject.parseObject(job.toJsonString());
        assertUnixSeconds(jobJson, "createTime");
        assertUnixSeconds(jobJson, "lastModifiedTime");

        Alert alert = new Alert();
        alert.setCreateTime(date);
        alert.setLastModifiedTime(date);
        JSONObject alertJson = JSONObject.parseObject(alert.toJsonString());
        assertUnixSeconds(alertJson, "createTime");
        assertUnixSeconds(alertJson, "lastModifiedTime");

        AlertConfiguration configuration = new AlertConfiguration();
        configuration.setMuteUntil(date);
        assertUnixSeconds(configuration.toJsonObject(), "muteUntil");
    }

    private static void assertUnixSeconds(JSONObject value, String key) {
        assertEquals(1700000000L, value.getLongValue(key));
    }

    @Test
    public void testDefaultModelRules() {
        NullableModel model = JsonCodec.fromJson(
                "{\"present\":\"value\",\"ignoredByModel\":true}", NullableModel.class);
        assertEquals("value", model.present);
        assertNull(model.omitted);
        assertEquals("{\"present\":\"value\"}", JsonCodec.toJson(model));
    }

    @Test
    public void testCustomRawJsonOmitsExplicitNulls() {
        GeneralJobConfiguration configuration =
                new GeneralJobConfiguration("{\"explicit\":null}");
        assertEquals("{}", configuration.toJsonObject().toString());
    }

    @Test
    public void testJobInstanceTimestampsRemainMilliseconds() {
        JSONObject json = JSONObject.parseObject("{"
                + "\"createTimeInMillis\":1700000000123,"
                + "\"beginTimeInMillis\":1700000001123,"
                + "\"updateTimeInMillis\":1700000002123,"
                + "\"scheduleTimeInMillis\":1700000003123}"
        );
        JobInstance instance = new JobInstance();
        instance.fromJsonObject(json);
        assertEquals(1700000000123L, instance.getCreateTimeInMillis());
        assertEquals(1700000001123L, instance.getBeginTimeInMillis());
        assertEquals(1700000002123L, instance.getUpdateTimeInMillis());
        assertEquals(1700000003123L, instance.getScheduleTimeInMillis());
    }

    @Test
    public void testNoHtmlEscapingAndUnicodeRoundTrip() {
        JSONObject value = new JSONObject();
        value.put("query", "a<b>c&d=e中文");
        value.put("chars", "中文\"quote\"\\backslash\nnewline");
        String json = value.toString();
        assertTrue(json.contains("a<b>c&d=e中文"));
        assertFalse(json.contains("\\u003c"));

        JSONObject reparsed = JSONObject.parseObject(json);
        assertEquals("中文\"quote\"\\backslash\nnewline", reparsed.getString("chars"));
    }

    private static void assertInvalid(String json) {
        try {
            JsonTree.parse(json);
            fail("should have rejected invalid JSON: " + json);
        } catch (JSONException expected) {
            // expected
        }
    }

    private static void assertInvalidObject(String json) {
        try {
            JSONObject.parseObject(json);
            fail("should have rejected a non-object root: " + json);
        } catch (JSONException expected) {
            assertTrue(expected.getMessage().contains("Expected JSON object"));
        }
    }

    private static void assertTypeMismatch(ThrowingAction action) {
        try {
            action.run();
            fail("should have rejected a JSON category mismatch");
        } catch (JSONException expected) {
            assertTrue(expected.getMessage().contains("must be JSON"));
        }
    }

    private static void assertInvalidNumber(ThrowingAction action) {
        try {
            action.run();
            fail("should have rejected a non-finite number");
        } catch (JSONException expected) {
            String message = expected.getMessage();
            assertTrue(message.contains("finite")
                    || message.contains("NaN")
                    || message.contains("Infinity"));
        }
    }

    private interface ThrowingAction {
        void run();
    }

    private static final class NullableModel {
        private String present;
        private String omitted;
    }

    private static final class NonFiniteModel {
        private double value = Double.NaN;
    }
}
