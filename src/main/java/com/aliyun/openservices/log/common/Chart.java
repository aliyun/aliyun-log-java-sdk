package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Chart implements Serializable {

	private static final long serialVersionUID = 7056271803253344862L;
	private String title = "";
	private String type = "";
	private String logstore = "";
	private String topic = "";
	private String query = "";
	private String start = "";
	private String end = "";
	private ArrayList<String> xAxisKeys = new ArrayList<String>();
	private ArrayList<String> yAxisKeys = new ArrayList<String>();
	private long xPosition = 0;
	private long yPosition = 0;
	private long width = 0;
	private long height = 0;
	private String displayName = "";
	private String rawDisplayAttr = "";
	
	public String getRawDisplayAttr() {
		return rawDisplayAttr;
	}
	public void setRawDisplayAttr(String rawDisplayAttr) {
		this.rawDisplayAttr = rawDisplayAttr;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public ArrayList<String> getxAxisKeys() {
		return xAxisKeys;
	}
	public void setxAxisKeys(ArrayList<String> xAxisKeys) {
		this.xAxisKeys = new ArrayList<String>();
		for (String keyName : xAxisKeys) {
			this.xAxisKeys.add(keyName);
		}
	}
	public ArrayList<String> getyAxisKeys() {
		return yAxisKeys;
	}
	public void setyAxisKeys(ArrayList<String> yAxisKeys) {
		this.yAxisKeys = new ArrayList<String>();
		for (String keyName : yAxisKeys) {
			this.yAxisKeys.add(keyName);
		}
	}
	public long getxPosition() {
		return xPosition;
	}
	public void setxPosition(long xPosition) {
		this.xPosition = xPosition;
	}
	public long getyPosition() {
		return yPosition;
	}
	public void setyPosition(long yPosition) {
		this.yPosition = yPosition;
	}
	public long getWidth() {
		return width;
	}
	public void setWidth(long width) {
		this.width = width;
	}
	public long getHeight() {
		return height;
	}
	public void setHeight(long height) {
		this.height = height;
	}
	public String getDisplayName() {
		return this.displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Chart() {}
	public Chart(String title, String type, String logstore, String topic, String query, String start, String end,
			ArrayList<String> xAxisKeys, ArrayList<String> yAxisKeys, long xPosition, long yPosition, long width,
			long height, String displayName) {
		super();
		this.title = title;
		this.type = type;
		this.logstore = logstore;
		this.topic = topic;
		this.query = query;
		this.start = start;
		this.end = end;
		setxAxisKeys(xAxisKeys);
		setyAxisKeys(yAxisKeys);
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.width = width;
		this.height = height;
		this.displayName = displayName;
	}
	
	public JSONObject RawDisplayToJsonObject() {
		JSONObject displayJson = new JSONObject();
		
		displayJson.put("xPos", getxPosition());
		displayJson.put("yPos", getyPosition());
		displayJson.put("width", getWidth());
		displayJson.put("height", getHeight());
		displayJson.put("displayName", getDisplayName());
		JSONArray xAxisArray = new JSONArray();
		for (String keyName : getxAxisKeys()) {
			xAxisArray.add(keyName);
		}
		displayJson.put("xAxis", xAxisArray);
		JSONArray yAxisArray = new JSONArray();
		for (String keyName : getyAxisKeys()) {
			yAxisArray.add(keyName);
		}
		// yAxis may be empty
		// insert empty item into it
		if (yAxisArray.size() == 0 && xAxisArray.size() > 0) {
			yAxisArray.add(xAxisArray);
		}
		displayJson.put("yAxis", yAxisArray);

		if (getRawDisplayAttr().length() > 0) {
			displayJson = JSONObject.fromObject(getRawDisplayAttr());
		}
		
		return displayJson;
	}
	
	public JSONObject ToJsonObject() {
		JSONObject chartJson = new JSONObject();
		chartJson.put("title", getTitle());
		chartJson.put("type", getType());
		
		JSONObject searchJson = new JSONObject();
		searchJson.put("logstore", getLogstore());
		searchJson.put("topic", getTopic());
		searchJson.put("query", getQuery());
		searchJson.put("start", getStart());
		searchJson.put("end", getEnd());
		chartJson.put("search", searchJson);
		
		chartJson.put("display", RawDisplayToJsonObject());

		return chartJson;
	}
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {		
			setTitle(dict.getString("title"));
			setType(dict.getString("type"));
			JSONObject searchJson = dict.getJSONObject("search");
			setLogstore(searchJson.getString("logstore"));
			setTopic(searchJson.getString("topic"));
			setQuery(searchJson.getString("query"));
			setStart(searchJson.getString("start"));
			setEnd(searchJson.getString("end"));
			
			// display attribute
			JSONObject displayJson = dict.getJSONObject("display");
			setRawDisplayAttr(displayJson.toString()); // set raw display attribute
			setxPosition(displayJson.getLong("xPos"));
			setyPosition(displayJson.getLong("yPos"));
			setWidth(displayJson.getLong("width"));
			setHeight(displayJson.getLong("height"));
			setDisplayName(displayJson.getString("displayName"));
			
			// xAxis is optional 
			if (displayJson.containsKey("xAxis")) {
				JSONArray xAxisArray = displayJson.getJSONArray("xAxis");
				ArrayList<String> xAxisArrayList = new ArrayList<String>();
				for (int index = 0; index != xAxisArray.size(); index++) {
					xAxisArrayList.add(xAxisArray.getString(index));
				}
				setxAxisKeys(xAxisArrayList);
			}
			
			// yAxis is optional
			if (displayJson.containsKey("yAxis")) {
				ArrayList<String> yAxisArrayList = new ArrayList<String>();
				JSONArray yAxisArray = displayJson.getJSONArray("yAxis");
				for (int index = 0; index != yAxisArray.size(); index++) {
					yAxisArrayList.add(yAxisArray.getString(index));
				}
				setyAxisKeys(yAxisArrayList);
			}
		} catch (JSONException e) {
			throw new LogException("FailedToGenerateChart",  e.getMessage(), e, "");
		}
	}
	public void FromJsonString(String chartString) throws LogException {
		try {
			JSONObject dict = JSONObject.fromObject(chartString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateChart", e.getMessage(), e, "");
		}
	}
}
