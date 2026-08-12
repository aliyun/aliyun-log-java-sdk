package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class OssShipperJsonStorageDetail extends OssShipperStorageDetail implements Serializable {
	private static final long serialVersionUID = -7191203366698052140L;

	public boolean isEnableTag() {
		return enableTag;
	}

	public void setEnableTag(boolean enableTag) {
		this.enableTag = enableTag;
	}

	private boolean enableTag = false;

	OssShipperJsonStorageDetail() {
		setStorageFormat("json");
	}
	
	@Override
	@InternalApi
	public JSONObject toJsonObject() {
		JSONObject obj = new JSONObject();
		JSONObject storage = new JSONObject();
		JSONObject detail = new JSONObject();
		detail.put("enableTag", this.enableTag);
		storage.put("detail", detail);
		storage.put("format", getStorageFormat());
		obj.put("storage", storage);
		return obj;
	}

	@Override
	@InternalApi
	public void fromJsonObject(JSONObject storageDetail) throws LogException {
		setStorageFormat("json");
		if (!storageDetail.containsKey("storage")) {
			this.enableTag = false;
			return;
		}
		JSONObject storage = storageDetail.getJSONObject("storage");
		if (!storage.containsKey("detail")) {
			this.enableTag = false;
			return;
		}
		JSONObject detail = storage.getJSONObject("detail");
		if (!detail.containsKey("enableTag")) {
			this.enableTag = false;
			return;
		}
		try {
			setStorageFormat(storage.getString("format"));
			setEnableTag(detail.getBoolean("enableTag"));
		} catch (JSONException ex) {
			throw new LogException("FailToParseOssShipperJsonStorageDetail",
					ex.getMessage(), ex, "");
		}
	}
}
