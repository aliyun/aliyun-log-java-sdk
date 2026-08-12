package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.annotation.InternalApi;

/**
 * The logtail config
 * 
 * @author log-service-dev
 *
 */
public class Config implements Serializable, JsonSerializable, JsonDeserializable {

	private static final long serialVersionUID = -8687635889524799595L;
	protected String logSample = "";
	protected String configName = "";
	protected String inputType;

	protected CommonConfigInputDetail inputDetail;
	protected String outputType = "LogService";
	protected ConfigOutputDetail outputDetail = new ConfigOutputDetail();
	protected int createTime = 0;
	protected int lastModifyTime = 0;

	public Config() {
		this.inputType = Consts.CONST_CONFIG_INPUTTYPE_FILE;
		inputDetail = new ConfigInputDetail();
	}

	public Config(String configName) {
		this.inputType = Consts.CONST_CONFIG_INPUTTYPE_FILE;
		this.configName = configName;
	}

	public Config(String configName, String inputType) {
		this.inputType = inputType;
		this.configName = configName;
		if (inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_STREAMLOG)
				|| inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_SYSLOG)) {
			inputDetail = new StreamLogConfigInputDetail();
		} else if (inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_PLUGIN)) {
			inputDetail = new PluginLogConfigInputDetail();
		} else {
			inputDetail = new ConfigInputDetail();
		}
	}

	public Config(Config config) {
		super();
		this.inputType = config.GetInputType();
		this.configName = config.GetConfigName();
		SetInputDetail(config.GetInputDetail());
		SetOutputDetail(config.GetOutputDetail());
		this.createTime = config.GetCreateTime();
		this.lastModifyTime = config.GetLastModifyTime();
		this.logSample = config.GetLogSample();
	}

	public String GetInputType() {
		return inputType;
	}

	public void SetInputType(String inputType) {
		this.inputType = inputType;
	}

	public String GetLogSample() {
		return logSample;
	}

	public void SetLogSample(String logSample) {
		this.logSample = logSample;
	}

	public String GetConfigName() {
		return configName;
	}

	public void SetConfigName(String configName) {
		this.configName = configName;
	}

	public CommonConfigInputDetail GetInputDetail() {
		return inputDetail;
	}

	public void SetInputDetail(CommonConfigInputDetail inputDetail) {
		this.inputDetail = inputDetail;
	}

	public void SetInputDetail(String inputDetailString) throws LogException {
		inputDetail = CommonConfigInputDetail.fromJsonString(inputType, inputDetailString);
	}

	@InternalApi
	public void SetInputDetail(JSONObject inputDetail) throws LogException {
		this.inputDetail = CommonConfigInputDetail.fromJsonObject(inputType, inputDetail);
	}

	public ConfigOutputDetail GetOutputDetail() {
		return outputDetail;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public void SetOutputDetail(ConfigOutputDetail outputDetail) {
		this.outputDetail = new ConfigOutputDetail(outputDetail);
	}

	public void SetOutputDetail(String outputDetailString) throws LogException {
		this.outputDetail = new ConfigOutputDetail();
		this.outputDetail.fromJsonString(outputDetailString);
	}

	@InternalApi
	public void SetOutputDetail(JSONObject outputDetail) throws LogException {
		this.outputDetail = new ConfigOutputDetail();
		this.outputDetail.fromJsonObject(outputDetail);
	}

	public int GetCreateTime() {
		return createTime;
	}

	public void SetCreateTime(int createTime) {
		this.createTime = createTime;
	}

	public int GetLastModifyTime() {
		return lastModifyTime;
	}

	public void SetLastModifyTime(int lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	private JSONObject toRequestJson() {
		JSONObject configDict = new JSONObject();

		configDict.put("configName", GetConfigName());
		configDict.put("logSample", logSample);
		configDict.put("inputType", inputType);
		configDict.put("inputDetail", GetInputDetail().toJsonObject());
		configDict.put("outputType", outputType);
		configDict.put("outputDetail", GetOutputDetail().toJsonObject());

		return configDict;
	}

	public String toRequestString() {
		return toRequestJson().toString();
	}

	@InternalApi
	public JSONObject toJsonObject() {
		JSONObject configDict = toRequestJson();
		configDict.put("createTime", GetCreateTime());
		configDict.put("lastModifyTime", GetLastModifyTime());
		return configDict;
	}


	@InternalApi
	public void fromJsonObject(JSONObject dict) throws LogException {
		try {
			String configName = dict.getString("configName");

			SetConfigName(configName);
			SetInputType(dict.getString("inputType"));
			if (dict.containsKey("outputType")) {
				outputType = dict.getString("outputType");
			}
			if (dict.containsKey("inputDetail")) {
				SetInputDetail(dict.getJSONObject("inputDetail"));
			}

			if (dict.containsKey("outputDetail")) {
				SetOutputDetail(dict.getJSONObject("outputDetail"));
			}

			if (dict.containsKey("createTime")) {
				SetCreateTime(dict.getIntValue("createTime"));
			}
			if (dict.containsKey("lastModifyTime")) {
				SetLastModifyTime(dict.getIntValue("lastModifyTime"));
			}
			if (dict.containsKey("logSample")) {
				SetLogSample(dict.getString("logSample"));
			}

		} catch (JSONException e) {
			throw new LogException("FailToGenerateConfig", e.getMessage(), e, "");
		}
	}

	public void fromJsonString(String configString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(configString);
			fromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateConfig",  e.getMessage(), e, "");
		}
	}
}
