package com.aliyun.openservices.log.internal.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

/**
 * Behavior tests for the internal gson-backed shim. The expected values below
 * were cross-checked against fastjson 1.2.83 (the legacy implementation)
 * before the dependency was removed.
 */
public class ShimBehaviorTest {

    private static final String SAMPLE = "{"
            + "\"str\":\"value\","
            + "\"int\":30,"
            + "\"negative\":-5,"
            + "\"long\":2147483648,"
            + "\"big\":9223372036854775808,"
            + "\"double\":1.5,"
            + "\"wholeDouble\":1.0,"
            + "\"boolTrue\":true,"
            + "\"boolFalse\":false,"
            + "\"nullValue\":null,"
            + "\"numStr\":\"42\","
            + "\"boolStr\":\"true\","
            + "\"emptyStr\":\"\","
            + "\"obj\":{\"a\":1,\"b\":\"x\"},"
            + "\"arr\":[1,\"two\",true,{\"k\":\"v\"}],"
            + "\"special.key\":\"dot\","
            + "\"$ref\":\"not-a-ref\","
            + "\"chars\":\"a<b>c&d=e中文\""
            + "}";

    private JSONObject parseShim() {
        return JSONObject.parseObject(SAMPLE);
    }

    @Test
    public void testGetString() {
        JSONObject shim = parseShim();
        assertEquals("value", shim.getString("str"));
        assertEquals("30", shim.getString("int"));
        assertEquals("1.5", shim.getString("double"));
        assertEquals("true", shim.getString("boolTrue"));
        assertEquals("42", shim.getString("numStr"));
        assertEquals("", shim.getString("emptyStr"));
        assertEquals("dot", shim.getString("special.key"));
        assertEquals("not-a-ref", shim.getString("$ref"));
        assertEquals("a<b>c&d=e中文", shim.getString("chars"));
        assertNull(shim.getString("nullValue"));
        assertNull(shim.getString("missing"));
    }

    @Test
    public void testIntAccessors() {
        JSONObject shim = parseShim();
        assertEquals(30, shim.getIntValue("int"));
        assertEquals(-5, shim.getIntValue("negative"));
        assertEquals(42, shim.getIntValue("numStr"));
        assertEquals(0, shim.getIntValue("nullValue"));
        assertEquals(0, shim.getIntValue("missing"));
        assertEquals(1, shim.getIntValue("boolTrue"));
        assertEquals(1, shim.getIntValue("double"));

        assertEquals(Integer.valueOf(30), shim.getInteger("int"));
        assertEquals(Integer.valueOf(-5), shim.getInteger("negative"));
        assertEquals(Integer.valueOf(42), shim.getInteger("numStr"));
        assertNull(shim.getInteger("nullValue"));
        assertNull(shim.getInteger("missing"));
        assertEquals(Integer.valueOf(1), shim.getInteger("boolTrue"));
        assertEquals(Integer.valueOf(1), shim.getInteger("double"));

        assertEquals(2147483648L, shim.getLongValue("long"));
        assertEquals(Long.valueOf(2147483648L), shim.getLong("long"));
        assertNull(shim.getLong("missing"));
    }

    @Test
    public void testBooleanAccessors() {
        JSONObject shim = parseShim();
        assertTrue(shim.getBooleanValue("boolTrue"));
        assertFalse(shim.getBooleanValue("boolFalse"));
        assertTrue(shim.getBooleanValue("boolStr"));
        assertFalse(shim.getBooleanValue("nullValue"));
        assertFalse(shim.getBooleanValue("missing"));
        // fastjson: numbers are true only when intValue() == 1
        assertFalse(shim.getBooleanValue("int"));

        assertEquals(Boolean.TRUE, shim.getBoolean("boolTrue"));
        assertEquals(Boolean.FALSE, shim.getBoolean("boolFalse"));
        assertEquals(Boolean.TRUE, shim.getBoolean("boolStr"));
        assertNull(shim.getBoolean("nullValue"));
        assertNull(shim.getBoolean("missing"));
        assertEquals(Boolean.FALSE, shim.getBoolean("int"));
    }

    @Test
    public void testDoubleAccessors() {
        JSONObject shim = parseShim();
        assertEquals(1.5, shim.getDoubleValue("double"), 1e-9);
        assertEquals(30.0, shim.getDoubleValue("int"), 1e-9);
        assertEquals(0.0, shim.getDoubleValue("missing"), 1e-9);
    }

