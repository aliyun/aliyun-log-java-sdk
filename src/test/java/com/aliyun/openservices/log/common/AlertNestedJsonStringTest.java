package com.aliyun.openservices.log.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AlertNestedJsonStringTest {

    @Test
    public void templateConfigurationSupportsPublicStringRoundTrip() throws Exception {
        AlertConfiguration.TemplateConfiguration original =
                new AlertConfiguration.TemplateConfiguration();
        original.setId("template-中文");
        original.setType("user");
        original.setVersion("2");
        original.setLang("zh-CN");
        Map<String, String> tokens = new LinkedHashMap<String, String>();
        tokens.put("query", "status >= 500 && path == \"/api\\v1\"");
        original.setTokens(tokens);
        Map<String, String> annotations = new LinkedHashMap<String, String>();
        annotations.put("说明", "值<>&=\"quoted\"\\path");
        original.setAnnotations(annotations);

        String json = original.toJsonString();
        assertJsonEquals("{\"id\":\"template-中文\",\"type\":\"user\",\"version\":\"2\","
                        + "\"lang\":\"zh-CN\",\"tokens\":{\"query\":"
                        + "\"status >= 500 && path == \\\"/api\\\\v1\\\"\"},"
                        + "\"annotations\":{\"说明\":\"值<>&=\\\"quoted\\\"\\\\path\"}}",
                json);

        AlertConfiguration.TemplateConfiguration decoded =
                new AlertConfiguration.TemplateConfiguration();
        decoded.fromJsonString(json);
        assertEquals("template-中文", decoded.getId());
        assertEquals("user", decoded.getType());
        assertEquals("2", decoded.getVersion());
        assertEquals("zh-CN", decoded.getLang());
        assertEquals(tokens, decoded.getTokens());
        assertEquals(annotations, decoded.getAnnotations());
        assertJsonEquals(json, decoded.toJsonString());
    }

    @Test
    public void conditionConfigurationSupportsPublicStringRoundTrip() throws Exception {
        AlertConfiguration.ConditionConfiguration original =
                new AlertConfiguration.ConditionConfiguration();
        original.setCondition("latency > 10 && name == \"中文\"");
        original.setCountCondition("__count__ == 0");

        String json = original.toJsonString();
        assertJsonEquals("{\"condition\":\"latency > 10 && name == \\\"中文\\\"\","
                + "\"countCondition\":\"__count__ == 0\"}", json);

        AlertConfiguration.ConditionConfiguration decoded =
                new AlertConfiguration.ConditionConfiguration();
        decoded.fromJsonString(json);
        assertEquals(original.getCondition(), decoded.getCondition());
        assertEquals(original.getCountCondition(), decoded.getCountCondition());

        decoded.fromJsonString("{}");
        assertNull(decoded.getCondition());
        assertNull(decoded.getCountCondition());
        assertJsonEquals("{}", decoded.toJsonString());
    }

    @Test
    public void joinConfigurationSupportsPublicStringRoundTrip() throws Exception {
        AlertConfiguration.JoinConfiguration original =
                new AlertConfiguration.JoinConfiguration();
        original.setType("left_join");
        original.setCondition("$0.id == $1.id");
        original.setUi("{\"position\":\"左侧\\path\"}");

        String json = original.toJsonString();
        assertJsonEquals("{\"type\":\"left_join\",\"condition\":\"$0.id == $1.id\","
                + "\"ui\":\"{\\\"position\\\":\\\"左侧\\\\path\\\"}\"}", json);

        AlertConfiguration.JoinConfiguration decoded =
                new AlertConfiguration.JoinConfiguration();
        decoded.fromJsonString(json);
        assertEquals("left_join", decoded.getType());
        assertEquals("$0.id == $1.id", decoded.getCondition());
        assertEquals("{\"position\":\"左侧\\path\"}", decoded.getUi());
    }

    @Test
    public void tagSupportsPublicStringRoundTrip() throws Exception {
        AlertConfiguration.Tag original = new AlertConfiguration.Tag();
        original.setKey("标签");
        original.setValue("value-\"quoted\"-\\path");

        String json = original.toJsonString();
        assertJsonEquals("{\"key\":\"标签\",\"value\":\"value-\\\"quoted\\\"-\\\\path\"}",
                json);

        AlertConfiguration.Tag decoded = new AlertConfiguration.Tag();
        decoded.fromJsonString(json);
        assertEquals("标签", decoded.getKey());
        assertEquals("value-\"quoted\"-\\path", decoded.getValue());
    }

    @Test
    public void severityConfigurationSupportsPublicStringRoundTrip() throws Exception {
        AlertConfiguration.ConditionConfiguration condition =
                new AlertConfiguration.ConditionConfiguration();
        condition.setCondition("value >= 0");
        condition.setCountCondition("__count__ > 3");
        AlertConfiguration.SeverityConfiguration original =
                new AlertConfiguration.SeverityConfiguration();
        original.setSeverity(AlertConfiguration.Severity.High);
        original.setEvalCondition(condition);

        String json = original.toJsonString();
        assertJsonEquals("{\"severity\":8,\"evalCondition\":{\"condition\":\"value >= 0\","
                + "\"countCondition\":\"__count__ > 3\"}}", json);

        AlertConfiguration.SeverityConfiguration decoded =
                new AlertConfiguration.SeverityConfiguration();
        decoded.fromJsonString(json);
        assertEquals(8, decoded.getSeverity());
        assertEquals("value >= 0", decoded.getEvalCondition().getCondition());
        assertEquals("__count__ > 3", decoded.getEvalCondition().getCountCondition());
    }

    @Test
    public void groupConfigurationSupportsPublicStringRoundTrip() throws Exception {
        AlertConfiguration.GroupConfiguration original =
                new AlertConfiguration.GroupConfiguration();
        original.setType("custom");
        original.setFields(Arrays.asList("host", "区域"));

        String json = original.toJsonString();
        assertJsonEquals("{\"type\":\"custom\",\"fields\":[\"host\",\"区域\"]}", json);

        AlertConfiguration.GroupConfiguration decoded =
                new AlertConfiguration.GroupConfiguration();
        decoded.fromJsonString(json);
        assertEquals("custom", decoded.getType());
        assertEquals(Arrays.asList("host", "区域"), decoded.getFields());
    }

    @Test
    public void policyConfigurationSupportsPublicStringRoundTrip() throws Exception {
        AlertConfiguration.PolicyConfiguration original =
                new AlertConfiguration.PolicyConfiguration();
        original.setActionPolicyId("action-中文");
        original.setAlertPolicyId("alert-1");
        original.setUseDefault(false);
        original.setRepeatInterval("0m");

        String json = original.toJsonString();
        assertJsonEquals("{\"actionPolicyId\":\"action-中文\",\"alertPolicyId\":\"alert-1\","
                + "\"useDefault\":false,\"repeatInterval\":\"0m\"}", json);

        AlertConfiguration.PolicyConfiguration decoded =
                new AlertConfiguration.PolicyConfiguration();
        decoded.fromJsonString(json);
        assertEquals("action-中文", decoded.getActionPolicyId());
        assertEquals("alert-1", decoded.getAlertPolicyId());
        assertFalse(decoded.isUseDefault());
        assertEquals("0m", decoded.getRepeatInterval());
    }

    @Test
    public void eventStoreSinkSupportsPublicStringRoundTrip() throws Exception {
        AlertConfiguration.SinkEventStoreConfiguration original =
                new AlertConfiguration.SinkEventStoreConfiguration();
        original.setEnabled(true);
        original.setEndpoint("https://example.com/path?x=<>&y=中文");
        original.setProject("project-中文");
        original.setEventStore("event-\"quoted\"");
        original.setRoleArn("acs:ram::1:role/alert\\writer");

        String json = original.toJsonString();
        assertJsonEquals("{\"enabled\":true,"
                        + "\"endpoint\":\"https://example.com/path?x=<>&y=中文\","
                        + "\"project\":\"project-中文\",\"eventStore\":\"event-\\\"quoted\\\"\","
                        + "\"roleArn\":\"acs:ram::1:role/alert\\\\writer\"}",
                json);

        AlertConfiguration.SinkEventStoreConfiguration decoded =
                new AlertConfiguration.SinkEventStoreConfiguration();
        decoded.fromJsonString(json);
        assertTrue(decoded.isEnabled());
        assertEquals("https://example.com/path?x=<>&y=中文", decoded.getEndpoint());
        assertEquals("project-中文", decoded.getProject());
        assertEquals("event-\"quoted\"", decoded.getEventStore());
        assertEquals("acs:ram::1:role/alert\\writer", decoded.getRoleArn());
    }

    @Test
    public void cmsSinkSupportsPublicStringRoundTripAndExplicitFalse() throws Exception {
        AlertConfiguration.SinkCmsConfiguration original =
                new AlertConfiguration.SinkCmsConfiguration();
        original.setEnabled(false);

        String json = original.toJsonString();
        assertJsonEquals("{\"enabled\":false}", json);

        AlertConfiguration.SinkCmsConfiguration decoded =
                new AlertConfiguration.SinkCmsConfiguration();
        decoded.setEnabled(true);
        decoded.fromJsonString(json);
        assertFalse(decoded.isEnabled());
    }

    @Test
    public void alerthubSinkSupportsPublicStringRoundTrip() throws Exception {
        AlertConfiguration.SinkAlerthubConfiguration original =
                new AlertConfiguration.SinkAlerthubConfiguration();
        original.setEnabled(true);

        String json = original.toJsonString();
        assertJsonEquals("{\"enabled\":true}", json);

        AlertConfiguration.SinkAlerthubConfiguration decoded =
                new AlertConfiguration.SinkAlerthubConfiguration();
        decoded.fromJsonString(json);
        assertTrue(decoded.isEnabled());
    }
}
