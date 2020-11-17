package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class Project implements Serializable {

	/**
	 * project resource
	 */
	private static final long serialVersionUID = 3190120783809426119L;
	protected String projectName = "";
	protected String projectStatus = "";
	protected String projectOwner = "";
	protected String projectDesc = "";
	protected String region = "";
	protected String createTime = "";
	protected String lastModifyTime = "";
	
	public Project() {
		super();
	}
	
	public Project(String projectName, String projectStatus, String projectOwner, String projectDesc, String region,
			String createTime, String lastModifyTime) {
		super();
		this.projectName = projectName;
		this.projectStatus = projectStatus;
		this.projectOwner = projectOwner;
		this.projectDesc = projectDesc;
		this.region = region;
		this.createTime = createTime;
		this.lastModifyTime = lastModifyTime;
	}
	
	public Project(Project project) {
		super();
		this.projectName = project.getProjectName();
		this.projectStatus = project.getProjectStatus();
		this.projectOwner = project.getProjectOwner();
		this.projectDesc = project.getProjectDesc();
		this.region = project.getRegion();
		this.createTime = project.getCreateTime();
		this.lastModifyTime = project.getLastModifyTime();
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectStatus() {
		return projectStatus;
	}
	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}
	public String getProjectOwner() {
		return projectOwner;
	}
	public void setProjectOwner(String projectOwner) {
		this.projectOwner = projectOwner;
	}
	public String getProjectDesc() {
		return projectDesc;
	}
	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	 
	private JSONObject ToRequestJson() {
		JSONObject projectDict = new JSONObject();
		projectDict.put(Consts.CONST_PROJECTNAME, getProjectName());
		projectDict.put(Consts.CONST_PROJECTSTATUS, getProjectStatus());
		projectDict.put(Consts.CONST_PROJECTOWNER, getProjectOwner());
		projectDict.put(Consts.CONST_PROJECTDESC, getProjectDesc());
		projectDict.put(Consts.CONST_PROJECTREGION, getRegion());
		return projectDict;
	}
	
	public String ToRequestString() {
		return ToRequestJson().toString();
	}
	
	public JSONObject ToJsonObject() {
		JSONObject projectDict = ToRequestJson();
		projectDict.put(Consts.CONST_CREATTIME, getCreateTime());
		projectDict.put(Consts.CONST_LASTMODIFYTIME, getLastModifyTime());
		return projectDict;
	}
	
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {		
			setProjectName(dict.getString(Consts.CONST_PROJECTNAME));			
			setProjectDesc(dict.getString(Consts.CONST_PROJECTDESC));
			setProjectOwner(dict.getString(Consts.CONST_PROJECTOWNER));
			setProjectStatus(dict.getString(Consts.CONST_PROJECTSTATUS));
			setRegion(dict.getString(Consts.CONST_PROJECTREGION));
			setCreateTime(dict.getString(Consts.CONST_CREATTIME));
			setLastModifyTime(dict.getString(Consts.CONST_LASTMODIFYTIME));
		} catch (JSONException e) {
			throw new LogException("FailToGenerateProject",  e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String projectString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(projectString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateProject",  e.getMessage(), e, "");
		}
	}
}
