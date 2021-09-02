package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.aliyun.openservices.log.exception.LogException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class Dashboard implements Serializable {

	private static final long serialVersionUID = 3152635375534266524L;
	private String dashboardName = "";
    private String description = "";
    private String displayName = "";
    private String attribute = "";
	private ArrayList<Chart> chartList = new ArrayList<Chart>();

	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getDisplayName() { 
		return displayName; 
	}
	public void setDisplayName(String displayName) { 
		this.displayName = displayName; 
	}
	public String getDashboardName() {
		return dashboardName;
	}
	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<Chart> getChartList() {
		return chartList;
	}
	public void setChartList(ArrayList<Chart> chartList) {
		this.chartList = new ArrayList<Chart>();
		for (Chart chart : chartList) {
			this.chartList.add(chart);
		}
	}
	
	public Dashboard() {}
	public Dashboard(String dashboardName, String description, ArrayList<Chart> chartList) {
		super();
		this.dashboardName = dashboardName;
		this.description = description;
		this.chartList = chartList;
	}

	public Dashboard(String dashboardName, String displayName, String description, ArrayList<Chart> chartList) {
		super();
		this.dashboardName = dashboardName;
		this.description = description;
		this.chartList = chartList;
		this.displayName = displayName;
	}
	
	public Dashboard(String dashboardName, String displayName, String description, String attribute, ArrayList<Chart> chartList) {
		super();
		this.dashboardName = dashboardName;
		this.description = description;
		this.chartList = chartList;
		this.displayName = displayName;
		this.attribute = attribute;
	}
	
	public JSONObject ToJsonObject() {
		JSONObject dashboardJson = new JSONObject();
		dashboardJson.put("dashboardName", getDashboardName());
		dashboardJson.put("description", getDescription());
		dashboardJson.put("displayName", getDisplayName());
		
		if (getAttribute().length() > 0) {
			dashboardJson.put("attribute", JSONObject.parseObject(getAttribute()));
		} else {
			dashboardJson.put("attribute", JSONObject.parseObject("{}"));
		}
		
		JSONArray chartArray = new JSONArray();
		for (Chart chart : getChartList()) {
			chartArray.add(chart.ToJsonObject());
		}
		dashboardJson.put("charts", chartArray);
		return dashboardJson;
	}
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			setDashboardName(dict.getString("dashboardName"));
			setDescription(dict.getString("description"));
			// displayName
			if (dict.containsKey("displayName")) {
				setDisplayName(dict.getString("displayName"));
			}
			// attribute
			if (dict.containsKey("attribute")) {
				JSONObject attributeJson = dict.getJSONObject("attribute");
				setAttribute(attributeJson.toString());
			}
			ArrayList<Chart> chartList = new ArrayList<Chart>();
			try {
				JSONArray chartJsonArray = dict.getJSONArray("charts");
				if (chartJsonArray != null) {
					for (int index = 0; index != chartJsonArray.size(); index++) {
						JSONObject jsonObject = chartJsonArray.getJSONObject(index);
						if (jsonObject == null) {
							continue;
						}
						Chart chart = new Chart();
						chart.FromJsonObject(jsonObject);
						chartList.add(chart);
					}
				}
			} catch (JSONException e) {
				// ignore
			}
			setChartList(chartList);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateDashboard",  e.getMessage(), e, "");
		}
	}
	public void FromJsonString(String dashboardString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(dashboardString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateDashboard", e.getMessage(), e, "");
		}
	}
}
