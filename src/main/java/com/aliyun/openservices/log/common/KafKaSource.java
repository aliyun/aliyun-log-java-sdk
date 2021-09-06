package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.aliyun.openservices.log.util.JsonUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class KafKaSource extends DataSource {
    private String topics;
    private String bootstrapServers;
    private ValueType valueType;
    private KafkaPosition fromPosition;
    private long fromTimestamp;
    private long toTimestamp;
    private String timeField;
    private String timePattern;
    // Do not specify timeFormat if use ingest time.
    private String timeFormat;
    private String timeZone;
    private Map<String, String> additionalProps;

    public enum KafkaPosition implements JSONSerializable {
        GROUP_OFFSETS,
        EARLIEST,
        LATEST,
        TIMESTAMP;

        public static KafkaPosition fromString(String value) {
            for (KafkaPosition position : KafkaPosition.values()) {
                if (position.name().equalsIgnoreCase(value)) {
                    return position;
                }
            }
            return null;
        }

        @Override
        public void write(JSONSerializer jsonSerializer, Object o, Type type, int i) {
            jsonSerializer.write(name());
        }
    }

    public enum ValueType implements JSONSerializable {
        JSON,
        TEXT;

        public static ValueType fromString(String value) {
            for (ValueType type : ValueType.values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public void write(JSONSerializer jsonSerializer, Object o, Type type, int i) {
            jsonSerializer.write(name());
        }
    }

    public KafKaSource() {
        super(DataSourceType.KAFKA);
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public KafkaPosition getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(KafkaPosition fromPosition) {
        this.fromPosition = fromPosition;
    }

    public long getFromTimestamp() {
        return fromTimestamp;
    }

    public void setFromTimestamp(long fromTimestamp) {
        this.fromTimestamp = fromTimestamp;
    }

    public long getToTimestamp() {
        return toTimestamp;
    }

    public void setToTimestamp(long toTimestamp) {
        this.toTimestamp = toTimestamp;
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }

    public String getTimePattern() {
        return timePattern;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Map<String, String> getAdditionalProps() {
        return additionalProps;
    }

    public void setAdditionalProps(Map<String, String> additionalProps) {
        this.additionalProps = additionalProps;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        topics = JsonUtils.readOptionalString(jsonObject, "topics");
        bootstrapServers = JsonUtils.readOptionalString(jsonObject, "bootstrapServers");
        valueType = ValueType.fromString(jsonObject.getString("valueType"));
        fromPosition = KafkaPosition.fromString(jsonObject.getString("fromPosition"));
        fromTimestamp = jsonObject.getLongValue("fromTimestamp");
        toTimestamp = jsonObject.getLongValue("toTimestamp");
        timeField = jsonObject.getString("timeField");
        timePattern = jsonObject.getString("timePattern");
        timeFormat = jsonObject.getString("timeFormat");
        timeZone = jsonObject.getString("timeZone");
        additionalProps = JsonUtils.readOptionalMap(jsonObject, "additionalProps");
    }
}
