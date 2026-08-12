package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class GroupAttribute implements Serializable, JsonSerializable, JsonDeserializable {

	private static final long serialVersionUID = -537679331882943768L;
	private String externalName = "";
	private String groupTopic = "";
	
	public GroupAttribute() {
	}
	
	public GroupAttribute(String externalName, String groupTopic) {
		super();
		this.externalName = externalName;
		this.groupTopic = groupTopic;
	}
	
	public GroupAttribute(GroupAttribute groupAttribute) {
		super();
		this.externalName = groupAttribute.GetExternalName();
		this.groupTopic = groupAttribute.GetGroupTopic();
	}
	
	public String GetExternalName() {
		return externalName;
	}
	public void SetExternalName(String externalName) {
		this.externalName = externalName;
	}
	
	public String GetGroupTopic() {
		return groupTopic;
	}
	public void SetGroupTopic(String groupTopic) {
		this.groupTopic = groupTopic;
	}
	
	@InternalApi
	public JSONObject toJsonObject() {
		JSONObject groupAttributeDict = new JSONObject();
		groupAttributeDict.put("groupTopic", GetGroupTopic());
		groupAttributeDict.put("externalName", GetExternalName());
		return groupAttributeDict;
	}
	
	
	@InternalApi
	public void fromJsonObject(JSONObject groupAttribute) throws LogException {
		try {
			this.externalName = groupAttribute.getString("externalName");
			this.groupTopic = groupAttribute.getString("groupTopic");
		} catch (JSONException e) {
			throw new LogException("FailToGenerateGroupAttribute", e.getMessage(), e, "");
		}
	}
	
	public void fromJsonString(String groupAttributeString) throws LogException {
		try {
			JSONObject groupAttribute = JSONObject.parseObject(groupAttributeString);
			fromJsonObject(groupAttribute);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateGroupAttribute", e.getMessage(), e, "");
		}
	}
}
