package com.aliyun.openservices.log.request;

public class GetACLRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3314521780347712860L;
	protected String ACLId = "";

	public GetACLRequest(String ACLId) {
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
