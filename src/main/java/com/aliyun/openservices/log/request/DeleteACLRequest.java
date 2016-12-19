package com.aliyun.openservices.log.request;

public class DeleteACLRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 680971188929868868L;
	protected String ACLId = "";

	public DeleteACLRequest(String ACLId) {
		super("");
		this.ACLId = ACLId;
	}
	
	public String GetACLId() {
		return ACLId;
	}

	public void SetACLId(String ACLId) {
		this.ACLId = ACLId;
	}
}
