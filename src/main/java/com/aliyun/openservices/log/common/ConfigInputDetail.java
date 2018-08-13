package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * The input config of a logtail config
 * @author log-service-dev
 *
 */
public class ConfigInputDetail extends LocalFileConfigInputDetail implements Serializable {
	private static final long serialVersionUID = 8699540049365755476L;
	private ArrayList<String> key = new ArrayList<String>();
	private String logBeginRegex = "";
	private String regex = "";
	private String customizedFields = "";

	public ConfigInputDetail() {
		this.logType = Consts.CONST_CONFIG_LOGTYPE_COMMON;
	}
	
	/**
	 * @param logPath
	 *            the log file dir path
	 * @param filePattern
	 *            the file name pattern, e.g "access.log" , "access.log.*"
	 * @param logType
	 *            the log data type , currently it only support
	 *            "common_reg_log", "apsara_log", "streamlog"
	 * @param logBeginRegex
	 *            the regex used to check if one line match the start of a log
	 * @param regex
	 *            the regex used to parse the log data if log type is
	 *            "common_reg_log", leave to "" if log type is "apsara_log"
	 * @param key
	 *            the key lists for every captured value using the defined
	 *            regex, "time" must be one name in key
	 * 
	 * @param timeFormat
	 *            the time format to parse the "time" field
	 * @param localStorage
	 *            true if save the log data if failed to send to the sls server
	 */
	public ConfigInputDetail(String logPath, String filePattern,
			String logType, String logBeginRegex, String regex,
			ArrayList<String> key, String timeFormat, boolean localStorage) {
		super();
		this.logType = Consts.CONST_CONFIG_LOGTYPE_COMMON;
		this.logPath = logPath;
		this.filePattern = filePattern;
		this.logType = logType;
		this.logBeginRegex = logBeginRegex;
		this.regex = regex;
		SetKey(key);
		this.timeFormat = timeFormat;
		this.localStorage = localStorage;
	}

	/**
	 * @param logPath
	 *            the log file dir path
	 * @param filePattern
	 *            the file name pattern, e.g "access.log" , "access.log.*"
	 * @param logType
	 *            the log data type , currently it only support
	 *            "common_reg_log", "apsara_log", "streamlog"
	 * @param logBeginRegex
	 *            the regex used to check if one line match the start of a log
	 * @param regex
	 *            the regex used to parse the log data if log type is
	 *            "common_reg_log", leave to "" if log type is "apsara_log"
	 * @param key
	 *            the key lists for every captured value using the defined
	 *            regex, "time" must be one name in key
	 * 
	 * @param timeFormat
	 *            the time format to parse the "time" field
	 * @param localStorage
	 *            true if save the log data if failed to send to the sls server
	 * @param customizedFields
	 *            the customized fields configuration for special usage
	 */
	public ConfigInputDetail(String logPath, String filePattern,
			String logType, String logBeginRegex, String regex,
			ArrayList<String> key, String timeFormat,
			boolean localStorage, String customizedFields) {
		super();
		this.logType = Consts.CONST_CONFIG_LOGTYPE_COMMON;
		this.logPath = logPath;
		this.filePattern = filePattern;
		this.logType = logType;
		this.logBeginRegex = logBeginRegex;
		this.regex = regex;
		SetKey(key);
		this.timeFormat = timeFormat;
		this.localStorage = localStorage;
		this.customizedFields = customizedFields;
	}
		
	public JSONObject ToJsonObject() {
		JSONObject jsonObj = new JSONObject();
		LocalFileConfigToJsonObject(jsonObj);

		JSONArray keyArray = new JSONArray();
		for (String k : key) {
			keyArray.add(k);
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_KEY, keyArray);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_LOGBEGINREGEX, logBeginRegex);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_REGEX, regex);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS, customizedFields);

		return jsonObj;
	}

	public String ToJsonString() {
		return ToJsonObject().toString();
	}

	public void FromJsonObject(JSONObject inputDetail) throws LogException {
		try {
			LocalFileConfigFromJsonObject(inputDetail);
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_LOGBEGINREGEX))
				this.logBeginRegex = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_LOGBEGINREGEX);
			else
				this.logBeginRegex = ".*";
			this.regex = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_REGEX);
			SetKey(inputDetail.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_KEY));
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS))
				this.customizedFields = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_CUSTOMIZEDFIELDS);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(),
					e, "");
		}
	}

	public void FromJsonString(String inputDetailString) throws LogException {
		try {
			JSONObject inputDetail = JSONObject.fromObject(inputDetailString);
			FromJsonObject(inputDetail);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(),
					e, "");
		}
	}

	public ConfigInputDetail(ConfigInputDetail inputDetail) {
		super();
		this.filePattern = inputDetail.GetFilePattern();
		SetKey(inputDetail.GetKey());
		this.localStorage = inputDetail.GetLocalStorage();
		this.logBeginRegex = inputDetail.GetLogBeginRegex();
		this.logPath = inputDetail.GetLogPath();
		this.logType = inputDetail.GetLogType();
		this.regex = inputDetail.GetRegex();
		this.timeFormat = inputDetail.GetTimeFormat();
		SetFilterKeyRegex(inputDetail.GetFilterKey(), inputDetail.GetFilterRegex());
		this.topicFormat = inputDetail.GetTopicFormat();
		this.customizedFields = inputDetail.customizedFields;
	}

	public ArrayList<String> GetKey() {
		return key;
	}

	public void SetKey(ArrayList<String> key) {
		this.key = new ArrayList<String>(key);
	}

	public void SetKey(JSONArray key) throws LogException {
		try {
			this.key = new ArrayList<String>();
			for (int i = 0; i < key.size(); i++) {
				this.key.add(key.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("FailToSetKey", e.getMessage(), e, "");
		}
	}

	public String GetLogBeginRegex() {
		return logBeginRegex;
	}

	public void SetLogBeginRegex(String logBeginRegex) {
		this.logBeginRegex = logBeginRegex;
	}

	public String GetRegex() {
		return regex;
	}

	public void SetRegex(String regex) {
		this.regex = regex;
	}

	public String GetCustomizedFields() {
		return customizedFields;
        }

	public void SetCustomizedFields(String customizedFields) {
		this.customizedFields = customizedFields;
        }
}
