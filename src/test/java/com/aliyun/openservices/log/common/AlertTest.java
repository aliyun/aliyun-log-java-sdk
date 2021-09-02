package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class AlertTest {

    @Test
    public void testAlertConfigurationV2Serialize() {
        AlertConfiguration alertConfiguration = new AlertConfiguration();
        alertConfiguration.setVersion("2.0");
        alertConfiguration.setThreshold(1);
        alertConfiguration.setType("default");

        List<Query> queryList = new ArrayList<Query>();
        Query query = new Query();
        query.setStart("-60s");
        query.setEnd("now");
        query.setTimeSpanType(TimeSpanType.CUSTOM);
        query.setChartTitle("chart1");
        query.setLogStore("logstore-test");
        query.setQuery("* | select name, count(uid) as uv group by name");
        query.setRegion("cn-heyuan");
        query.setProject("test-project");
        query.setRoleArn("acs:*:xxxxx");
        queryList.add(query);

        Query query2 = new Query();
        query2.setStart("-86400s");
        query2.setEnd("now");
        query2.setTimeSpanType(TimeSpanType.RELATIVE);
        query2.setQuery("* | select name, min(latency) group by name");
        query2.setStore("test-alert-latency");
        queryList.add(query2);
        alertConfiguration.setQueryList(queryList);

        AlertConfiguration.ConditionConfiguration conditionConfiguration = new AlertConfiguration.ConditionConfiguration();
        conditionConfiguration.setCondition("name == 'k8s'");
        conditionConfiguration.setCountCondition("__count__ > 20");
        alertConfiguration.setConditionConfiguration(conditionConfiguration);

        alertConfiguration.setThreshold(1);
        List<AlertConfiguration.Tag> labels = new ArrayList<AlertConfiguration.Tag>();
        labels.add(new AlertConfiguration.Tag() {{
            setKey("l1");
            setValue("v1");
        }});
        labels.add(new AlertConfiguration.Tag() {{
            setKey("l2");
            setValue("v2");
        }});
        alertConfiguration.setLabels(labels);

        List<AlertConfiguration.Tag> annotations = new ArrayList<AlertConfiguration.Tag>();
        annotations.add(new AlertConfiguration.Tag() {{
            setKey("ak1");
            setValue("av1");
        }});
        alertConfiguration.setAnnotations(annotations);

        List<AlertConfiguration.SeverityConfiguration> severityConfigurations = new ArrayList<AlertConfiguration.SeverityConfiguration>();
        severityConfigurations.add(new AlertConfiguration.SeverityConfiguration() {{
            setSeverity(AlertConfiguration.Severity.High);
            setEvalCondition(new AlertConfiguration.ConditionConfiguration() {{
                setCondition("latency > 90");
                setCountCondition("__count__ > 3");
            }});
        }});
        severityConfigurations.add(new AlertConfiguration.SeverityConfiguration() {{
            setSeverity(AlertConfiguration.Severity.Medium);
        }});
        alertConfiguration.setSeverityConfigurations(severityConfigurations);

        alertConfiguration.setNoDataFire(true);
        alertConfiguration.setNoDataSeverity(AlertConfiguration.Severity.High);
        alertConfiguration.setSendResolved(true);

        List<AlertConfiguration.JoinConfiguration> joinConfigs = new ArrayList<AlertConfiguration.JoinConfiguration>();
        AlertConfiguration.JoinConfiguration joinConfig = new AlertConfiguration.JoinConfiguration();
        joinConfig.setType("left_join");
        joinConfig.setCondition("$0.name == $1.name");
        joinConfigs.add(joinConfig);
        alertConfiguration.setJoinConfigurations(joinConfigs);

        AlertConfiguration.GroupConfiguration groupConfig = new AlertConfiguration.GroupConfiguration();
        groupConfig.setType("no_group");
        alertConfiguration.setGroupConfiguration(groupConfig);

        AlertConfiguration.PolicyConfiguration policyConfiguration = new AlertConfiguration.PolicyConfiguration();
        policyConfiguration.setRepeatInterval("4m");
        policyConfiguration.setUseDefault(true);
        policyConfiguration.setAlertPolicyId("xxxxxx");
        policyConfiguration.setActionPolicyId("yyyyyyyy");
        alertConfiguration.setPolicyConfiguration(policyConfiguration);

        String body = JsonUtils.serialize(alertConfiguration);
        assertEquals(body, "{\"annotations\":[{\"key\":\"ak1\",\"value\":\"av1\"}],\"autoAnnotation\":false,\"conditionConfiguration\":{\"condition\":\"name == 'k8s'\",\"countCondition\":\"__count__ > 20\"},\"groupConfiguration\":{\"type\":\"no_group\"},\"joinConfigurations\":[{\"condition\":\"$0.name == $1.name\",\"type\":\"left_join\"}],\"labels\":[{\"key\":\"l1\",\"value\":\"v1\"},{\"key\":\"l2\",\"value\":\"v2\"}],\"noDataFire\":true,\"noDataSeverity\":8,\"notifyThreshold\":1,\"policyConfiguration\":{\"actionPolicyId\":\"yyyyyyyy\",\"alertPolicyId\":\"xxxxxx\",\"repeatInterval\":\"4m\",\"useDefault\":true},\"queryList\":[{\"chartTitle\":\"chart1\",\"end\":\"now\",\"logStore\":\"logstore-test\",\"project\":\"test-project\",\"query\":\"* | select name, count(uid) as uv group by name\",\"region\":\"cn-heyuan\",\"roleArn\":\"acs:*:xxxxx\",\"start\":\"-60s\",\"timeSpanType\":\"Custom\"},{\"end\":\"now\",\"query\":\"* | select name, min(latency) group by name\",\"start\":\"-86400s\",\"store\":\"test-alert-latency\",\"timeSpanType\":\"Relative\"}],\"sendRecoveryMessage\":false,\"sendResolved\":true,\"severityConfigurations\":[{\"evalCondition\":{\"condition\":\"latency > 90\",\"countCondition\":\"__count__ > 3\"},\"severity\":8},{\"severity\":6}],\"threshold\":1,\"type\":\"default\",\"version\":\"2.0\"}");
    }

    @Test
    public void testConfigurationV2Deserialize() {
        String body = "{\"annotations\":[{\"key\":\"ak1\",\"value\":\"av1\"}],\"conditionConfiguration\":{\"condition\":\"name == 'k8s'\",\"countCondition\":\"__count__ > 20\"},\"groupConfiguration\":{\"type\":\"no_group\"},\"joinConfigurations\":[{\"condition\":\"$0.name == $1.name\",\"type\":\"left_join\"}],\"labels\":[{\"key\":\"l1\",\"value\":\"v1\"},{\"key\":\"l2\",\"value\":\"v2\"}],\"noDataFire\":true,\"noDataSeverity\":8,\"notifyThreshold\":1,\"policyConfiguration\":{\"actionPolicyId\":\"yyyyyyyy\",\"alertPolicyId\":\"xxxxxx\",\"repeatInterval\":\"4m\",\"useDefault\":true},\"queryList\":[{\"chartTitle\":\"chart1\",\"end\":\"now\",\"logStore\":\"logstore-test\",\"project\":\"test-project\",\"query\":\"* | select name, count(uid) as uv group by name\",\"region\":\"cn-heyuan\",\"roleArn\":\"acs:*:xxxxx\",\"start\":\"-60s\",\"timeSpanType\":\"Custom\"},{\"end\":\"now\",\"query\":\"* | select name, min(latency) group by name\",\"start\":\"-86400s\",\"store\":\"test-alert-latency\",\"timeSpanType\":\"Relative\"}],\"sendRecoveryMessage\":false,\"sendResolved\":true,\"severityConfigurations\":[{\"evalCondition\":{\"condition\":\"latency > 90\",\"countCondition\":\"__count__ > 3\"},\"severity\":8},{\"severity\":6}],\"threshold\":1,\"type\":\"default\",\"version\":\"2.0\"}";

        AlertConfiguration alertConfiguration = new AlertConfiguration();
        alertConfiguration.deserialize(JSONObject.parseObject(body));
        assertEquals(alertConfiguration.getVersion(), "2.0");
        assertEquals(alertConfiguration.getType(), "default");
        assertEquals(alertConfiguration.getThreshold(), 1);

        assertNotNull(alertConfiguration.getQueryList());
        assertEquals(alertConfiguration.getQueryList().size(), 2);
        assertEquals(alertConfiguration.getQueryList().get(0).getProject(), "test-project");
        assertEquals(alertConfiguration.getQueryList().get(0).getRoleArn(), "acs:*:xxxxx");
        assertEquals(alertConfiguration.getQueryList().get(0).getQuery(), "* | select name, count(uid) as uv group by name");
        assertEquals(alertConfiguration.getQueryList().get(0).getRegion(), "cn-heyuan");

        assertEquals(alertConfiguration.getConditionConfiguration().getCondition(), "name == 'k8s'");
        assertEquals(alertConfiguration.getConditionConfiguration().getCountCondition(), "__count__ > 20");

        assertEquals(alertConfiguration.getThreshold(), 1);

        assertEquals(alertConfiguration.getLabels().size(), 2);
        assertEquals(alertConfiguration.getLabels().get(0).getKey(), "l1");
        assertEquals(alertConfiguration.getLabels().get(0).getValue(), "v1");
        assertEquals(alertConfiguration.getLabels().get(1).getKey(), "l2");
        assertEquals(alertConfiguration.getLabels().get(1).getValue(), "v2");

        assertEquals(alertConfiguration.getAnnotations().size(), 1);
        assertEquals(alertConfiguration.getAnnotations().get(0).getKey(), "ak1");
        assertEquals(alertConfiguration.getAnnotations().get(0).getValue(), "av1");


        assertTrue(alertConfiguration.isNoDataFire());
        assertFalse(alertConfiguration.isAutoAnnotation());
        assertEquals(alertConfiguration.getNoDataSeverity(), AlertConfiguration.Severity.High.value());
        assertTrue(alertConfiguration.isSendResolved());

        assertEquals(alertConfiguration.getJoinConfigurations().size(), 1);
        assertEquals(alertConfiguration.getJoinConfigurations().get(0).getType(), "left_join");
        assertEquals(alertConfiguration.getJoinConfigurations().get(0).getCondition(), "$0.name == $1.name");

        assertEquals(alertConfiguration.getGroupConfiguration().getType(), "no_group");

        assertEquals(alertConfiguration.getPolicyConfiguration().getRepeatInterval(), "4m");
        assertTrue(alertConfiguration.getPolicyConfiguration().isUseDefault());
        assertEquals(alertConfiguration.getPolicyConfiguration().getAlertPolicyId(), "xxxxxx");
        assertEquals(alertConfiguration.getPolicyConfiguration().getActionPolicyId(), "yyyyyyyy");
    }

    @Test
    public void testAlertConfigurationSerialize() {
        AlertConfiguration alertConfiguration = new AlertConfiguration();
        alertConfiguration.setVersion("2.0");
        alertConfiguration.setThreshold(1);
        alertConfiguration.setType("tpl");
        AlertConfiguration.TemplateConfiguration templateConfiguration = new AlertConfiguration.TemplateConfiguration();
        templateConfiguration.setVersion("1");
        templateConfiguration.setType("sys");
        templateConfiguration.setLang("cn");
        HashMap<String, String> tokens = new HashMap<String, String>();
        tokens.put("default.logstore", "test_logstore");
        tokens.put("default.app", "sls.audit.alert_policy_default");
        templateConfiguration.setTokens(tokens);
        alertConfiguration.setTemplateConfiguration(templateConfiguration);

        String body = JsonUtils.serialize(alertConfiguration);
        assertEquals(body, "{\"autoAnnotation\":false,\"noDataFire\":false,\"noDataSeverity\":6,\"notifyThreshold\":1,\"sendRecoveryMessage\":false,\"sendResolved\":false,\"templateConfiguration\":{\"lang\":\"cn\",\"tokens\":{\"default.logstore\":\"test_logstore\",\"default.app\":\"sls.audit.alert_policy_default\"},\"type\":\"sys\",\"version\":\"1\"},\"threshold\":1,\"type\":\"tpl\",\"version\":\"2.0\"}");
    }

    @Test
    public void testConfigurationDeserialize() {
        String body = "{\"noDataFire\":false,\"noDataSeverity\":6,\"notifyThreshold\":1,\"sendRecoveryMessage\":false,\"sendResolved\":false,\"templateConfiguration\":{\"lang\":\"cn\",\"tokens\":{\"default.logstore\":\"test_logstore\",\"default.app\":\"sls.audit.alert_policy_default\"},\"type\":\"sys\",\"version\":\"1\"},\"threshold\":1,\"type\":\"tpl\",\"version\":\"2.0\"}";
        AlertConfiguration alertConfiguration = new AlertConfiguration();
        alertConfiguration.deserialize(JSONObject.parseObject(body));
        assertEquals(alertConfiguration.getVersion(), "2.0");
        assertEquals(alertConfiguration.getType(), "tpl");
        assertEquals(alertConfiguration.getThreshold(), 1);
        assertFalse(alertConfiguration.isNoDataFire());
        assertFalse(alertConfiguration.isAutoAnnotation());
        assertNotNull(alertConfiguration.getGroupConfiguration());
        assertNotNull(alertConfiguration.getJoinConfigurations());
        assertNotNull(alertConfiguration.getSeverityConfigurations());
        assertNotNull(alertConfiguration.getAnnotations());
        assertNotNull(alertConfiguration.getLabels());
        assertNotNull(alertConfiguration.getQueryList());
        assertNotNull(alertConfiguration.getConditionConfiguration());
        assertNotNull(alertConfiguration.getTemplateConfiguration());
        assertNull(alertConfiguration.getTemplateConfiguration().getId());
        assertEquals(alertConfiguration.getTemplateConfiguration().getLang(), "cn");
        assertEquals(alertConfiguration.getTemplateConfiguration().getType(), "sys");
        assertEquals(alertConfiguration.getTemplateConfiguration().getVersion(), "1");
        Map<String, String> tokens = alertConfiguration.getTemplateConfiguration().getTokens();
        assertEquals(tokens.get("default.logstore"), "test_logstore");
        assertEquals(tokens.get("default.app"), "sls.audit.alert_policy_default");
        assertNull(tokens.get("xxx"));
    }

    @Test
    public void testAlertSerialize() {
        Alert alert = new Alert();
        alert.setType(JobType.ALERT);
        AlertConfiguration alertConfiguration = new AlertConfiguration();
        alertConfiguration.setVersion("2.0");
        alertConfiguration.setThreshold(1);
        alertConfiguration.setType("tpl");
        AlertConfiguration.TemplateConfiguration templateConfiguration = new AlertConfiguration.TemplateConfiguration();
        templateConfiguration.setVersion("1");
        templateConfiguration.setType("sys");
        templateConfiguration.setLang("cn");
        HashMap<String, String> tokens = new HashMap<String, String>();
        tokens.put("default.logstore", "test_logstore");
        tokens.put("default.app", "sls.audit.alert_policy_default");
        templateConfiguration.setTokens(tokens);
        HashMap<String, String> annotations = new HashMap<String, String>();
        annotations.put("__k2", "dwdd");
        templateConfiguration.setAnnotations(annotations);
        alertConfiguration.setTemplateConfiguration(templateConfiguration);
        alert.setConfiguration(alertConfiguration);
        alert.setStatus(JobState.ENABLED.toString());
        JobSchedule schedule = new JobSchedule();
        schedule.setInterval("60s");
        schedule.setType(JobScheduleType.FIXED_RATE);
        alert.setSchedule(schedule);

        String body = JsonUtils.serialize(alert);
        assertEquals(body, "{\"configuration\":{\"autoAnnotation\":false,\"noDataFire\":false,\"noDataSeverity\":6,\"notifyThreshold\":1,\"sendRecoveryMessage\":false,\"sendResolved\":false,\"templateConfiguration\":{\"annotations\":{\"__k2\":\"dwdd\"},\"lang\":\"cn\",\"tokens\":{\"default.logstore\":\"test_logstore\",\"default.app\":\"sls.audit.alert_policy_default\"},\"type\":\"sys\",\"version\":\"1\"},\"threshold\":1,\"type\":\"tpl\",\"version\":\"2.0\"},\"recyclable\":false,\"schedule\":{\"interval\":\"60s\",\"runImmediately\":false,\"type\":\"FixedRate\"},\"status\":\"Enabled\",\"type\":\"Alert\"}");
    }

    @Test
    public void testAlertDeserialize() {
        String body = "{\"state\":\"Enabled\",\"name\":\"alert-1\",\"createTime\":1542763714,\"" +
                "lastModifiedTime\":1542763714,\"configuration\":{\"noDataFire\":false,\"noDataSeverity\":6," +
                "\"notifyThreshold\":1,\"sendRecoveryMessage\":false,\"sendResolved\":false," +
                "\"templateConfiguration\":{\"lang\":\"cn\"," +
                "\"tokens\":{\"default.logstore\":\"test_logstore\",\"default.app\":\"sls.audit.alert_policy_default\"}," +
                "\"type\":\"sys\",\"version\":\"1\"},\"threshold\":1,\"type\":\"tpl\",\"version\":\"2.0\"}," +
                "\"recyclable\":false,\"schedule\":{\"interval\":\"60s\",\"runImmediately\":false," +
                "\"type\":\"FixedRate\"},\"status\":\"Enabled\",\"type\":\"Alert\"}";
        Alert alert = new Alert();
        alert.deserialize(JSONObject.parseObject(body));

        assertEquals("alert-1", alert.getName());
        assertNull(alert.getDescription());
        assertEquals(JobState.ENABLED, alert.getState());
        assertEquals(JobType.ALERT, alert.getType());
    }


    @Test
    public void testJobDeserialize() {
        String body = "{\"configuration\":{\"condition\":\"ID > 100\",\"dashboard\":\"dashboardtest\"," +
                "\"notificationList\":[{\"content\":\"messagetest\",\"mobileList\":[\"86-13738162867\"]," +
                "\"type\":\"SMS\"}],\"notifyThreshold\":1,\"queryList\":[{\"chartTitle\":\"chart1\",\"end\":" +
                "\"now\",\"logStore\":\"logstore-test\",\"query\":\"*\",\"start\":\"-60s\",\"timeSpanType\":" +
                "\"Custom\"}]},\"name\":\"alertTest\",\"schedule\":{\"interval\":\"60s\"," +
                "\"type\":\"FixedRate\"},\"state\":\"Enabled\",\"type\":\"Alert\",\"createTime\":1542763714,\"lastModifiedTime\":1542763714}";
        Alert alert = new Alert();
        alert.deserialize(JSONObject.parseObject(body));

        assertEquals("alertTest", alert.getName());
        assertNull(alert.getDescription());
        assertEquals(JobState.ENABLED, alert.getState());
        assertEquals(JobType.ALERT, alert.getType());

        JobSchedule schedule = new JobSchedule();
        schedule.setInterval("60s");
        schedule.setType(JobScheduleType.FIXED_RATE);
        assertEquals(schedule, alert.getSchedule());
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
        assertEquals(configuration, alert.getConfiguration());
    }

    @Test
    public void testAlertConfigurationDeserializeNull() {
        String configuration = "{\"condition\":\"\",\"dashboard\":\"\",\"queryList\":[{\"chartTitle\":\"\",\"logStore\":\"\",\"query\":\"* | " +
                "select name, min(latency) as latency group by name\",\"timeSpanType\":\"Custom\",\"start\":\"-3600s\",\"end\":\"now\"," +
                "\"storeType\":\"log\",\"project\":\"\",\"store\":\"test-alert-latency\",\"ui\":\"\",\"region\":\"\",\"roleArn\":\"\"}," +
                "{\"chartTitle\":\"\",\"logStore\":\"\",\"query\":\"* | select name, count(uid) as uv group by name\"," +
                "\"timeSpanType\":\"Custom\",\"start\":\"-3600s\",\"end\":\"now\",\"storeType\":\"log\",\"project\":\"\"," +
                "\"store\":\"test-alert-access\",\"ui\":\"\",\"region\":\"\",\"roleArn\":\"\"}],\"muteUntil\":1605622333," +
                "\"notificationList\":null,\"notifyThreshold\":0,\"throttling\":\"\",\"version\":\"2.0\",\"type\":\"default\"," +
                "\"threshold\":1,\"noDataFire\":true,\"noDataSeverity\":10,\"sendResolved\":true,\"templateConfiguration\":null," +
                "\"joinConfigurations\":[{\"type\":\"left_join\",\"condition\":\"$0.name == $1.name\",\"ui\":\"\"}]," +
                "\"groupConfiguration\":{\"type\":\"custom\",\"fields\":[\"name\"]},\"conditionConfiguration\":{\"condition\":\"uv > 200\",\"countCondition\":\"__count__ > 1\"}," +
                "\"annotations\":[{\"key\":\"ak1\",\"value\":\"av1\"},{\"key\":\"title\",\"value\":\"test-huolang-alert-2\"}]," +
                "\"labels\":[{\"key\":\"lk1\",\"value\":\"lv1\"}]," +
                "\"severityConfigurations\":[{\"severity\":8,\"evalCondition\":{\"condition\":\"uv > 100\",\"countCondition\":\"\"}}," +
                "{\"severity\":6,\"evalCondition\":{\"condition\":\"\",\"countCondition\":\"\"}}]," +
                "\"policyConfiguration\":{\"useDefault\":false,\"repeatInterval\":\"3m\",\"alertPolicyId\":\"sls.builtin.dynamic\",\"actionPolicyId\":\"test-action-policy-1\"}}";
        AlertConfiguration alertConfiguration = new AlertConfiguration();
        alertConfiguration.deserialize(JSONObject.parseObject(configuration));
    }

}
