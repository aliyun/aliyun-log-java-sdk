package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

public class EncryptUserCmkConf {
	private String cmk_key_id;
	private String arn;
	private String region_id;

	public EncryptUserCmkConf(String cmk_key_id, String arn, String region_id) {
		this.cmk_key_id = cmk_key_id;
		this.arn = arn;
		this.region_id = region_id;
	}
	public EncryptUserCmkConf()
	{
		
	}

	public String getCmkKeyId() {
		return this.cmk_key_id;
	}

	public void setCmkKeyId(String cmk_key_id) {
		this.cmk_key_id = cmk_key_id;
	}

	public String getArn() {
		return this.arn;
	}

	public void setArn(String arn) {
		this.arn = arn;
	}


	public String getRegionId() {
		return this.region_id;
	}

	public void setRegionId(String region_id) {
		this.region_id = region_id;
	}

	public JSONObject ToJsonObject() {
		JSONObject dict = new JSONObject();
		dict.put("cmk_key_id", this.cmk_key_id);
		dict.put("arn", this.arn);
		dict.put("region_id", this.region_id);
		return dict;
	}

	public String ToJsonString() {
		return ToJsonObject().toString();
	}

	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			setCmkKeyId(dict.getString("cmk_key_id"));
			setArn(dict.getString("arn"));
			setRegionId(dict.getString("region_id"));
		} catch (JSONException e) {
			throw new LogException("The Encrypt User config is invalid", e.getMessage(), e, "");
		}
	}
    public void FromJsonString(String logStoreString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(logStoreString);
            FromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("The Encrypt User config is invalid", e.getMessage(), e, "");
        }
    }
}
