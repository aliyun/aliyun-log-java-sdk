package com.aliyun.openservices.log.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ListTagResourcesRequest extends Request {
	private static final long serialVersionUID = 3697721439237579690L;
	
	public ListTagResourcesRequest(String resourceType, List<String> resourceIdList, Map<String, String> tagList) {
		super(""); // empty project
		setResourceType(resourceType);
		setResourceIdList(resourceIdList);
		setTagList(tagList);
	}	

	public void setResourceType(String resourceType) {
		SetParam("resourceType", resourceType);
	}
	
	public String getResourceType() {
		return GetParam("resourceType");
	}
	
	public void setResourceIdList(List<String> resourceIdList) {
		// generate json array
		JSONArray resourceIdArray = new JSONArray();
		for (String resourceId : resourceIdList) {
			resourceIdArray.add(resourceId);
		}
		SetParam("resourceId", resourceIdArray.toJSONString());
	}
	
	public void setTagList(Map<String, String> tagList) {
		// generate json 
		JSONArray tagListJson = new JSONArray();
		for (Map.Entry<String, String> entry : tagList.entrySet()) {
			JSONObject tagJson = new JSONObject();
			tagJson.put("key", entry.getKey());
			tagJson.put("value", entry.getValue());
			tagListJson.add(tagJson);
		}
		SetParam("tags", tagListJson.toJSONString());
	}
}
