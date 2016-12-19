/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.aliyun.openservices.log.common.Consts;

/**
 * The base response class of all sls response
 * 
 * @author sls_dev
 * 
 */
public class Response implements Serializable {
	private static final long serialVersionUID = 7331835262124313824L;
	private Map<String, String> mHeaders = new HashMap<String, String>();

	/**
	 * Construct the base response body with http headers
	 * 
	 * @param headers
	 *            http headers
	 */
	public Response(Map<String, String> headers) {
		SetAllHeaders(headers);
	}

	/**
	 * Get the request id of the response
	 * 
	 * @return request id
	 */
	public String GetRequestId() {
		return GetHeader(Consts.CONST_X_SLS_REQUESTID);
	}

	/**
	 * Get the value of a key in the http response header, if the key is not
	 * found, it will return empty
	 * 
	 * @param key
	 *            key name
	 * @return the value of the key
	 */
	public String GetHeader(String key) {
		if (mHeaders.containsKey(key)) {
			return mHeaders.get(key);
		} else {
			return new String();
		}
	}

	/**
	 * Set http headers
	 * 
	 * @param headers
	 *            http headers
	 */
	private void SetAllHeaders(Map<String, String> headers) {
		mHeaders = new HashMap<String, String>(headers);
	}

	/**
	 * Get all http headers
	 * 
	 * @return http headers
	 */
	public Map<String, String> GetAllHeaders() {
		return mHeaders;
	}

}
