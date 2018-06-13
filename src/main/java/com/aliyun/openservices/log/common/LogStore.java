package com.aliyun.openservices.log.common;

import java.io.Serializable;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.aliyun.openservices.log.exception.LogException;

public class LogStore implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7408057477332043832L;
	private String logStoreName = "";
	private int ttl = -1;
	private int shardCount = -1;
	private boolean enableWebTracking = false;
	private boolean mAutoSplit = false;
	private int mMaxSplitShard = -1;
	private int createTime = -1;
	private int lastModifyTime = -1;
	
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
		this.logStoreName = logStore.GetLogStoreName();
		this.ttl = logStore.GetTtl();
		this.shardCount = logStore.GetShardCount();
		this.createTime = logStore.GetCreateTime();
		this.lastModifyTime = logStore.GetLastModifyTime();
		this.enableWebTracking = logStore.enableWebTracking;
		this.mAutoSplit = logStore.mAutoSplit;
		this.mMaxSplitShard = logStore.mMaxSplitShard;
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
	
	public JSONObject ToRequestJson(){
		JSONObject logStoreDict = new JSONObject();
		logStoreDict.put("logstoreName", GetLogStoreName());
		logStoreDict.put("ttl", GetTtl());
		logStoreDict.put("shardCount", GetShardCount());
		logStoreDict.put("enable_tracking", isEnableWebTracking());
		logStoreDict.put("autoSplit", ismAutoSplit());
		logStoreDict.put("maxSplitShard", getmMaxSplitShard());
		return logStoreDict;
	}
	
	public String ToRequestString() {	
		return ToRequestJson().toString();
	}
	
	public JSONObject ToJsonObject()  {
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
			SetTtl(dict.getInt("ttl"));
			SetShardCount(dict.getInt("shardCount"));
			if(dict.containsKey("enable_tracking"))
			{
				this.setEnableWebTracking(dict.getBoolean("enable_tracking"));
			}
			if (dict.containsKey("createTime")) {
				createTime = dict.getInt("createTime");
			}
			
			if (dict.containsKey("lastModifyTime")) {
				lastModifyTime = dict.getInt("lastModifyTime");
			}

			if (dict.containsKey("autoSplit")) {
				mAutoSplit = dict.getBoolean("autoSplit");
			}
			if (dict.containsKey("maxSplitShard")) {
				mMaxSplitShard = dict.getInt("maxSplitShard");
			}
			
		} catch (JSONException e) {
			throw new LogException("FailToGenerateLogStore", e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String logStoreString) throws LogException {
		try {
			JSONObject dict = JSONObject.fromObject(logStoreString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateLogStore", e.getMessage(), e, "");
		}
	}
}
