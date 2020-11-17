package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.aliyun.openservices.log.exception.LogException;

import java.util.ArrayList;
import java.util.List;

/**
 * Index config for a logstore,  it contains the index data life cycle(ttl),  index for keys and for log line
 * @author log-service-dev
 *
 */
public class Index {
	private int ttl = -1;
	private IndexKeys keys = new IndexKeys();
	private IndexLine line = new IndexLine();
	
	private boolean keysSet = false;
	private boolean lineSet = false;
	private boolean logReduceEnable = false;

	private int maxTextLen = 0;
	private List<String> logReduceWhiteList = new ArrayList<String>();
	private List<String> logReduceBlackList = new ArrayList<String>();

	public Index() {
	}
	/**
	 * create Index 
	 * @param ttl the index data file cycle in day, currently only support 7, 30, 90
	 * @param keys the keys index config
	 * @param line the log line index config
	 */
	public Index(int ttl, IndexKeys keys, IndexLine line) {
		this.ttl = ttl;
		SetKeys(keys);
		SetLine(line);
	}
	
	/**
	 * Create index from another index 
	 * @param other another index config
	 */
	public Index(Index other) {
		this.ttl = other.GetTtl();
		this.logReduceEnable = other.isLogReduceEnable();
		if (other.isKeysSet()) {
			SetKeys(other.GetKeys());
		}
		if (other.isLineSet()) {
			SetLine(other.GetLine());
		}
		this.maxTextLen = other.getMaxTextLen();
		setLogReduceWhiteList(other.getLogReduceWhiteList());
		setLogReduceBlackList(other.getLogReduceBlackList());
	}
	
	public boolean isLogReduceEnable() {
		return logReduceEnable;
	}
	public void setLogReduceEnable(boolean logReduce) {
		this.logReduceEnable = logReduce;
	}
	
	/**
	 * @return the keysSet
	 */
	public boolean isKeysSet() {
		return keysSet;
	}

	/**
	 * @return the lineSet
	 */
	public boolean isLineSet() {
		return lineSet;
	}

	
	
	/**
	 * @return the ttl
	 */
	public int GetTtl() {
		return ttl;
	}

	/**
	 * @param ttl the ttl to set
	 */
	public void SetTtl(int ttl) {
		this.ttl = ttl;
	}

	/**
	 * @return the keys
	 */
	public IndexKeys GetKeys() {
		return keys;
	}

	/**
	 * @return the line
	 */
	public IndexLine GetLine() {
		return line;
	}


	/**
	 * @param keys the keys to set
	 */
	public void SetKeys(IndexKeys keys) {
		keysSet = true;
		this.keys = new IndexKeys(keys);
	}

	/**
	 * @param line the line to set
	 */
	public void SetLine(IndexLine line) {
		lineSet = true;
		this.line = new IndexLine(line);
	}

	public int getMaxTextLen() {
		return maxTextLen;
	}

	public void setMaxTextLen(int maxTextLen) {
		this.maxTextLen = maxTextLen;
	}

	public List<String> getLogReduceWhiteList() {
		return logReduceWhiteList;
	}

	public void setLogReduceWhiteList(List<String> logReduceWhiteList) {
		this.logReduceWhiteList = logReduceWhiteList;
	}

	public List<String> getLogReduceBlackList() {
		return logReduceBlackList;
	}

	public void setLogReduceBlackList(List<String> logReduceBlackList) {
		this.logReduceBlackList = logReduceBlackList;
	}

	/**
	 * Return index in json object
	 * @return index in json object
	 * @throws LogException if any error happened
	 */
	public JSONObject ToRequestJson() throws LogException  {
		JSONObject index = new JSONObject();
		
		index.put("ttl", ttl);
		index.put("log_reduce", logReduceEnable);
		
		if (lineSet) {
			JSONObject lineDict = line.ToJsonObject();
			index.put("line", lineDict);
		}
		
		if (keysSet) {
			JSONObject keysDict = keys.ToJsonObject();
			index.put("keys", keysDict);
		}

		if(maxTextLen>0){
			index.put("max_text_len", maxTextLen);
		}

		if (logReduceWhiteList.size() > 0) {
			JSONArray logReduceWhiteListDict = new JSONArray();
			logReduceWhiteListDict.addAll(logReduceWhiteList);
			index.put("log_reduce_white_list", logReduceWhiteListDict);
		}

		if (logReduceBlackList.size() > 0) {
			JSONArray logReduceBlackListDict = new JSONArray();
			logReduceBlackListDict.addAll(logReduceBlackList);
			index.put("log_reduce_black_list", logReduceBlackListDict);
		}

		return index;
	}
	
	public String ToRequestString() throws LogException {	
		return ToRequestJson().toString();
	}
	
	public JSONObject ToJsonObject() throws LogException {
		JSONObject index = ToRequestJson();
		return index;
	}
	 
	public String ToJsonString() throws LogException {	
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			ttl = dict.getIntValue("ttl");
			
			if (dict.containsKey("line")) {
				JSONObject lineDict = dict.getJSONObject("line");
				line.FromJsonObject(lineDict);
				lineSet = true;
			}
			
			if (dict.containsKey("keys")) {
				JSONObject keysDict = dict.getJSONObject("keys");
				keys.FromJsonObject(keysDict);
				keysSet = true;
			}
			
			if (dict.containsKey("log_reduce")) {
				logReduceEnable = dict.getBooleanValue("log_reduce");
			}

			if (dict.containsKey("max_text_len")) {
				maxTextLen = dict.getIntValue("max_text_len");
			}

            if (dict.containsKey("log_reduce_white_list")) {
                JSONArray logReduceWhiteListDict = dict.getJSONArray("log_reduce_white_list");
                logReduceWhiteList = new ArrayList<String>();
                for (int i = 0;i < logReduceWhiteListDict.size();i++) {
                    logReduceWhiteList.add(logReduceWhiteListDict.getString(i));
                }
            }

            if (dict.containsKey("log_reduce_black_list")) {
                JSONArray logReduceBlackListDict = dict.getJSONArray("log_reduce_black_list");
                logReduceBlackList = new ArrayList<String>();
                for (int i = 0;i < logReduceBlackListDict.size();i++) {
                    logReduceBlackList.add(logReduceBlackListDict.getString(i));
                }
            }

		} catch (JSONException e) {
			throw new LogException("FailToGenerateIndex", e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String indexString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(indexString, Feature.DisableSpecialKeyDetect);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateIndex", e.getMessage(), e, "");
		}
	}
}
