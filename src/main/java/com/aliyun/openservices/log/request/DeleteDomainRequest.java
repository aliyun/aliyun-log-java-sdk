package com.aliyun.openservices.log.request;

public class DeleteDomainRequest extends Request {

	private static final long serialVersionUID = 2097911173247269635L;
	private String domainName = "";

	public DeleteDomainRequest(String project, String domainName) {
		super(project);
		this.domainName = domainName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
}
