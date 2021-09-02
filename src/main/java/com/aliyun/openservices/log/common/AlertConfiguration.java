package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Configuration for alert job.
 */
public class AlertConfiguration extends DashboardBasedJobConfiguration {

    /**
     * The trigger condition expression e.g $0.xx > 100 and $1.yy < 100.
     * Which depends on the order of queries in {@code queryList}.
     */
    @Deprecated
    @JSONField
    private String condition;

    @JSONField
    private List<Query> queryList;

    @JSONField
    private Date muteUntil;

    /**
     * Optional notify threshold, defaults to 1.
     */
    @Deprecated
    @JSONField
    private Integer notifyThreshold = 1;

    /**
     * Duration with format '1h', '2s'
     */
    @JSONField
    private String throttling;

    @Deprecated
    @JSONField
    private boolean sendRecoveryMessage;

    @JSONField
    private boolean autoAnnotation;
    @JSONField
    private String version;
    @JSONField
    private String type;
    /**
     * Optional eval threshold, defaults to 1.
     */
    @JSONField
    private int threshold = 1;
    @JSONField
    private boolean noDataFire;
    @JSONField
    private int noDataSeverity = Severity.Medium.value();
    @JSONField
    private boolean sendResolved;
    @JSONField
    private TemplateConfiguration templateConfiguration;
    @JSONField
    private ConditionConfiguration conditionConfiguration;
    @JSONField
    private List<Tag> annotations;
    @JSONField
    private List<Tag> labels;
    @JSONField
    private List<SeverityConfiguration> severityConfigurations;
    @JSONField
    private List<JoinConfiguration> joinConfigurations;
    @JSONField
    private GroupConfiguration groupConfiguration;
    @JSONField
    private PolicyConfiguration policyConfiguration;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<Query> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<Query> queryList) {
        this.queryList = queryList;
    }

    public Date getMuteUntil() {
        return muteUntil;
    }

    public void setMuteUntil(Date muteUntil) {
        this.muteUntil = muteUntil;
    }

    @Deprecated
    public Integer getNotifyThreshold() {
        return notifyThreshold;
    }

    @Deprecated
    public void setNotifyThreshold(Integer notifyThreshold) {
        this.notifyThreshold = notifyThreshold;
    }

    @Deprecated
    public String getThrottling() {
        return throttling;
    }

    @Deprecated
    public void setThrottling(String throttling) {
        this.throttling = throttling;
    }

    @Deprecated
    public boolean getSendRecoveryMessage() {
        return sendRecoveryMessage;
    }

    @Deprecated
    public void setSendRecoveryMessage(boolean sendRecoveryMessage) {
        this.sendRecoveryMessage = sendRecoveryMessage;
    }

    public boolean isAutoAnnotation() {
        return autoAnnotation;
    }

    public void setAutoAnnotation(boolean autoAnnotation) {
        this.autoAnnotation = autoAnnotation;
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        setVersion(JsonUtils.readOptionalString(value, "version"));
        if (getVersion() != null) {
            deserializeAlert2(value);
        } else {
            deserializeAlert(value);
        }
    }

    private void deserializeAlert(JSONObject value) {
        condition = value.getString("condition");
        queryList = JsonUtils.readList(value, "queryList", new Unmarshaller<Query>() {
            @Override
            public Query unmarshal(JSONArray value, int index) {
                Query query = new Query();
                query.deserialize(value.getJSONObject(index));
                return query;
            }
        });
        if (value.containsKey("muteUntil")) {
            muteUntil = Utils.timestampToDate(value.getLong("muteUntil"));
        }
        notifyThreshold = JsonUtils.readOptionalInt(value, "notifyThreshold");
        throttling = JsonUtils.readOptionalString(value, "throttling");
        sendRecoveryMessage = JsonUtils.readBool(value, "sendRecoveryMessage", false);
    }

    private void deserializeAlert2(JSONObject value) {
        if (value.containsKey("muteUntil")) {
            muteUntil = Utils.timestampToDate(value.getLong("muteUntil"));
        }
        setVersion(JsonUtils.readOptionalString(value, "version"));
        setType(JsonUtils.readOptionalString(value, "type"));
        if (value.containsKey("threshold")) {
            setThreshold(value.getInteger("threshold"));
        }
        if (value.containsKey("noDataFire")) {
            setNoDataFire(value.getBoolean("noDataFire"));
        }
        if (value.containsKey("noDataSeverity")) {
            Severity severityVal = Severity.valueOf(value.getInteger("noDataSeverity"));
            if (severityVal != null) {
                setNoDataSeverity(severityVal);
            }
        }
        if (value.containsKey("autoAnnotation")) {
            setAutoAnnotation(value.getBoolean("autoAnnotation"));
        }
        if (value.containsKey("sendResolved")) {
            setSendResolved(value.getBoolean("sendResolved"));
        }
        templateConfiguration = new TemplateConfiguration();
        if (value.containsKey("templateConfiguration") && value.getJSONObject("templateConfiguration") != null) {
            templateConfiguration.deserialize(value.getJSONObject("templateConfiguration"));
        }
        conditionConfiguration = new ConditionConfiguration();
        if (value.containsKey("conditionConfiguration")) {
            conditionConfiguration.deserialize(value.getJSONObject("conditionConfiguration"));
        }
        queryList = JsonUtils.readList(value, "queryList", new Unmarshaller<Query>() {
            @Override
            public Query unmarshal(JSONArray value, int index) {
                Query query = new Query();
                query.deserialize(value.getJSONObject(index));
                return query;
            }
        });
        annotations = JsonUtils.readList(value, "annotations", new Unmarshaller<Tag>() {
            @Override
            public Tag unmarshal(JSONArray value, int index) {
                Tag tag = new Tag();
                tag.deserialize(value.getJSONObject(index));
                return tag;
            }
        });
        labels = JsonUtils.readList(value, "labels", new Unmarshaller<Tag>() {
            @Override
            public Tag unmarshal(JSONArray value, int index) {
                Tag tag = new Tag();
                tag.deserialize(value.getJSONObject(index));
                return tag;
            }
        });

        severityConfigurations = JsonUtils.readList(value, "severityConfigurations", new Unmarshaller<SeverityConfiguration>() {
            @Override
            public SeverityConfiguration unmarshal(JSONArray value, int index) {
                SeverityConfiguration severityConfiguration = new SeverityConfiguration();
                severityConfiguration.deserialize(value.getJSONObject(index));
                return severityConfiguration;
            }
        });

        joinConfigurations = JsonUtils.readList(value, "joinConfigurations", new Unmarshaller<JoinConfiguration>() {
            @Override
            public JoinConfiguration unmarshal(JSONArray value, int index) {
                JoinConfiguration joinConfiguration = new JoinConfiguration();
                joinConfiguration.deserialize(value.getJSONObject(index));
                return joinConfiguration;
            }
        });
        groupConfiguration = new GroupConfiguration();
        if (value.containsKey("groupConfiguration") && value.getJSONObject("groupConfiguration") != null) {
            groupConfiguration.deserialize(value.getJSONObject("groupConfiguration"));
        }

        policyConfiguration = new PolicyConfiguration();
        if (value.containsKey("policyConfiguration") && value.getJSONObject("policyConfiguration") != null) {
            policyConfiguration.deserialize(value.getJSONObject("policyConfiguration"));
        }
    }

    @Deprecated
    @Override
    Notification makeQualifiedNotification(NotificationType type) {
        switch (type) {
            case DING_TALK:
                return new DingTalkNotification();
            case EMAIL:
                return new EmailNotification();
            case MESSAGE_CENTER:
                return new MessageCenterNotification();
            case SMS:
                return new SmsNotification();
            case WEBHOOK:
                return new WebhookNotification();
            case VOICE:
                return new VoiceNotification();
            default:
                throw new IllegalArgumentException("Unimplemented notification type: " + type);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertConfiguration that = (AlertConfiguration) o;

        if (sendRecoveryMessage != that.sendRecoveryMessage) return false;
        if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
        if (queryList != null ? !queryList.equals(that.queryList) : that.queryList != null) return false;
        if (muteUntil != null ? !muteUntil.equals(that.muteUntil) : that.muteUntil != null) return false;
        if (notifyThreshold != null ? !notifyThreshold.equals(that.notifyThreshold) : that.notifyThreshold != null)
            return false;
        return throttling != null ? throttling.equals(that.throttling) : that.throttling == null;
    }

    @Override
    public int hashCode() {
        int result = condition != null ? condition.hashCode() : 0;
        result = 31 * result + (queryList != null ? queryList.hashCode() : 0);
        result = 31 * result + (muteUntil != null ? muteUntil.hashCode() : 0);
        result = 31 * result + (notifyThreshold != null ? notifyThreshold.hashCode() : 0);
        result = 31 * result + (throttling != null ? throttling.hashCode() : 0);
        result = 31 * result + (sendRecoveryMessage ? 1 : 0);
        return result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isNoDataFire() {
        return noDataFire;
    }

    public void setNoDataFire(boolean noDataFire) {
        this.noDataFire = noDataFire;
    }

    public int getNoDataSeverity() {
        return noDataSeverity;
    }

    public void setNoDataSeverity(Severity noDataSeverity) {
        this.noDataSeverity = noDataSeverity.value();
    }

    public boolean isSendResolved() {
        return sendResolved;
    }

    public void setSendResolved(boolean sendResolved) {
        this.sendResolved = sendResolved;
    }

    public TemplateConfiguration getTemplateConfiguration() {
        return templateConfiguration;
    }

    public void setTemplateConfiguration(TemplateConfiguration templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
    }

    public ConditionConfiguration getConditionConfiguration() {
        return conditionConfiguration;
    }

    public void setConditionConfiguration(ConditionConfiguration conditionConfiguration) {
        this.conditionConfiguration = conditionConfiguration;
    }

    public List<Tag> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Tag> annotations) {
        this.annotations = annotations;
    }

    public List<Tag> getLabels() {
        return labels;
    }

    public void setLabels(List<Tag> labels) {
        this.labels = labels;
    }

    public List<SeverityConfiguration> getSeverityConfigurations() {
        return severityConfigurations;
    }

    public void setSeverityConfigurations(List<SeverityConfiguration> severityConfigurations) {
        this.severityConfigurations = severityConfigurations;
    }

    public List<JoinConfiguration> getJoinConfigurations() {
        return joinConfigurations;
    }

    public void setJoinConfigurations(List<JoinConfiguration> joinConfigurations) {
        this.joinConfigurations = joinConfigurations;
    }

    public GroupConfiguration getGroupConfiguration() {
        return groupConfiguration;
    }

    public void setGroupConfiguration(GroupConfiguration groupConfiguration) {
        this.groupConfiguration = groupConfiguration;
    }

    public PolicyConfiguration getPolicyConfiguration() {
        return policyConfiguration;
    }

    public void setPolicyConfiguration(PolicyConfiguration policyConfiguration) {
        this.policyConfiguration = policyConfiguration;
    }

    public enum Severity {
        Report(2), Low(4), Medium(6), High(8), Critical(10);

        private int value = 0;

        private Severity(int value) {
            this.value = value;
        }

        public static Severity valueOf(int value) {
            switch (value) {
                case 2:
                    return Report;
                case 4:
                    return Low;
                case 6:
                    return Medium;
                case 8:
                    return High;
                case 10:
                    return Critical;
                default:
                    return null;
            }
        }

        public int value() {
            return this.value;
        }
    }

    public enum JoinType implements JSONSerializable {
        CROSS_JOIN("cross_join"),
        INNER_JOIN("inner_join"),
        LEFT_JOIN("left_join"),
        RIGHT_JOIN("right_join"),
        FULL_JOIN("full_join"),
        LEFT_EXCLUDE("left_exclude"),
        RIGHT_EXCLUDE("right_exclude"),
        CONCAT("concat"),
        NO_JOIN("no_join");

        private final String value;

        JoinType(String value) {
            this.value = value;
        }

        public static JoinType fromString(String value) {
            for (JoinType type : JoinType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
            serializer.write(toString());
        }
    }

    public enum GroupType implements JSONSerializable {
        NO_GROUP("no_group"),
        LABELS_AUTO("labels_auto"),
        CUSTOM("custom");

        private final String value;

        GroupType(String value) {
            this.value = value;
        }

        public static GroupType fromString(String value) {
            for (GroupType type : GroupType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
            serializer.write(toString());
        }
    }

    public enum StoreType implements JSONSerializable {
        LOG("log"),
        METRIC("metric"),
        META("meta");

        private final String value;

        StoreType(String value) {
            this.value = value;
        }

        public static StoreType fromString(String value) {
            for (StoreType type : StoreType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
            serializer.write(toString());
        }
    }

    public static class TemplateConfiguration {
        @JSONField
        private String id;
        @JSONField
        private String type;
        @JSONField
        private String version;
        @JSONField
        private String lang;
        @JSONField
        private Map<String, String> tokens;
        @JSONField
        private Map<String, String> annotations;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public Map<String, String> getTokens() {
            return tokens;
        }

        public void setTokens(Map<String, String> tokens) {
            this.tokens = tokens;
        }

        public Map<String, String> getAnnotations() {
            return annotations;
        }

        public void setAnnotations(Map<String, String> annotations) {
            this.annotations = annotations;
        }

        public void deserialize(JSONObject value) {
            setId(JsonUtils.readOptionalString(value, "id"));
            setType(JsonUtils.readOptionalString(value, "type"));
            setLang(JsonUtils.readOptionalString(value, "lang"));
            setVersion(JsonUtils.readOptionalString(value, "version"));
            setTokens(JsonUtils.readOptionalMap(value, "tokens"));
            setAnnotations(JsonUtils.readOptionalMap(value, "annotations"));
        }
    }

    public static class ConditionConfiguration {
        @JSONField
        private String condition;
        @JSONField
        private String countCondition;

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getCountCondition() {
            return countCondition;
        }

        public void setCountCondition(String countCondition) {
            this.countCondition = countCondition;
        }

        public void deserialize(JSONObject value) {
            if (value != null) {
                setCondition(JsonUtils.readOptionalString(value, "condition"));
                setCountCondition(JsonUtils.readOptionalString(value, "countCondition"));
            }
        }
    }

    public static class JoinConfiguration {
        @JSONField
        private String type;
        @JSONField
        private String condition;
        @JSONField
        private String ui;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getUi() {
            return ui;
        }

        public void setUi(String ui) {
            this.ui = ui;
        }

        public void deserialize(JSONObject value) {
            setType(value.getString("type"));
            setCondition(value.getString("condition"));
            setUi(value.getString("ui"));
        }
    }

    public static class Tag {
        @JSONField
        private String key;
        @JSONField
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }


        public void deserialize(JSONObject value) {
            if (value != null) {
                setKey(value.getString("key"));
                setValue(value.getString("value"));
            }
        }
    }

    public static class SeverityConfiguration {
        @JSONField
        private int severity;
        @JSONField
        private ConditionConfiguration evalCondition;

        public int getSeverity() {
            return severity;
        }

        public void setSeverity(Severity severity) {
            this.severity = severity.value();
        }

        public ConditionConfiguration getEvalCondition() {
            return evalCondition;
        }

        public void setEvalCondition(ConditionConfiguration evalCondition) {
            this.evalCondition = evalCondition;
        }


        public void deserialize(JSONObject value) {
            if (value.containsKey("severity")) {
                setSeverity(Severity.valueOf(value.getInteger("severity")));
            }
            evalCondition = new ConditionConfiguration();
            if (value.containsKey("evalCondition") && value.getJSONObject("evalCondition") != null) {
                evalCondition.deserialize(value.getJSONObject("evalCondition"));
            }
        }
    }

    public static class GroupConfiguration {
        @JSONField
        private String type;
        @JSONField
        private List<String> fields;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getFields() {
            return fields;
        }

        public void setFields(List<String> fields) {
            this.fields = fields;
        }

        public void deserialize(JSONObject value) {
            setType(value.getString("type"));
            setFields(JsonUtils.readStringList(value, "fields"));
        }
    }

    public static class PolicyConfiguration {
        @JSONField
        private String actionPolicyId;
        @JSONField
        private String alertPolicyId;
        @JSONField
        private boolean useDefault;
        @JSONField
        private String repeatInterval;

        public String getActionPolicyId() {
            return actionPolicyId;
        }

        public void setActionPolicyId(String actionPolicyId) {
            this.actionPolicyId = actionPolicyId;
        }

        public String getAlertPolicyId() {
            return alertPolicyId;
        }

        public void setAlertPolicyId(String alertPolicyId) {
            this.alertPolicyId = alertPolicyId;
        }

        public boolean isUseDefault() {
            return useDefault;
        }

        public void setUseDefault(boolean useDefault) {
            this.useDefault = useDefault;
        }

        public String getRepeatInterval() {
            return repeatInterval;
        }

        public void setRepeatInterval(String repeatInterval) {
            this.repeatInterval = repeatInterval;
        }

        public void deserialize(JSONObject value) {
            setUseDefault(value.getBoolean("useDefault"));
            setRepeatInterval(JsonUtils.readOptionalString(value, "repeatInterval"));
            setActionPolicyId(JsonUtils.readOptionalString(value, "actionPolicyId"));
            setAlertPolicyId(JsonUtils.readOptionalString(value, "alertPolicyId"));
        }
    }

}
