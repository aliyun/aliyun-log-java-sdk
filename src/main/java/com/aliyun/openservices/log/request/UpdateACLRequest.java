package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ACL;
import com.aliyun.openservices.log.common.Consts;

public class UpdateACLRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2903548277414202335L;
	private String logStore = "";
	
	protected ACL acl = new ACL();

	public UpdateACLRequest(String project, ACL acl) {
		super(project);
		SetACL(acl);
		SetParam(Consts.CONST_TYPE, "acl");
	}
	
	public UpdateACLRequest(String project, String logStore, ACL acl) {
		super(project);
		SetLogStore(logStore);
		SetACL(acl);
		SetParam(Consts.CONST_TYPE, "acl");
	}
	
	public ACL GetACL() {
		return acl;
	}

	public void SetACL(ACL acl) {
		this.acl = new ACL(acl);
	}

	public String GetLogStore() {
		return logStore;
	}

	public void SetLogStore(String logStore) {
		this.logStore = logStore;
	}
	
	
}
