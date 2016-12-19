package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class GetProjectResponse extends Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1938728647331317823L;

	private String mDescription = "";
	private String mStatus = "";

	private String mRegion = "";
	private String mOwner = "";

	public GetProjectResponse(Map<String, String> headers) {
		super(headers);
	}

	public void FromJsonObject(JSONObject obj) throws LogException {
		try {
			mDescription = obj.getString("description");
			mStatus = obj.getString("status");
			mRegion = obj.getString("region");
			mOwner = obj.getString("owner");
		} catch (JSONException e) {
			throw new LogException("InvalidErrorResponse", e.getMessage(),
					GetRequestId());
		}
	}

	public String GetProjectDescription() {
		return mDescription;
	}

	public String GetProjectStatus() {
		return mStatus;
	}

	public String GetProjectRegion() {
		return mRegion;
	}

	public String GetProjectOwner() {
		return mOwner;
	}

}