    @Test
    public void testNestedAccess() {
        JSONObject shim = parseShim();

        assertEquals("x", shim.getJSONObject("obj").getString("b"));
        assertEquals(1, shim.getJSONObject("obj").getIntValue("a"));
        assertNull(shim.getJSONObject("missing"));
        assertNull(shim.getJSONArray("missing"));

        JSONArray shimArr = shim.getJSONArray("arr");
        assertEquals(4, shimArr.size());
        assertEquals(1, shimArr.getIntValue(0));
        assertEquals("two", shimArr.getString(1));
        assertEquals(Boolean.TRUE, shimArr.getBoolean(2));
        assertEquals("v", shimArr.getJSONObject(3).getString("k"));
    }

    @Test
    public void testContainsKeyAndNullSemantics() {
        JSONObject shim = parseShim();
        assertTrue(shim.containsKey("nullValue"));
        assertFalse(shim.containsKey("missing"));
        assertTrue(shim.containsKey("special.key"));
        assertTrue(shim.containsKey("$ref"));
    }

    @Test
    public void testRoundTripSemanticEquality() {
        // null map values are skipped on serialization (fastjson default)
        JSONObject shim = parseShim();
        JSONObject reparsed = JSONObject.parseObject(shim.toString());
        assertFalse(reparsed.containsKey("nullValue"));
        assertEquals(shim.size() - 1, reparsed.size());
        for (String key : reparsed.keySet()) {
            assertEquals("value of " + key, shim.getString(key), reparsed.getString(key));
        }
    }

    @Test
    public void testNoHtmlEscaping() {
        JSONObject object = new JSONObject();
        object.put("query", "a<b>c&d=e");
        String json = object.toString();
        assertTrue("must not escape html chars: " + json, json.contains("a<b>c&d=e"));
        assertFalse(json.contains("\\u003c"));
    }

    @Test
    public void testIntegerStaysInteger() {
        JSONObject object = JSONObject.parseObject("{\"ttl\":30}");
        assertEquals("{\"ttl\":30}", object.toString());
        assertTrue(object.get("ttl") instanceof Integer);
    }

    @Test
    public void testNumberLiteralPreserved() {
        for (String json : new String[]{
                "{\"v\":30}",
                "{\"v\":-1}",
                "{\"v\":2147483648}",
                "{\"v\":9223372036854775807}",
                "{\"v\":1.5}",
                "{\"v\":0.001}"}) {
            assertEquals(json, JSONObject.parseObject(json).toString());
        }
    }

    @Test
    public void testBigDecimal() {
        JSONObject shim = parseShim();
        assertEquals(new BigDecimal("1.5"), shim.getBigDecimal("double"));
    }

    @Test
    public void testNullMapValueSkippedOnSerialize() {
        JSONObject shim = new JSONObject();
        shim.put("a", 1);
        shim.put("b", null);
        assertEquals("{\"a\":1}", shim.toJSONString());
    }

    @Test
    public void testDateSerializedAsUnixTimestamp() {
        Date date = new Date(1700000000000L);
        assertEquals("1700000000", JSON.toJSONString(date));
    }

    @Test
    public void testEnumSerializedByToString() {
        assertEquals("\"Alert\"", JSON.toJSONString(com.aliyun.openservices.log.common.JobType.ALERT));
        assertEquals("\"ScheduledSQL\"",
                JSON.toJSONString(com.aliyun.openservices.log.common.JobType.SCHEDULED_SQL));
    }

    @Test
    public void testParseInvalidJsonThrows() {
        try {
            JSONObject.parseObject("{invalid");
            throw new AssertionError("should have thrown");
        } catch (JSONException expected) {
            // ok
        }
    }

    @Test
    public void testUnicodeAndSpecialChars() {
        JSONObject shim = parseShim();
        assertEquals("a<b>c&d=e中文", shim.getString("chars"));
        JSONObject out = new JSONObject();
        out.put("chars", "中文\"quote\"\\backslash\nnewline");
        JSONObject reparsed = JSONObject.parseObject(out.toString());
        assertEquals("中文\"quote\"\\backslash\nnewline", reparsed.getString("chars"));
    }
}
