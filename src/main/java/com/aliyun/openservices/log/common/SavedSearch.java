package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class SavedSearch implements Serializable {

	private static final long serialVersionUID = 8415874596775859485L;
	protected String savedSearchName = "";
	protected String searchQuery = "";
	protected String logstore = "";
	protected String topic = "";
	protected String displayName = "";

	public String getDisplayName() { return displayName; }
	public void setDisplayName(String displayName) { this.displayName = displayName; }
	public String getSavedSearchName() {
		return savedSearchName;
	}
	public void setSavedSearchName(String savedSearchName) {
		this.savedSearchName = savedSearchName;
	}
	public String getSearchQuery() {
		return searchQuery;
	}
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	public String getLogstore() {
		return logstore;
	}
	public void setLogstore(String logstore) {
		this.logstore = logstore;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public SavedSearch() {}
	public SavedSearch(SavedSearch savedSearch) {
		super();
		this.savedSearchName = savedSearch.savedSearchName;
		this.searchQuery = savedSearch.searchQuery;
		this.logstore = savedSearch.logstore;
		this.topic = savedSearch.topic;
		this.displayName = savedSearch.displayName;
	}
	public JSONObject ToJsonObject() {
		JSONObject savedSearchJson = new JSONObject();
		savedSearchJson.put(Consts.CONST_SAVEDSEARCH_NAME, getSavedSearchName());
		savedSearchJson.put(Consts.CONST_SAVEDSEARCH_QUERY, getSearchQuery());
		savedSearchJson.put(Consts.CONST_SAVEDSEARCH_LOGSTORE, getLogstore());
		savedSearchJson.put(Consts.CONST_SAVEDSEARCH_TOPIC, getTopic());
		savedSearchJson.put(Consts.CONST_SAVEDSEARCH_DISPLAYNAME, getDisplayName());
		return savedSearchJson;
	}
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {		
			setSavedSearchName(dict.getString(Consts.CONST_SAVEDSEARCH_NAME));
			setSearchQuery(dict.getString(Consts.CONST_SAVEDSEARCH_QUERY));
			setLogstore(dict.getString(Consts.CONST_SAVEDSEARCH_LOGSTORE));
			setTopic(dict.getString(Consts.CONST_SAVEDSEARCH_TOPIC));
			setDisplayName(dict.getString(Consts.CONST_SAVEDSEARCH_DISPLAYNAME));
		} catch (JSONException e) {
			throw new LogException("FailToGenerateSavedSearch",  e.getMessage(), e, "");
		}
	}
	public void FromJsonString(String savedSearchString) throws LogException {
		try {
			JSONObject dict = JSONObject.fromObject(savedSearchString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateSavedSearch", e.getMessage(), e, "");
		}
	}
}
