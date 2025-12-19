package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.util.Args;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public class MetricStore implements Serializable {

    private static final long serialVersionUID = 7408057471332043832L;
    private String name = "";
    private int ttl = -1;
    private int shardCount = -1;
    private boolean appendMeta = false;
    private boolean autoSplit = false;
    private int maxSplitShard = -1;
    private int createTime = -1;
    private int lastModifyTime = -1;
    private int hotTTL = -1;
    private String mode = null;
    private int infrequentAccessTTL = -1;

    public MetricStore() {
    }

    public MetricStore(String name, int ttl, int shardCount) {
        super();
        this.name = name;
        this.ttl = ttl;
        this.shardCount = shardCount;
    }

    public MetricStore(MetricStore metricStore) {
        super();
        Args.notNull(metricStore, "MetricStore must not be null");
        this.name = metricStore.getName();
        this.ttl = metricStore.getTtl();
        this.shardCount = metricStore.getShardCount();
        this.createTime = metricStore.getCreateTime();
        this.lastModifyTime = metricStore.getLastModifyTime();
        this.appendMeta = metricStore.isAppendMeta();
        this.autoSplit = metricStore.isAutoSplit();
        this.maxSplitShard = metricStore.getMaxSplitShard();
        this.hotTTL = metricStore.hotTTL;
        this.mode = metricStore.mode;
        this.infrequentAccessTTL = metricStore.infrequentAccessTTL;
    }

    // from logstore
    public MetricStore(LogStore logStore) {
        super();
        Args.notNull(logStore, "LogStore must not be null");
        this.name = logStore.GetLogStoreName();
        this.ttl = logStore.GetTtl();
        this.shardCount = logStore.GetShardCount();
        this.createTime = logStore.GetCreateTime();
        this.lastModifyTime = logStore.GetLastModifyTime();
        this.appendMeta = logStore.isAppendMeta();
        this.autoSplit = logStore.ismAutoSplit();
        this.maxSplitShard = logStore.getmMaxSplitShard();
        this.hotTTL = logStore.getHotTTL();
        this.mode = logStore.getMode();
        this.infrequentAccessTTL = logStore.getInfrequentAccessTTL();
    }

    public int getMaxSplitShard() {
        return maxSplitShard;
    }

    public void setMaxSplitShard(int mMaxSplitShard) {
        this.maxSplitShard = mMaxSplitShard;
    }

    public boolean isAutoSplit() {
        return autoSplit;
    }

    public void setAutoSplit(boolean mAutoSplit) {
        this.autoSplit = mAutoSplit;
    }

    public boolean isAppendMeta() {
        return appendMeta;
    }

    public void setAppendMeta(boolean appendMeta) {
        this.appendMeta = appendMeta;
    }

    /**
     * @return the metricStoreName
     */
    public String getName() {
        return name;
    }

    /**
     * @return the ttl
     */
    public int getTtl() {
        return ttl;
    }

    /**
     * @return the shardCount
     */
    public int getShardCount() {
        return shardCount;
    }

    /**
     * @return the createTime
     */
    public int getCreateTime() {
        return createTime;
    }

    /**
     * @return the lastModifyTime
     */
    public int getLastModifyTime() {
        return lastModifyTime;
    }

    /**
     * @param name the metricStoreName to Set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param ttl the ttl to Set
     */
    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    /**
     * @param shardCount the shardCount to Set
     */
    public void setShardCount(int shardCount) {
        this.shardCount = shardCount;
    }

    public int getHotTTL() {
        return hotTTL;
    }

    public void setHotTTL(int hotTTL) {
        this.hotTTL = hotTTL;
    }

    public int getInfrequentAccessTTL() {
        return infrequentAccessTTL;
    }

    public void setInfrequentAccessTTL(int infrequentAccessTTL) {
        this.infrequentAccessTTL = infrequentAccessTTL;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public JSONObject ToRequestJson() {
        JSONObject metricStoreDict = new JSONObject();
        metricStoreDict.put("name", getName());
        metricStoreDict.put("shardCount", getShardCount());
        metricStoreDict.put("autoSplit", isAutoSplit());
        metricStoreDict.put("maxSplitShard", getMaxSplitShard());
        metricStoreDict.put("appendMeta", isAppendMeta());

        if (ttl > 0) {
            metricStoreDict.put("ttl", getTtl());
        }
        if (hotTTL > 0) {
            metricStoreDict.put("hot_ttl", hotTTL);
        }
        if (infrequentAccessTTL >= 0) {
            metricStoreDict.put("infrequentAccessTTL", infrequentAccessTTL);
        }
        if (mode != null) {
            metricStoreDict.put("mode", mode);
        }
        return metricStoreDict;
    }

    public String ToRequestString() {
        return ToRequestJson().toString();
    }

    public JSONObject ToJsonObject() {
        JSONObject dict = ToRequestJson();
        dict.put("createTime", getCreateTime());
        dict.put("lastModifyTime", getLastModifyTime());
        return dict;
    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject dict) throws LogException {
        try {
            setName(dict.getString("name"));
            setTtl(dict.getIntValue("ttl"));
            setShardCount(dict.getIntValue("shardCount"));
            if (dict.containsKey("createTime")) {
                createTime = dict.getIntValue("createTime");
            }

            if (dict.containsKey("lastModifyTime")) {
                lastModifyTime = dict.getIntValue("lastModifyTime");
            }

            if (dict.containsKey("autoSplit")) {
                autoSplit = dict.getBoolean("autoSplit");
            }
            if (dict.containsKey("maxSplitShard")) {
                maxSplitShard = dict.getIntValue("maxSplitShard");
            }
            appendMeta = dict.containsKey("appendMeta") && dict.getBoolean("appendMeta");
            if (dict.containsKey("hot_ttl")) {
                this.hotTTL = dict.getInteger("hot_ttl");
            }
            if (dict.containsKey("infrequentAccessTTL")) {
                this.infrequentAccessTTL = dict.getInteger("infrequentAccessTTL");
            }
            if (dict.containsKey("mode")) {
                this.mode = dict.getString("mode");
            }
        } catch (JSONException e) {
            throw new LogException("FailToGenerateMetircStore", e.getMessage(), e, "");
        }
    }

    public void FromJsonString(String metricStoreString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(metricStoreString);
            FromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateMetricStore", e.getMessage(), e, "");
        }
    }
}
