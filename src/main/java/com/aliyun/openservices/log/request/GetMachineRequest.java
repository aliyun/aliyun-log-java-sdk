package com.aliyun.openservices.log.request;

public class GetMachineRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6028800908603879031L;
	protected String uuid = "";
	
	public GetMachineRequest(String uuid) {
		super("");
		this.uuid = uuid;
	}

	public String GetUuid() {
		return uuid;
	}

	public void SetUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
