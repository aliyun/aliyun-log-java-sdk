package com.aliyun.openservices.log.common;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;


public abstract class CommonConfigInputDetail {
	protected boolean localStorage = true;
	protected ArrayList<String> filterRegex = new ArrayList<String>();
	protected ArrayList<String> filterKey = new ArrayList<String>();
	protected ArrayList<String> shardHashKey = new ArrayList<String>();
	protected boolean enableTag = false;
	protected boolean enableRawLog = false;
	protected int maxSendRate = -1;
	protected int sendRateExpire = 0;
	protected ArrayList<SensitiveKey> sensitiveKeys = new ArrayList<SensitiveKey>();
	protected String mergeType;
	protected long delayAlarmBytes = 0;
	protected boolean adjustTimezone = false;
	protected String logTimezone = "";
	protected int  priority = 0;
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isAdjustTimezone() {
		return adjustTimezone;
	}

	public void setAdjustTimezone(boolean adjustTimezone) {
		this.adjustTimezone = adjustTimezone;
	}

	public String getLogTimezone() {
		return logTimezone;
	}

	public void setLogTimezone(String logTimezone) {
		this.logTimezone = logTimezone;
	}

	public long getDelayAlarmBytes() {
		return delayAlarmBytes;
	}

	public void setDelayAlarmBytes(long delayAlarmBytes) {
		this.delayAlarmBytes = delayAlarmBytes;
	}

	public String getMergeType() {
		return mergeType;
	}

	public void setMergeType(String mergeType) {
		this.mergeType = mergeType;
	}

	public int getMaxSendRate() {
		return maxSendRate;
	}

	public void setMaxSendRate(int maxSendRate) {
		this.maxSendRate = maxSendRate;
	}

	public int getSendRateExpire() {
		return sendRateExpire;
	}

	public void setSendRateExpire(int sendRateExpire) {
		this.sendRateExpire = sendRateExpire;
	}

	public boolean GetEnableRawLog() {
		return enableRawLog;
	}

	public void SetEnableRawLog(boolean enableRawLog) {
		this.enableRawLog = enableRawLog;
	}

	public boolean GetEnableTag() {
		return enableTag;
	}

	public void SetEnableTag(boolean enableTag) {
		this.enableTag = enableTag;
	}
	
	public boolean GetLocalStorage() 
	{
		return this.localStorage;
	}
	
	public void SetLocalStorage(boolean localStorage)
	{
		this.localStorage = localStorage;
	}
	
	public void SetSensitiveKeys(List<SensitiveKey> sensitiveKeys) {
		this.sensitiveKeys = new ArrayList<SensitiveKey>(sensitiveKeys);
	}
	
	public ArrayList<SensitiveKey> GetSensitiveKeys() {
		return sensitiveKeys;
	}
	
	public void SetFilterKeyRegex(List<String> filterKey, List<String> filterRegex) {
		this.filterKey = new ArrayList<String>(filterKey);
		this.filterRegex = new ArrayList<String>(filterRegex);
	}
	
	public ArrayList<String> GetShardHashKey() {
		return shardHashKey;
	}
	
	public void SetShardHashKeyList(List<String> shardHashKey) {
		this.shardHashKey = new ArrayList<String>(shardHashKey);
	}
	
	public void SetShardHashKey(JSONArray shardHashKey) throws LogException {
		try {
			this.shardHashKey = new ArrayList<String>();
			for (int i = 0; i < shardHashKey.size(); i++)
				this.shardHashKey.add(shardHashKey.getString(i));
		} catch (JSONException e) {
			throw new LogException("FailToSetShardHashKey", e.getMessage(), e, "");
		}
	}
	
	public ArrayList<String> GetFilterRegex() {
		return filterRegex;
	}
	
