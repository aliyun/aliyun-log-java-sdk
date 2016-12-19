package com.aliyun.openservices.log.response;

import java.util.Map;

public class CreateACLResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7655451760927369529L;

	String aclId = "";
	
	public CreateACLResponse(Map<String, String> headers, String aclId) {
		super(headers);
		this.aclId = aclId;
	}

	public String GetACLId() {
		return aclId;
	}
}
