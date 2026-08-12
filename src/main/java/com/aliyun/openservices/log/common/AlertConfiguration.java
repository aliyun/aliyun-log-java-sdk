package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JsonEnumAdapter;
import com.aliyun.openservices.log.internal.json.JsonEnumCreator;
import com.aliyun.openservices.log.internal.json.JsonEnumValue;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.util.Utils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

/**
 * Configuration for alert job.
 */
public class AlertConfiguration extends DashboardBasedJobConfiguration {

    /**
     * The trigger condition expression e.g $0.xx > 100 and $1.yy < 100.
     * Which depends on the order of queries in {@code queryList}.
     */
    @Deprecated
    private String condition;

    private List<Query> queryList;

    private Date muteUntil;

    /**
     * Optional notify threshold, defaults to 1.
     */
    @Deprecated
    private Integer notifyThreshold = 1;

    /**
     * Duration with format '1h', '2s'
     */
    private String throttling;

    @Deprecated
    private boolean sendRecoveryMessage;

    private boolean autoAnnotation;
    private String version;
    private String type;
    /**
     * Optional eval threshold, defaults to 1.
     */
    private int threshold = 1;
    private boolean noDataFire;
    private int noDataSeverity = Severity.Medium.value();
    private boolean sendResolved;
    private TemplateConfiguration templateConfiguration;
    private ConditionConfiguration conditionConfiguration;
    private List<Tag> annotations;
    private List<Tag> labels;
    private List<SeverityConfiguration> severityConfigurations;
    private List<JoinConfiguration> joinConfigurations;
    private GroupConfiguration groupConfiguration;
    private PolicyConfiguration policyConfiguration;
    private List<String> tags;
    private SinkEventStoreConfiguration sinkEventStore;
    private SinkCmsConfiguration sinkCms;
    private SinkAlerthubConfiguration sinkAlerthub;

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
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (muteUntil != null) {
            value.put("muteUntil", Utils.dateToTimestamp(muteUntil));
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
        setVersion(JsonUtils.readOptionalString(value, "version"));
        if (getVersion() != null) {
            fromJsonObjectV2(value);
        } else {
            fromJsonObjectV1(value);
        }
    }

    private void fromJsonObjectV1(JSONObject value) {
        condition = value.getString("condition");
        queryList = JsonUtils.readList(value, "queryList", new Unmarshaller<Query>() {
            @Override
            public Query unmarshal(JSONArray value, int index) {
                Query query = new Query();
                query.fromJsonObject(value.getJSONObject(index));
                return query;
            }
        });
        if (value.containsKey("muteUntil")) {
            muteUntil = Utils.timestampToDate(value.getLong("muteUntil"));
        }
        notifyThreshold = JsonUtils.readOptionalInt(value, "notifyThreshold");
        throttling = JsonUtils.readOptionalString(value, "throttling");
        sendRecoveryMessage = JsonUtils.readBool(value, "sendRecoveryMessage", false);
        if (value.containsKey("tags")) {
            tags = JsonUtils.readStringList(value, "tags");
        }
    }

    private void fromJsonObjectV2(JSONObject value) {
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
            templateConfiguration.fromJsonObject(value.getJSONObject("templateConfiguration"));
        }
        conditionConfiguration = new ConditionConfiguration();
        if (value.containsKey("conditionConfiguration")) {
            conditionConfiguration.fromJsonObject(value.getJSONObject("conditionConfiguration"));
        }
        queryList = JsonUtils.readList(value, "queryList", new Unmarshaller<Query>() {
            @Override
            public Query unmarshal(JSONArray value, int index) {
                Query query = new Query();
                query.fromJsonObject(value.getJSONObject(index));
                return query;
            }
        });
        annotations = JsonUtils.readList(value, "annotations", new Unmarshaller<Tag>() {
            @Override
            public Tag unmarshal(JSONArray value, int index) {
                Tag tag = new Tag();
                tag.fromJsonObject(value.getJSONObject(index));
                return tag;
            }
        });
        labels = JsonUtils.readList(value, "labels", new Unmarshaller<Tag>() {
            @Override
            public Tag unmarshal(JSONArray value, int index) {
                Tag tag = new Tag();
                tag.fromJsonObject(value.getJSONObject(index));
                return tag;
            }
        });

