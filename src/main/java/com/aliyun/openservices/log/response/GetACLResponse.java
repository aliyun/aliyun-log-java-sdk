package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.ACL;

public class GetACLResponse extends Response {

	private static final long serialVersionUID = 3805949202678526920L;
	ACL acl = new ACL();

	public GetACLResponse(Map<String, String> headers, ACL acl) {
		super(headers);
		SetACL(acl);
	}

	public ACL GetACL() {
		return acl;
	}

	public void SetACL(ACL acl) {
		this.acl = new ACL(acl);
	}
}
