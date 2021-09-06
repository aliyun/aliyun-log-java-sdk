package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AliyunTSDBSink extends DataSink {

    public static final String DEFAULT_TSDB_INSTANCE_PORT = "8242";

    public String getInstancePort() {
        return instancePort;
    }

    public void setInstancePort(String instancePort) {
        this.instancePort = instancePort;
    }

    public static class MappingField {
        private String name;
        private String type;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public MappingField(String name, String type, String value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
    };

    public static class KVEntry {
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

        public KVEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    };

    private String endpoint;

    private String vpcId;

    private String instanceId;

    private String instancePort = DEFAULT_TSDB_INSTANCE_PORT;

    private String dbType;

    private String dbVersion;

    private String metric;

    private ArrayList<MappingField> fieldMapping;

    private ArrayList<KVEntry> tagMapping;

    private KVEntry timestamp;

    private boolean strictMode;

    public AliyunTSDBSink() {
        super(DataSinkType.ALIYUN_TSDB);
        this.fieldMapping = new ArrayList<MappingField>();
        this.tagMapping = new ArrayList<KVEntry>();
        this.timestamp = new KVEntry("", "");
    }

    public AliyunTSDBSink(String endpoint, String vpcId, String instanceId, String instancePort, String dbType, String dbVersion, String metric, boolean strictMode) {
        super(DataSinkType.ALIYUN_TSDB);
        this.endpoint = endpoint;
        this.vpcId = vpcId;
        this.instanceId = instanceId;
        this.instancePort = instancePort;
        this.dbType = dbType;
        this.dbVersion = dbVersion;
        this.metric = metric;
        this.strictMode = strictMode;
        this.fieldMapping = new ArrayList<MappingField>();
        this.tagMapping = new ArrayList<KVEntry>();
        this.timestamp = new KVEntry("", "");
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public ArrayList<MappingField> getFieldMapping() {
        return fieldMapping;
    }

    public void setFieldMapping(ArrayList<MappingField> fieldMapping) {
        this.fieldMapping = fieldMapping;
    }

    public void addFieldMapping(MappingField field) {
        this.fieldMapping.add(field);
    }

    public ArrayList<KVEntry> getTagMapping() {
        return tagMapping;
    }

    public void setTagMapping(ArrayList<KVEntry> tagMapping) {
        this.tagMapping = tagMapping;
    }

    public void addTagMapping(KVEntry tag) {
        this.tagMapping.add(tag);
    }

    public KVEntry getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(KVEntry timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    @Override
    public void deserialize(JSONObject value) {
        endpoint = value.getString("endpoint");
        vpcId = value.getString("vpcId");
        instanceId = value.getString("instanceId");
        instancePort = JsonUtils.readOptionalString(value, "instancePort", DEFAULT_TSDB_INSTANCE_PORT);
        dbType = value.getString("dbType");
        dbVersion = value.getString("dbVersion");
        metric = value.getString("metric");
        JSONArray fm = value.getJSONArray("fieldMapping");
        this.fieldMapping.clear();
        for (int i = 0; i < fm.size(); ++i) {
            JSONObject f = fm.getJSONObject(i);
            this.fieldMapping.add(new MappingField(
                    f.getString("name"),
                    f.getString("type"),
                    f.getString("value")));
        }
        JSONArray tm = value.getJSONArray("tagMapping");
        this.tagMapping.clear();
        for (int i = 0; i < tm.size(); ++i) {
            JSONObject t = tm.getJSONObject(i);
            this.tagMapping.add(new KVEntry(
                    t.getString("key"),
                    t.getString("value")));
        }
        JSONObject ts = value.getJSONObject("timestamp");
        this.timestamp = new KVEntry(ts.getString("key"), ts.getString("value"));
        strictMode = value.getBoolean("strictMode");
    }
}
