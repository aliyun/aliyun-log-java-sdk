package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ACL;

public class CreateACLRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3052086156124473549L;
	protected ACL acl = new ACL();

	public CreateACLRequest(ACL acl) {
		super("");
		SetACL(acl);
	}
	
	public ACL GetACL() {
		return acl;
	}

	public void SetACL(ACL acl) {
		this.acl = new ACL(acl);
	}
}