        severityConfigurations = JsonUtils.readList(value, "severityConfigurations", new Unmarshaller<SeverityConfiguration>() {
            @Override
            public SeverityConfiguration unmarshal(JSONArray value, int index) {
                SeverityConfiguration severityConfiguration = new SeverityConfiguration();
                severityConfiguration.fromJsonObject(value.getJSONObject(index));
                return severityConfiguration;
            }
        });

        joinConfigurations = JsonUtils.readList(value, "joinConfigurations", new Unmarshaller<JoinConfiguration>() {
            @Override
            public JoinConfiguration unmarshal(JSONArray value, int index) {
                JoinConfiguration joinConfiguration = new JoinConfiguration();
                joinConfiguration.fromJsonObject(value.getJSONObject(index));
                return joinConfiguration;
            }
        });
        groupConfiguration = new GroupConfiguration();
        if (value.containsKey("groupConfiguration") && value.getJSONObject("groupConfiguration") != null) {
            groupConfiguration.fromJsonObject(value.getJSONObject("groupConfiguration"));
        }

        policyConfiguration = new PolicyConfiguration();
        if (value.containsKey("policyConfiguration") && value.getJSONObject("policyConfiguration") != null) {
            policyConfiguration.fromJsonObject(value.getJSONObject("policyConfiguration"));
        }

        if (value.containsKey("tags")) {
            tags = JsonUtils.readStringList(value, "tags");
        }

        if (value.containsKey("sinkEventStore") && value.getJSONObject("sinkEventStore") != null) {
            sinkEventStore = new SinkEventStoreConfiguration();
            sinkEventStore.fromJsonObject(value.getJSONObject("sinkEventStore"));
        }

        if (value.containsKey("sinkCms") && value.getJSONObject("sinkCms") != null) {
            sinkCms = new SinkCmsConfiguration();
            sinkCms.fromJsonObject(value.getJSONObject("sinkCms"));
        }

        if (value.containsKey("sinkAlerthub") && value.getJSONObject("sinkAlerthub") != null) {
            sinkAlerthub = new SinkAlerthubConfiguration();
            sinkAlerthub.fromJsonObject(value.getJSONObject("sinkAlerthub"));
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public SinkEventStoreConfiguration getSinkEventStore() {
        return sinkEventStore;
    }

    public void setSinkEventStore(SinkEventStoreConfiguration sinkEventStore) {
        this.sinkEventStore = sinkEventStore;
    }

    public SinkCmsConfiguration getSinkCms() {
        return sinkCms;
    }

    public void setSinkCms(SinkCmsConfiguration sinkCms) {
        this.sinkCms = sinkCms;
    }

    public SinkAlerthubConfiguration getSinkAlerthub() {
        return sinkAlerthub;
    }

    public void setSinkAlerthub(SinkAlerthubConfiguration sinkAlerthub) {
        this.sinkAlerthub = sinkAlerthub;
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

    @JsonEnumAdapter
    public enum JoinType {
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

        @JsonEnumCreator
        public static JoinType fromString(String value) {
            for (JoinType type : JoinType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }

        @JsonEnumValue
        @Override
        public String toString() {
            return value;
        }

    }

    @JsonEnumAdapter
    public enum GroupType {
        NO_GROUP("no_group"),
        LABELS_AUTO("labels_auto"),
        CUSTOM("custom");

        private final String value;

        GroupType(String value) {
            this.value = value;
        }

        @JsonEnumCreator
        public static GroupType fromString(String value) {
            for (GroupType type : GroupType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }

        @JsonEnumValue
        @Override
        public String toString() {
            return value;
        }

    }

    @JsonEnumAdapter
    public enum StoreType {
        LOG("log"),
        METRIC("metric"),
        META("meta");

        private final String value;

        StoreType(String value) {
            this.value = value;
        }

        @JsonEnumCreator
        public static StoreType fromString(String value) {
            for (StoreType type : StoreType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }

        @JsonEnumValue
        @Override
        public String toString() {
            return value;
        }

    }

    public static class TemplateConfiguration implements JsonSerializable, JsonDeserializable {
        private String id;
        private String type;
        private String version;
        private String lang;
        private Map<String, String> tokens;
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

        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            setId(JsonUtils.readOptionalString(value, "id"));
            setType(JsonUtils.readOptionalString(value, "type"));
            setLang(JsonUtils.readOptionalString(value, "lang"));
            setVersion(JsonUtils.readOptionalString(value, "version"));
            setTokens(JsonUtils.readOptionalMap(value, "tokens"));
            setAnnotations(JsonUtils.readOptionalMap(value, "annotations"));
        }
    }

    public static class ConditionConfiguration implements JsonSerializable, JsonDeserializable {
        private String condition;
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

        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            if (value != null) {
                setCondition(JsonUtils.readOptionalString(value, "condition"));
                setCountCondition(JsonUtils.readOptionalString(value, "countCondition"));
            }
        }
    }

    public static class JoinConfiguration implements JsonSerializable, JsonDeserializable {
        private String type;
        private String condition;
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

        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            setType(value.getString("type"));
            setCondition(value.getString("condition"));
            setUi(value.getString("ui"));
        }
    }

    public static class Tag implements JsonSerializable, JsonDeserializable {
        private String key;
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


        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            if (value != null) {
                setKey(value.getString("key"));
                setValue(value.getString("value"));
            }
        }
    }

    public static class SeverityConfiguration implements JsonSerializable, JsonDeserializable {
        private int severity;
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


        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            if (value.containsKey("severity")) {
                setSeverity(Severity.valueOf(value.getInteger("severity")));
            }
            evalCondition = new ConditionConfiguration();
            if (value.containsKey("evalCondition") && value.getJSONObject("evalCondition") != null) {
                evalCondition.fromJsonObject(value.getJSONObject("evalCondition"));
            }
        }
    }

