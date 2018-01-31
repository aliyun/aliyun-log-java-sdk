package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public abstract class LocalFileConfigInputDetail extends CommonConfigInputDetail implements Serializable {
	
	private static final long serialVersionUID = -5807698217939352222L;
	protected String logPath = "";
	protected String logType = "";
	protected String filePattern = "";
	protected String timeFormat = "";
	protected String topicFormat = Consts.CONST_CONFIG_DEFAULT_TOPICFORMAT;
	protected boolean preserve = true;
	protected Integer preserveDepth = 0;
	protected String fileEncoding = Consts.CONST_CONFIG_INPUTDETAIL_FILEENCODING_UTF8;
	protected boolean discardUnmatch = true;
	protected Integer maxDepth = Consts.CONST_CONFIG_INPUTDETAUL_DEFAULTMAXDEPTH;
	protected boolean tailExisted = false; // false means ignore history log content
	protected boolean discardNonUtf8 = false; // false means discard non utf8 content
	protected boolean isDockerFile = false;
	protected Map<String, String> dockerIncludeLabel = new HashMap<String, String>();
	protected Map<String, String> dockerExcludeLabel = new HashMap<String, String>();
	
	public Map<String, String> getDockerIncludeLabel() {
		return dockerIncludeLabel;
	}

	public void setDockerIncludeLabel(Map<String, String> dockerIncludeLabel) {
		this.dockerIncludeLabel = dockerIncludeLabel;
	}

	public Map<String, String> getDockerExcludeLabel() {
		return dockerExcludeLabel;
	}

	public void setDockerExcludeLabel(Map<String, String> dockerExcludeLabel) {
		this.dockerExcludeLabel = dockerExcludeLabel;
	}
	
	public boolean isDockerFile() {
		return isDockerFile;
	}

	public void setDockerFile(boolean isDockerFile) {
		this.isDockerFile = isDockerFile;
	}

	public boolean GetTailExisted() {
		return tailExisted;
	}
	
	public void SetTailExisted(boolean tailExisted) {
		this.tailExisted = tailExisted;
	}
	
	public boolean GetDiscardNonUtf8() {
		return discardNonUtf8;
	}
	
	public void SetDiscardNonUtf8(boolean discardNonUtf8) {
		this.discardNonUtf8 = discardNonUtf8;
	}
	
	public String GetLogType() {
		return logType;
	}
	
	public void SetLogType(String logType) {
		this.logType = logType;
	}
	
	public Integer GetMaxDepth() {
		return maxDepth;
	}

	public void SetMaxDepth(Integer maxDepth) {
		this.maxDepth = maxDepth;
	}

	public boolean GetDiscardUnmatch() {
		return discardUnmatch;
	}
	
	public void SetDiscardUnmatch(boolean discardUnmatch) {
		this.discardUnmatch = discardUnmatch;
	}
	
	public String GetFileEncoding() {
		return fileEncoding;
	}

	public void SetFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

	public boolean GetPreserve() {
		return preserve;
	}
	
	public void SetPreserve(boolean preserve) {
		this.preserve = preserve;
	}
	
	public Integer GetPreserveDepth() {
		return preserveDepth;
	}
	
	public void SetPreserveDepth(Integer preserveDepth) {
		this.preserveDepth = preserveDepth;
	}
	
	public String GetLogPath() {
		return logPath;
	}

	public void SetLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String GetFilePattern() {
		return filePattern;
	}

	public void SetFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}

	public String GetTimeFormat() {
		return timeFormat;
	}

	public void SetTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public String GetTopicFormat() {
		return topicFormat;
	}

	public void SetTopicFormat(String topicFormat) {
		this.topicFormat = topicFormat;
	}
	
	protected void LocalFileConfigToJsonObject(JSONObject jsonObj) {
		CommonConfigToJsonObject(jsonObj);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_LOGPATH, logPath);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_FILEPATTERN, filePattern);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_LOGTYPE, logType);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_TIMEFORMAT, timeFormat);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_FILEENCODING, fileEncoding);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_PRESERVE, preserve);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_PRESERVERDEPTH, preserveDepth);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_TOPICFORMAT, topicFormat);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_MAXDEPTH, maxDepth);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_DISCARDNONUTF8, discardNonUtf8);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_TAILEXISTED, tailExisted);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ISDOCKERFILE, isDockerFile);
		
		JSONObject dockerIncludeLabelJson = new JSONObject();
		for (Map.Entry<String, String> entry : dockerIncludeLabel.entrySet()) {
			dockerIncludeLabelJson.put(entry.getKey(), entry.getValue());
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_DOCKER_INCLUDE_LABEL, dockerIncludeLabelJson);
		
		JSONObject dockerExcludeLabelJson = new JSONObject();
		for (Map.Entry<String, String> entry : dockerExcludeLabel.entrySet()) {
			dockerExcludeLabelJson.put(entry.getKey(), entry.getValue());
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_DOCKER_EXCLUDE_LABEL, dockerExcludeLabelJson);
	}
	
	protected void LocalFileConfigFromJsonObject(JSONObject inputDetail) throws LogException {
		try {
			CommonConfigFromJsonObject(inputDetail);
			this.logPath = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_LOGPATH);
			this.filePattern = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_FILEPATTERN);
			this.logType = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_LOGTYPE);
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_TIMEFORMAT))
				this.timeFormat = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_TIMEFORMAT);
			else
				this.timeFormat = "";
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_FILEENCODING))
				this.fileEncoding = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_FILEENCODING);
			else
				this.fileEncoding = Consts.CONST_CONFIG_INPUTDETAIL_FILEENCODING_UTF8;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_PRESERVE))
				this.preserve = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_PRESERVE);
			else
				this.preserve = true;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_PRESERVERDEPTH))
				this.preserveDepth = inputDetail.getInt(Consts.CONST_CONFIG_INPUTDETAIL_PRESERVERDEPTH);
			else
				this.preserveDepth = 0;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_DISCARDUNMATCH))
				this.discardUnmatch = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_DISCARDUNMATCH);
			else
				this.discardUnmatch = true;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_MAXDEPTH))
				this.maxDepth = inputDetail.getInt(Consts.CONST_CONFIG_INPUTDETAIL_MAXDEPTH);
			else
				this.maxDepth = Consts.CONST_CONFIG_INPUTDETAUL_DEFAULTMAXDEPTH;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_TOPICFORMAT))
				this.topicFormat = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_TOPICFORMAT);
			else
				this.topicFormat = Consts.CONST_CONFIG_DEFAULT_TOPICFORMAT;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_DISCARDNONUTF8))
				this.discardNonUtf8 = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_DISCARDNONUTF8);
			else
				this.discardNonUtf8 = false;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_TAILEXISTED))
				this.tailExisted = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_TAILEXISTED);
			else
				this.tailExisted = false;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_ISDOCKERFILE))
				this.isDockerFile = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_ISDOCKERFILE);
			else 
				this.isDockerFile = false;
			
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_DOCKER_INCLUDE_LABEL)) {
				JSONObject dockerIncludeLabelJson = inputDetail.getJSONObject(Consts.CONST_CONFIG_INPUTDETAIL_DOCKER_INCLUDE_LABEL);
				Iterator<String> sIterator = dockerIncludeLabelJson.keys();  
				while (sIterator.hasNext()) {
					dockerIncludeLabel.put(sIterator.next(), dockerIncludeLabelJson.getString(sIterator.next()));
				}
			}
			
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_DOCKER_EXCLUDE_LABEL)) {
				JSONObject dockerExcludeLabelJson = inputDetail.getJSONObject(Consts.CONST_CONFIG_INPUTDETAIL_DOCKER_EXCLUDE_LABEL);
				Iterator<String> sIterator = dockerExcludeLabelJson.keys();  
				while (sIterator.hasNext()) {
					dockerExcludeLabel.put(sIterator.next(), dockerExcludeLabelJson.getString(sIterator.next()));
				}
			}

		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(), e, "");
		}
	}
}
