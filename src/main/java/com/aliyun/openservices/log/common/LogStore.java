package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.util.Args;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public class LogStore implements Serializable {

    private static final long serialVersionUID = 7408057477332043832L;
    private String logStoreName = "";
    private int ttl = -1;
    private int shardCount = -1;
    private boolean enableWebTracking = false;
    private boolean appendMeta = false;
    private boolean mAutoSplit = false;
    private int mMaxSplitShard = -1;
    private int createTime = -1;
    private int lastModifyTime = -1;
    private long preserveStorage = -1;
    private long usedStorage = 0;
    private String productType = "";
    private int archiveSeconds = 0;
    private String telemetryType = "";
    private EncryptConf encryptConf = null;

    public int getArchiveSeconds() {
        return archiveSeconds;
    }

    public void setArchiveSeconds(int archiveSeconds) {
        this.archiveSeconds = archiveSeconds;
    }

    public String getTelemetryType() {
        return telemetryType;
    }

    public void setTelemetryType(String telemetryType) {
        this.telemetryType = telemetryType;
    }

    public String getProductType() {
        return productType;
    }

    public LogStore() {
        super();
    }

    public LogStore(String logStoreName, int ttl, int shardCount) {
        super();
        this.logStoreName = logStoreName;
        this.ttl = ttl;
        this.shardCount = shardCount;
    }

    public LogStore(String logStoreName, int ttl, int shardCount, boolean enableWebTracking) {
        super();
        this.logStoreName = logStoreName;
        this.ttl = ttl;
        this.shardCount = shardCount;
        this.enableWebTracking = enableWebTracking;
    }

    public LogStore(LogStore logStore) {
        super();
        Args.notNull(logStore, "LogStore must not be null");
        this.logStoreName = logStore.GetLogStoreName();
        this.ttl = logStore.GetTtl();
        this.shardCount = logStore.GetShardCount();
        this.createTime = logStore.GetCreateTime();
        this.lastModifyTime = logStore.GetLastModifyTime();
        this.enableWebTracking = logStore.isEnableWebTracking();
        this.appendMeta = logStore.isAppendMeta();
        this.mAutoSplit = logStore.ismAutoSplit();
        this.mMaxSplitShard = logStore.getmMaxSplitShard();
        this.preserveStorage = logStore.preserveStorage;
        this.usedStorage = logStore.usedStorage;
        this.productType = logStore.getProductType();
        this.archiveSeconds = logStore.getArchiveSeconds();
        this.telemetryType = logStore.getTelemetryType();
        this.encryptConf = logStore.encryptConf;
    }

    public long getPreserveStorage() {
		return preserveStorage;
	}

	public void setPreserveStorage(long preserveStorage) {
		this.preserveStorage = preserveStorage;
	}

	public long getUsedStorage() {
		return usedStorage;
	}

	public void setUsedStorage(long usedStorage) {
		this.usedStorage = usedStorage;
	}

	public int getmMaxSplitShard() {
        return mMaxSplitShard;
    }

    public void setmMaxSplitShard(int mMaxSplitShard) {
        this.mMaxSplitShard = mMaxSplitShard;
    }

    public boolean ismAutoSplit() {
        return mAutoSplit;
    }

    public void setmAutoSplit(boolean mAutoSplit) {
        this.mAutoSplit = mAutoSplit;
    }

    public boolean isEnableWebTracking() {
        return enableWebTracking;
    }

    public void setEnableWebTracking(boolean enableWebTracking) {
        this.enableWebTracking = enableWebTracking;
    }

    public boolean isAppendMeta() {
        return appendMeta;
    }

    public void setAppendMeta(boolean appendMeta) {
        this.appendMeta = appendMeta;
    }

    /**
     * @return the logStoreName
     */
    public String GetLogStoreName() {
        return logStoreName;
    }

    /**
     * @return the ttl
     */
    public int GetTtl() {
        return ttl;
    }

    /**
     * @return the shardCount
     */
    public int GetShardCount() {
        return shardCount;
    }

    /**
     * @return the createTime
     */
    public int GetCreateTime() {
        return createTime;
    }

    /**
     * @return the lastModifyTime
     */
    public int GetLastModifyTime() {
        return lastModifyTime;
    }

    /**
     * @param logStoreName the logStoreName to Set
     */
    public void SetLogStoreName(String logStoreName) {
        this.logStoreName = logStoreName;
    }

    /**
     * @param ttl the ttl to Set
     */
    public void SetTtl(int ttl) {
        this.ttl = ttl;
    }

    /**
     * @param shardCount the shardCount to Set
     */
    public void SetShardCount(int shardCount) {
        this.shardCount = shardCount;
    }
    
    public void SetEncryptConf(EncryptConf encrypt_conf)
    {
    	this.encryptConf = encrypt_conf;
    }
    
    public EncryptConf getEncryptConf()
    {
    	return this.encryptConf;
    }

	public JSONObject ToRequestJson() {
        JSONObject logStoreDict = new JSONObject();
        logStoreDict.put("logstoreName", GetLogStoreName());
        logStoreDict.put("ttl", GetTtl());
        logStoreDict.put("shardCount", GetShardCount());
        logStoreDict.put("enable_tracking", isEnableWebTracking());
        logStoreDict.put("autoSplit", ismAutoSplit());
        logStoreDict.put("maxSplitShard", getmMaxSplitShard());
        logStoreDict.put("appendMeta", isAppendMeta());
        JSONObject resourceQuota = new JSONObject();
        JSONObject storage = new JSONObject();
        storage.put("preserved", preserveStorage);
        resourceQuota.put("storage", storage);
        logStoreDict.put("resourceQuota", resourceQuota);
        logStoreDict.put("archiveSeconds", archiveSeconds);
        logStoreDict.put("telemetryType", telemetryType);
        if (this.encryptConf != null)
        {
        	logStoreDict.put("encrypt_conf", this.encryptConf.ToJsonObject());
        }
        return logStoreDict;
    }

    public String ToRequestString() {
        return ToRequestJson().toString();
    }

    public JSONObject ToJsonObject() {
        JSONObject logStoreDict = ToRequestJson();
        logStoreDict.put("createTime", GetCreateTime());
        logStoreDict.put("lastModifyTime", GetLastModifyTime());
        return logStoreDict;
    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject dict) throws LogException {
        try {
            SetLogStoreName(dict.getString("logstoreName"));
            SetTtl(dict.getIntValue("ttl"));
            SetShardCount(dict.getIntValue("shardCount"));
            if (dict.containsKey("enable_tracking")) {
                this.setEnableWebTracking(dict.getBoolean("enable_tracking"));
            }
            if (dict.containsKey("createTime")) {
                createTime = dict.getIntValue("createTime");
            }

            if (dict.containsKey("lastModifyTime")) {
                lastModifyTime = dict.getIntValue("lastModifyTime");
            }

            if (dict.containsKey("autoSplit")) {
                mAutoSplit = dict.getBoolean("autoSplit");
            }
            if (dict.containsKey("maxSplitShard")) {
                mMaxSplitShard = dict.getIntValue("maxSplitShard");
            }
            appendMeta = dict.containsKey("appendMeta") && dict.getBoolean("appendMeta");
            if (dict.containsKey("productType")) {
                productType = dict.getString("productType");
            }
            if (dict.containsKey("archiveSeconds")) {
                archiveSeconds = dict.getIntValue("archiveSeconds");
            }
            if (dict.containsKey("telemetryType")) {
                telemetryType = dict.getString("telemetryType");
            }
            // set resourceQuota
            if (dict.containsKey("resourceQuota")) {
            	JSONObject resourceQuotaJson = dict.getJSONObject("resourceQuota");
            	if (resourceQuotaJson.containsKey("storage")) {
            		JSONObject storageJson = resourceQuotaJson.getJSONObject("storage");
            		if (storageJson.containsKey("preserved")) {
            			preserveStorage = storageJson.getLong("preserved");
            		}
            		if (storageJson.containsKey("used")) {
            			usedStorage = storageJson.getLong("used");
            		}
            	}
            }
            if (dict.containsKey("encrypt_conf"))
            {
            	EncryptConf encypt_config = new EncryptConf();
            	encypt_config.FromJsonObject(dict.getJSONObject("encrypt_conf"));
            	this.encryptConf = encypt_config;
            }
        } catch (JSONException e) {
            throw new LogException("FailToGenerateLogStore", e.getMessage(), e, "");
        }
    }

    public void FromJsonString(String logStoreString) throws LogException {
        try {
        	System.out.println(logStoreString);

            JSONObject dict = JSONObject.parseObject(logStoreString);
            FromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateLogStore", e.getMessage(), e, "");
        }
    }
}