	public void SetFilterRegex(JSONArray filterRegex) throws LogException {
		try {
			this.filterRegex = new ArrayList<String>();
			for (int i = 0; i < filterRegex.size(); i++) {
				this.filterRegex.add(filterRegex.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("FailToSetFilterRegex", e.getMessage(), e, "");
		}
	}
	
	public ArrayList<String> GetFilterKey() {
		return filterKey;
	}
	
	public void SetFilterKey(JSONArray filterKey) throws LogException {
		try {
			this.filterKey = new ArrayList<String>();
			for (int i = 0; i < filterKey.size(); i++) {
				this.filterKey.add(filterKey.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("FailToSetFilterKey", e.getMessage(), e, "");
		}
	}
	
	public abstract JSONObject ToJsonObject();
	public abstract void FromJsonObject(JSONObject inputDetail) throws LogException;
	
	protected void CommonConfigToJsonObject(JSONObject jsonObj) {
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_LOCALSTORAGE, localStorage);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ENABLETAG, enableTag);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ENABLERAWLOG, enableRawLog);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_MAXSENDRATE, maxSendRate);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_SENDRATEEXPIRE, sendRateExpire);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_MERGETYPE, mergeType);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_DELAYALARMBYTES, delayAlarmBytes);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ADJUSTTIMEZONE, adjustTimezone);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_LOGTIMEZONE, logTimezone);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_PRIORITY, priority);
		JSONArray sensitiveKeysArray = new JSONArray();
		for (SensitiveKey sensitiveKey : sensitiveKeys) {
			sensitiveKeysArray.add(sensitiveKey.ToJsonObject());
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS, sensitiveKeysArray);
		JSONArray filterRegexArray = new JSONArray();
		for (String fr : filterRegex) {
			filterRegexArray.add(fr);
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_FILTERREGEX, filterRegexArray);

		JSONArray filterKeyArray = new JSONArray();
		for (String fk : filterKey) {
			filterKeyArray.add(fk);
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_FILTERKEY, filterKeyArray);
		
		JSONArray shardHashKeyArray = new JSONArray();
		for (String shk : shardHashKey) {
			shardHashKeyArray.add(shk);
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_SHARDHASHKEY, shardHashKeyArray);
	}
	
	protected void CommonConfigFromJsonObject(JSONObject inputDetail) throws LogException {
		try {
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ADJUSTTIMEZONE)) {
				this.adjustTimezone = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_ADJUSTTIMEZONE);
			} else {
				this.adjustTimezone = false;
			}
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_LOGTIMEZONE)) {
				this.logTimezone = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_LOGTIMEZONE);
			} else {
				this.logTimezone = "";
			}	
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_PRIORITY))
				this.priority = inputDetail.getIntValue(Consts.CONST_CONFIG_INPUTDETAIL_PRIORITY);
			else
				this.priority = 0;
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_MERGETYPE))
				this.mergeType = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_MERGETYPE);
			else
				this.mergeType = "topic";
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_SENDRATEEXPIRE))
				this.sendRateExpire = inputDetail.getIntValue(Consts.CONST_CONFIG_INPUTDETAIL_SENDRATEEXPIRE);
			else
				this.sendRateExpire = 0;
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_MAXSENDRATE))
				this.maxSendRate = inputDetail.getIntValue(Consts.CONST_CONFIG_INPUTDETAIL_MAXSENDRATE);
			else
				this.maxSendRate = -1;
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_LOCALSTORAGE))
				this.localStorage = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_LOCALSTORAGE);
			else
				this.localStorage = true;
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ENABLETAG))
				this.enableTag = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_ENABLETAG);
			else
				this.enableTag = false;
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_ENABLERAWLOG))
				this.enableRawLog = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_ENABLERAWLOG);
			else
				this.enableRawLog = false;
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_DELAYALARMBYTES))
				this.delayAlarmBytes = inputDetail.getLong(Consts.CONST_CONFIG_INPUTDETAIL_DELAYALARMBYTES);
			else 
				this.delayAlarmBytes = 0;
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_FILTERREGEX))
				SetFilterRegex(inputDetail.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_FILTERREGEX));
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_FILTERKEY))
				SetFilterKey(inputDetail.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_FILTERKEY));
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_SHARDHASHKEY))
				SetShardHashKey(inputDetail.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_SHARDHASHKEY));
			if (inputDetail.containsKey(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS)) {
				JSONArray sensitiveKeysArray = inputDetail.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS);
				for (int index = 0; index < sensitiveKeysArray.size(); index++) {
					SensitiveKey sensitiveKey = new SensitiveKey();
					sensitiveKey.FromJsonString(sensitiveKeysArray.getJSONObject(index).toString());
					sensitiveKeys.add(sensitiveKey);
				}
			}
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(), e, "");
		}
	}
	
	public static CommonConfigInputDetail FromJsonStringS(final String inputType, final String jsonString) throws LogException 
	{
		try {
			JSONObject inputDetail = JSONObject.parseObject(jsonString);
			return FromJsonObjectS(inputType, inputDetail);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(),
					e, "");
		}
	}

	public static CommonConfigInputDetail FromJsonObjectS(final String inputType, JSONObject inputDetail)
			throws LogException {
		try {
			if (inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_SYSLOG)
					|| inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_STREAMLOG)) {
				StreamLogConfigInputDetail res = new StreamLogConfigInputDetail();
				res.FromJsonObject(inputDetail);
				return res;
			} else if (inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_PLUGIN)) {
				PluginLogConfigInputDetail res = new PluginLogConfigInputDetail();
				res.FromJsonObject(inputDetail);
				return res;
			} else if (inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_FILE)) {
				if (inputDetail.containsKey(Consts.CONST_CONFIG_LOGTYPE)) {
					if (inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE)
							.compareTo(Consts.CONST_CONFIG_LOGTYPE_JSON) == 0) {
						JsonConfigInputDetail res = new JsonConfigInputDetail();
						res.FromJsonObject(inputDetail);
						return res;
					} else if (inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE)
							.compareTo(Consts.CONST_CONFIG_LOGTYPE_DELIMITER) == 0) {
						DelimiterConfigInputDetail res = new DelimiterConfigInputDetail();
						res.FromJsonObject(inputDetail);
						return res;
					} else if (inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE)
							.compareTo(Consts.CONST_CONFIG_LOGTYPE_APSARA) == 0) {
						ApsaraLogConfigInputDetail res = new ApsaraLogConfigInputDetail();
						res.FromJsonObject(inputDetail);
						return res;
					} else if (inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE)
							.compareTo(Consts.CONST_CONFIG_LOGTYPE_COMMON) == 0) {
						ConfigInputDetail res = new ConfigInputDetail();
						res.FromJsonObject(inputDetail);
						return res;
					} else {
						throw new LogException("FailToGenerateInputDetail", "invlaid logType",
								inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE));
					}
				} else {
					throw new LogException("FailToGenerateInputDetail", "logType field does not exist in input detail",
							"");
				}
			} else {
				throw new LogException("FailToGenerateInputDetail", "invalid inputType", inputType);
			}
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(), e, "");
		}
	}
}
