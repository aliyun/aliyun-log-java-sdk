package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

/**
 * The output config of a logtail
 * @author log-service-dev
 *
 */
public class ConfigOutputDetail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8032121726921069455L;
	private String endpoint = "";
	private String logstoreName = "";
	
	public JSONObject ToJsonObject() {
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("endpoint", endpoint);
		jsonObj.put("logstoreName", logstoreName);
		
		return jsonObj;
	}
	
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject outputDetail) throws LogException {
		try {
			if (outputDetail.containsKey("endpoint")) {
				this.endpoint = outputDetail.getString("endpoint");
			}
			this.logstoreName = outputDetail.getString("logstoreName");
		} catch (JSONException e) {
			throw new LogException("FailToGenerateOutputDetail",
					e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String outputDetailString) throws LogException {
		try {
			JSONObject outputDetail = JSONObject.parseObject(outputDetailString);
			FromJsonObject(outputDetail);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateOutputDetail", e.getMessage(), e, "");
		}
	}
	
	public ConfigOutputDetail() {
	}

	public ConfigOutputDetail(String endpoint, String logstoreName) {
		this.endpoint = endpoint;
		this.logstoreName = logstoreName;
	}
	
	public ConfigOutputDetail(ConfigOutputDetail outputDetail) {
		super();
		this.endpoint = outputDetail.GetEndpoint();
		this.logstoreName = outputDetail.GetLogstoreName();
	}

	public String GetEndpoint() {
		return endpoint;
	}

	public void SetEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String GetLogstoreName() {
		return logstoreName;
	}

	public void SetLogstoreName(String logstoreName) {
		this.logstoreName = logstoreName;
	}
	
	
}