    public static class GroupConfiguration implements JsonSerializable, JsonDeserializable {
        private String type;
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

        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            setType(value.getString("type"));
            setFields(JsonUtils.readStringList(value, "fields"));
        }
    }

    public static class PolicyConfiguration implements JsonSerializable, JsonDeserializable {
        private String actionPolicyId;
        private String alertPolicyId;
        private boolean useDefault;
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

        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            setUseDefault(JsonUtils.readBool(value, "useDefault", false));
            setRepeatInterval(JsonUtils.readOptionalString(value, "repeatInterval"));
            setActionPolicyId(JsonUtils.readOptionalString(value, "actionPolicyId"));
            setAlertPolicyId(JsonUtils.readOptionalString(value, "alertPolicyId"));
        }
    }

    public static class SinkEventStoreConfiguration implements JsonSerializable, JsonDeserializable {
        private boolean enabled;
        private String endpoint;
        private String project;
        private String eventStore;
        private String roleArn;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public String getEventStore() {
            return eventStore;
        }

        public void setEventStore(String eventStore) {
            this.eventStore = eventStore;
        }

        public String getRoleArn() {
            return roleArn;
        }

        public void setRoleArn(String roleArn) {
            this.roleArn = roleArn;
        }

        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            setEnabled(JsonUtils.readBool(value, "enabled", false));
            setEndpoint(JsonUtils.readOptionalString(value, "endpoint"));
            setProject(JsonUtils.readOptionalString(value, "project"));
            setEventStore(JsonUtils.readOptionalString(value, "eventStore"));
            setRoleArn(JsonUtils.readOptionalString(value, "roleArn"));
        }
    }

    public static class SinkCmsConfiguration implements JsonSerializable, JsonDeserializable {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            setEnabled(JsonUtils.readBool(value, "enabled", false));
        }
    }

    public static class SinkAlerthubConfiguration implements JsonSerializable, JsonDeserializable {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        @InternalApi
        public JSONObject toJsonObject() {
            return JsonCodec.toJsonObject(this);
        }

        @Override
        @InternalApi
        public void fromJsonObject(JSONObject value) {
            setEnabled(JsonUtils.readBool(value, "enabled", false));
        }
    }
}
